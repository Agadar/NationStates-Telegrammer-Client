package com.github.agadar.nstelegram;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.region.Region;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.RegionShard;
import com.github.agadar.nsapi.enums.shard.WAShard;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nsapi.event.TelegramSentListener;
import com.github.agadar.nsapi.query.TelegramQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages sending telegrams.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public final class TelegramManager 
{
    // Singleton public instance.
    public final static TelegramManager Instance = new TelegramManager();   
    // List of addresses.
    public final Set<String> Addressees = new HashSet<>();
    // The thread on which the TelegramQuery is running on.
    private Thread telegramThread;
    
    private TelegramManager() { }
    
    /**
     * Resets the address list.
     */
    public void resetAddressees()
    {
        Addressees.clear();
    }
    
    /**
     * Adds new addressees to the address list.
     * 
     * @param addressees 
     */
    public void addAddressees(Collection<String> addressees)
    {
        this.Addressees.addAll(addressees);
    }
    
    /**
     * Removes addressees from the address list.
     * 
     * @param addressees 
     */
    public void removeAddressees(Collection<String> addressees)
    {
        this.Addressees.removeAll(addressees);
    }
    
    public int numberOfAddressees()
    {
        return this.Addressees.size();
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
            catch (RuntimeException ex)
            {
                System.out.println("Shit's fucked, yo. \n");
                ex.printStackTrace();
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
     * Returns a list of nations that live in the given region.
     * 
     * @param region the region of which the nations to return
     * @return a list of nations that live in the given region
     */
    public static List<String> nationsInRegion(String region)
    {
        Region r = NSAPI.region(region).shards(RegionShard.NationNames).execute();
        return r != null ? r.NationNames : new ArrayList<>();
    }
    
    /**
     * Returns a list of new nations.
     * 
     * @return a list of new nations
     */
    public static List<String> newNations()
    {
        return NSAPI.world(WorldShard.NewestNations).execute().NewestNations;
    }
    
    /**
     * Returns a list of refounded nations.
     * 
     * NOTE: It is currently unsupported by the NationStates API to retrieve a 
     * list of refounded nations, so this only throws UnsupportedOperationException.
     * 
     * @return a list of refounded nations
     */
    public static List<String> refoundedNations()
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a list of nations that are new delegates.
     * 
     * NOTE: It is currently unsupported by the NationStates API to retrieve a 
     * list of new delegates, so this only throws UnsupportedOperationException.
     * 
     * @return a list of nations that are new delegates
     */
    public static List<String> newDelegates()
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a list of all delegate nations.
     * 
     * @return a list of all delegate nations
     */
    public static List<String> delegates()
    {
        return NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Delegates).execute().Delegates;
    }
    
    /**
     * Returns a list of all nations that are World Assembly members.
     * 
     * @return a list of all nations that are World Assembly members.
     */
    public static List<String> worldAssemblyMembers()
    {
        return NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Members).execute().Members;
    }
    
    /**
     * Returns a list of all nations in the world.
     * 
     * @return a list of all nations in the world.
     */
    public static List<String> allNations()
    {
        return NSAPI.world(WorldShard.Nations).execute().Nations;
    }
    
    /**
     * Converts a comma-separated string to a list of strings.
     * 
     * @param string
     * @return 
     */
    public static List<String> stringToStringList(String string)
    {
        return Arrays.asList(string.trim().split("\\s*,\\s*"));
    }
}
