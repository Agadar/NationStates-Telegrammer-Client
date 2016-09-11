package com.github.agadar.nstelegram;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.region.Region;
import com.github.agadar.nsapi.domain.shared.Happening;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.HapFilter;
import com.github.agadar.nsapi.enums.shard.RegionShard;
import com.github.agadar.nsapi.enums.shard.WAShard;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Assists in retrieving nation names from the server.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public class FilterHelper 
{
    // Pattern used for extracting nation names from happenings descriptions.
    private final static Pattern PATTERN = Pattern.compile("\\@\\@(.*?)\\@\\@");
    
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
     * @return a list of refounded nations
     */
    public static List<String> refoundedNations()
    {
        final World w = NSAPI.world(WorldShard.Happenings)
                .happeningsFilter(HapFilter.founding).execute();
        return filterHappenings(w.Happenings, "refounded");
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
        WorldAssembly w = NSAPI.wa(Council.SECURITY_COUNCIL)
                .shards(WAShard.RecentHappenings).execute();
        return filterHappenings(w.RecentHappenings, "became");
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
     * From a list of happenings, derives nation names from each happening that
     * contains in its description the supplied string.
     * 
     * @param toApplyTo
     * @return 
     */
    private static List<String> filterHappenings(List<Happening> happenings, String ifContains)
    {       
        final List<String> nations = new ArrayList<>();
        
        happenings.forEach(h -> 
        {
            if (h.Description.contains(ifContains))
            {
                final Matcher matcher = PATTERN.matcher(h.Description);
                
                if (matcher.find())
                {
                    nations.add(matcher.group(1));
                }
            }
        });    
        
        return nations;
    }
}
