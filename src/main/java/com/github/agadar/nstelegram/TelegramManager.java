package com.github.agadar.nstelegram;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.NationStatesAPIException;
import com.github.agadar.nsapi.event.TelegramSentListener;
import com.github.agadar.nsapi.query.TelegramQuery;
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
    public final static TelegramManager Instance = new TelegramManager(); // Singleton public instance.
    
    private final static String USER_AGENT = "Agadar's Telegrammer using Client Key '%s' (https://github.com/Agadar/NationStates-Telegrammer)";
    private final List<Tuple<Boolean, Set<String>>> Steps = new ArrayList<>(); // List of steps. True = add, false = remove.
    private final Set<String> Addressees = new HashSet<>(); // Presumably most up-to-date addressees list, based on Steps.
    private Thread telegramThread; // The thread on which the TelegramQuery is running on.
    
    private TelegramManager() 
    {
        // Set user agent for the first time.
        NSAPI.setUserAgent("Agadar's Telegrammer (https://github.com/Agadar/NationStates-Telegrammer)");
    }
    
    /**
     * Resets the address list.
     */
    public void resetAddressees()
    {
        Steps.clear();
        updateAddressees();
    }
    
    /**
     * Adds new addressees to the address list.
     * 
     * @param addressees 
     */
    public void addAddressees(Set<String> addressees)
    {
        Steps.add(new Tuple(true, addressees));
        updateAddressees();
    }
    
    /**
     * Removes addressees from the address list.
     * 
     * @param addressees 
     */
    public void removeAddressees(Set<String> addressees)
    {
        Steps.add(new Tuple(false, addressees));
        updateAddressees();
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
     * Removes the step with the given index.
     * 
     * @param index 
     */
    public void removeStep(int index)
    {
        Steps.remove(index);
        updateAddressees();
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
    
    /**
     * Updates the addressees list according to the supplied steps.
     */
    private void updateAddressees()
    {
        Addressees.clear();
        
        Steps.stream().forEach((step) ->
        {
            if (step.x)
            {
                Addressees.addAll(step.y);
            }
            else
            {
                Addressees.removeAll(step.y);
            }
        });
    }
}
