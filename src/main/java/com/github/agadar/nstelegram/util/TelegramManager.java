package com.github.agadar.nstelegram.util;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nstelegram.event.RecipientRemovedEvent;
import com.github.agadar.nstelegram.event.RecipientRemovedEvent.Reason;
import com.github.agadar.nstelegram.event.TelegramManagerListener;
import com.github.agadar.nstelegram.filter.abstractfilter.Filter;
import com.github.agadar.nstelegram.runnable.SendTelegramsRunnable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the recipients list and sending telegrams to the former.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public final class TelegramManager
{
    // User agent string for formatting.
    private final static String USER_AGENT = "Agadar's Telegrammer using Client "
            + "Key '%s' (https://github.com/Agadar/NationStates-Telegrammer)";
    // Duration in milliseconds for timeout when no recipients were found while looping.
    private final static int NO_ADDRESSEES_FOUND_TIMEOUT = 60000;
    
    private final List<Filter> Filters = new ArrayList<>(); // The filters to apply in chronological order.
    private final Set<String> Recipients = new HashSet<>(); // Presumably most up-to-date recipients list, based on Filters.
    private final Map<String, Set<String>> History = new ConcurrentHashMap<>();   // History of recipients, mapped to telegram id's.
    private final Set<TelegramManagerListener> Listeners = new HashSet<>(); // Listeners to events thrown by this.
    private Thread TelegramThread; // The thread on which the TelegramQuery is running.
    
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
        Recipients.clear();
        Filters.forEach((filter) -> { filter.applyFilter(Recipients, localCacheOnly); });
        //removeOldRecipients();
    }
    
    /**
     * Adds new filter.
     * 
     * @param filter
     */
    public void addFilter(Filter filter)
    {
        Filters.add(filter);
        filter.applyFilter(Recipients, false);
        refreshFilters(true);
    }
    
    /**
     * Gives the number of recipients.
     * 
     * @return 
     */
    public int numberOfRecipients()
    {
        return Recipients.size();
    }
    
    /**
     * Gets a copy of the Recipients.
     * 
     * @return 
     */
    public Set<String> getRecipients()
    {
        return new HashSet<>(Recipients);
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
     * Starts sending the telegram to the recipients in a new thread.
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
        
        refreshFilters(true);   // Refresh filters one last time before checking # of recipients
        
        if (numberOfRecipients() == 0)
            throw new IllegalArgumentException("Please supply at least one recipient!"); 
        
        removeOldRecipients();  // Remove old recipients.
        NSAPI.setUserAgent(String.format(USER_AGENT, ClientKey)); // Update user agent.
        
        // Check to make sure the thread is not already running to prevent synchronization issues.
        if (TelegramThread != null && TelegramThread.isAlive())
            throw new IllegalThreadStateException("Telegram thread already running!");
        
        // Prepare thread, then run it.
        TelegramThread = new Thread(new SendTelegramsRunnable(this, Recipients, Listeners,
            NO_ADDRESSEES_FOUND_TIMEOUT, History));       
        TelegramThread.start();
    }
    
    /**
     * Stops sending the telegram to the recipients.
     */
    public void stopSending()
    {
        if (TelegramThread != null)
            TelegramThread.interrupt();
    }
    
    /**
     * Removes old recipients from Recipients. Called right before executing
     * the Telegram Query.
     */
    public void removeOldRecipients()
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
            if (Recipients.remove(oldRecipient))
            {
                final RecipientRemovedEvent event = 
                        new RecipientRemovedEvent(this, oldRecipient, Reason.AlreadyReceivedBefore);

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
}
