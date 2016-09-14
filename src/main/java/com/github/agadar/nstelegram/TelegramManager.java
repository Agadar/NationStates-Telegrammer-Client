package com.github.agadar.nstelegram;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.NationStatesAPIException;
import com.github.agadar.nsapi.event.TelegramSentListener;
import com.github.agadar.nsapi.query.TelegramQuery;
import com.github.agadar.nstelegram.filter.abstractfilter.Filter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the addressees list and sending telegrams to the former.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public final class TelegramManager 
{
    // User agent string for formatting.
    private final static String USER_AGENT = "Agadar's Telegrammer using Client "
            + "Key '%s' (https://github.com/Agadar/NationStates-Telegrammer)";
    
    private final List<Filter> Filters = new ArrayList<>(); // The filters to apply in chronological order.
    private final Set<String> Addressees = new HashSet<>(); // Presumably most up-to-date addressees list, based on Steps.
    private Thread telegramThread; // The thread on which the TelegramQuery is running.
    
    public TelegramManager() 
    {
        // Set user agent for the first time.
        NSAPI.setUserAgent("Agadar's Telegrammer (https://github.com/Agadar/NationStates-Telegrammer)");
    }
    
    /**
     * Resets the filters.
     */
    public void resetFilters()
    {
        Filters.clear();
        Addressees.clear();
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
        Addressees.clear();
        Filters.forEach((filter) -> { filter.applyFilter(Addressees, true); });
    }
    
    /**
     * Starts sending the telegram to the addressees in a new thread.
     * 
     * @param clientKey
     * @param telegramId
     * @param secretKey
     * @param isRecruitment
     * @param listeners
     */
    public void startSending(String clientKey, String telegramId, 
            String secretKey, boolean isRecruitment, TelegramSentListener... listeners)
    {
        // Update user agent.
        NSAPI.setUserAgent(String.format(USER_AGENT, clientKey));

        // Prepare TelegramQuery.
        final TelegramQuery q = NSAPI.telegram(clientKey, telegramId, secretKey, 
                Addressees.toArray(new String[Addressees.size()])).addListeners(listeners);
        
        if (isRecruitment)
        {
            q.isRecruitment();
        }
        
        // Prepare thread, then run it.
        telegramThread = new Thread(() ->
        {
            try
            {
                q.execute(null);
            }
            catch (NationStatesAPIException ex)
            {
                // Ignore error.
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
        {
            telegramThread.interrupt();
        }
    }
}
