package com.github.agadar.nstelegram.util;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.event.TelegramSentEvent;
import com.github.agadar.nsapi.event.TelegramSentListener;
import com.github.agadar.nsapi.query.TelegramQuery;
import com.github.agadar.nstelegram.event.NoAddresseesEvent;
import com.github.agadar.nstelegram.event.RefreshingRecipientsEvent;
import com.github.agadar.nstelegram.event.RemovedAddresseeEvent;
import com.github.agadar.nstelegram.event.RemovedAddresseeEvent.Reason;
import com.github.agadar.nstelegram.event.StoppedEvent;
import com.github.agadar.nstelegram.event.TelegramManagerListener;
import com.github.agadar.nstelegram.filter.abstractfilter.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the addressees list and sending telegrams to the former.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public final class TelegramManager implements TelegramSentListener
{
    // User agent string for formatting.
    private final static String USER_AGENT = "Agadar's Telegrammer using Client "
            + "Key '%s' (https://github.com/Agadar/NationStates-Telegrammer)";
    // Duration in milliseconds for timeout when no addressees were found while looping.
    private final static int NO_ADDRESSEES_FOUND_TIMEOUT = 60000;
    
    private final List<Filter> Filters = new ArrayList<>(); // The filters to apply in chronological order.
    private final Set<String> Addressees = new HashSet<>(); // Presumably most up-to-date addressees list, based on Filters.
    private final Map<String, Set<String>> History = new ConcurrentHashMap<>();   // History of recipients, mapped to telegram id's.
    private final Set<TelegramManagerListener> Listeners = new HashSet<>(); // Listeners to events thrown by this.
    private Thread telegramThread; // The thread on which the TelegramQuery is running.
    
    // Variables that will be used for sending the telegrams. Should be manually
    // updated by a form or where-ever these values are defined.
    public String ClientKey;
    public String TelegramId;
    public String SecretKey;
    public boolean SendAsRecruitment;
    public boolean IsLooping;
    
    public TelegramManager() 
    {
        // Set user agent for the first time.
        NSAPI.setUserAgent("Agadar's Telegrammer (https://github.com/Agadar/NationStates-Telegrammer)");
    }
    
    /**
     * Refreshes the filters.
     * 
     * @param localCacheOnly if true, explicitly uses the local caches for returning
     * the filters' nations lists instead of allowing the possibility for using 
     * the global cache, daily dump file, or calls to the server.
     */
    public void refreshFilters(boolean localCacheOnly)
    {
        Addressees.clear();
        Filters.forEach((filter) -> { filter.applyFilter(Addressees, localCacheOnly); });
        removeOldRecipients();
    }
    
    /**
     * Adds new filter.
     * 
     * @param filter
     */
    public void addFilter(Filter filter)
    {
        Filters.add(filter);
        filter.applyFilter(Addressees, false);
        refreshFilters(true);
    }
    
    /**
     * Gives the number of addressees.
     * 
     * @return 
     */
    public int numberOfAddressees()
    {
        return Addressees.size();
    }
    
    /**
     * Removes the filter with the given index.
     * 
     * @param index 
     */
    public void removeFilterAt(int index)
    {
        Filters.remove(index);
        refreshFilters(true);
    }
    
    /**
     * Starts sending the telegram to the addressees in a new thread.
     * Throws IllegalArgumentException if the variables are not properly set.
     * 
     */
    public void startSending()
    {
        // Make sure all inputs are valid.
        if (ClientKey == null || ClientKey.isEmpty())
            throw new IllegalArgumentException("Please supply a Client Key!");      
        if (TelegramId == null || TelegramId.isEmpty())
            throw new IllegalArgumentException("Please supply a Telegram Id!");      
        if (SecretKey == null || SecretKey.isEmpty())
            throw new IllegalArgumentException("Please supply a Secret Key!");
        
        refreshFilters(true);   // Refresh filters one last time before checking # of addressees.
        
        if (numberOfAddressees() == 0)
            throw new IllegalArgumentException("Please supply at least one recipient!"); 

        NSAPI.setUserAgent(String.format(USER_AGENT, ClientKey)); // Update user agent.
        
        // Prepare thread, then run it.
        telegramThread = new Thread(() ->
        {
            boolean causedByError = false;
            String errorMsg = null;
            
            try
            {
                do
                {
                    // Prepare query.
                    final TelegramQuery q = NSAPI.telegram(ClientKey, TelegramId, SecretKey, 
                        Addressees.toArray(new String[Addressees.size()]))
                            .addListeners(this).addListeners(this);

                    if (SendAsRecruitment)
                        q.isRecruitment();

                    q.execute(null);    // send the telegrams
                    
                    // If looping, update addressees until there's addressees available.
                    if (IsLooping)
                    {
                        final RefreshingRecipientsEvent refrevent = new RefreshingRecipientsEvent(this);
                        synchronized(Listeners)
                        {
                            // Pass telegram sent event through.
                            Listeners.stream().forEach((tsl) ->
                            {
                                tsl.handleRefreshingRecipients(refrevent);
                            });
                        }
                        
                        refreshFilters(false);
                        
                        while (Addressees.isEmpty() && !Thread.interrupted())
                        {
                            final NoAddresseesEvent event = 
                                    new NoAddresseesEvent(this, NO_ADDRESSEES_FOUND_TIMEOUT);
                            
                            synchronized(Listeners)
                            {
                                // Pass telegram sent event through.
                                Listeners.stream().forEach((tsl) ->
                                {
                                    tsl.handleNoAddresseesEvent(event);
                                });
                            }
                            Thread.sleep(NO_ADDRESSEES_FOUND_TIMEOUT);
                            
                            synchronized(Listeners)
                            {
                                // Pass telegram sent event through.
                                Listeners.stream().forEach((tsl) ->
                                {
                                    tsl.handleRefreshingRecipients(refrevent);
                                });
                            }
                            
                            refreshFilters(false);
                        }
                    }
                } 
                while (IsLooping && !Thread.interrupted());
            }
            catch (InterruptedException ex) { /* Just fall through to finally. */ }
            catch (Exception ex)
            {
                // Dirty solution to not have ratelimiter exceptions show up as legit errors. 
                if (!ex.getMessage().equals("RateLimiter.class blew up!"))
                {
                    causedByError = true;
                    errorMsg = ex.getMessage();
                }
            }
            finally
            {
                final StoppedEvent stoppedEvent = new StoppedEvent(this, 
                        causedByError, errorMsg, 0, 0, 0);               
                Listeners.stream().forEach((tsl) ->
                {
                    tsl.handleStoppedEvent(stoppedEvent);
                });
            }
        });
        
        telegramThread.start();
    }
    
    /**
     * Stops sending the telegram to the addressees.
     */
    public void stopSending()
    {
        if (telegramThread != null)
            telegramThread.interrupt();
    }
    
    /**
     * Removes old recipients from Addressees. Called right before executing
     * the Telegram Query.
     */
    private void removeOldRecipients()
    {
        Set<String> oldRecipients = History.get(TelegramId);
        
        // Create the telegram id entry in history if it doesn't exist yet.
        if (oldRecipients == null)
        {
            oldRecipients = new HashSet<>();
            History.put(TelegramId, oldRecipients);
        }
        
        // Clear old recipients from recipients, publish events for each.
        for (String oldRecipient : oldRecipients)
        {
            if (Addressees.remove(oldRecipient))
            {
                final RemovedAddresseeEvent event = 
                        new RemovedAddresseeEvent(this, oldRecipient, Reason.AlreadyReceivedBefore);

                synchronized(Listeners)
                {
                    // Pass telegram sent event through.
                    Listeners.stream().forEach((tsl) ->
                    {
                        tsl.handleRecipientRemoved(event);
                    });
                }
            }
        }
    }
    
    /**
     * Registers new telegram manager listeners.
     * 
     * @param newlisteners the listeners to register
     */
    public void addListeners(TelegramManagerListener... newlisteners)
    {
        synchronized (Listeners)
        {
            Listeners.addAll(Arrays.asList(newlisteners));
        }
    }
    
    @Override
    public void handleTelegramSent(TelegramSentEvent event)
    {
        // Update the History. We're assuming removeOldRecipients is always
        // called before this and the Telegram Id didn't change in the meantime,
        // so there is no need to make sure the entry for the current Telegram Id
        // changed.       
        if (event.Queued)
            History.get(TelegramId).add(event.Addressee);
        
        synchronized(Listeners)
        {
            // Pass telegram sent event through.
            Listeners.stream().forEach((tsl) ->
            {
                tsl.handleTelegramSent(event);
            });
        }
    }
}
