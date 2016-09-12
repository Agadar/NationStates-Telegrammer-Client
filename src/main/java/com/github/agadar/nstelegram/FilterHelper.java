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
import java.util.HashSet;
import java.util.Set;
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
    
    // Cache used for reducing API calls.
    private final static FilterCache CACHE = new FilterCache();
    
    /**
     * Returns a list of nations that live in the given regions.
     * 
     * @param regions the regions of which the nations to return
     * @return a list of nations that live in the given regions
     */
    public static Set<String> nationsInRegions(Set<String> regions)
    {
        final Set<String> nations = new HashSet<>();
        
        for (String region : regions)
        {
            // Check if cache already contains the values.
            Set<String> nationsInRegion = CACHE.getNationsInRegion(region);
            
            // If not, make an API call and add the values to the cache.
            if (nationsInRegion == null)
            {
                final Region r = NSAPI.region(region)
                        .shards(RegionShard.NationNames).execute();
                
                if (r == null || r.NationNames == null)
                {
                    CACHE.mapNationsToRegion(region, new HashSet<>());
                }
                else
                {
                    nationsInRegion = new HashSet<>(r.NationNames);
                    nations.addAll(nationsInRegion);
                    CACHE.mapNationsToRegion(region, nationsInRegion);
                }
            }
            else
            {
                nations.addAll(nationsInRegion);
            }
        }

        return nations;
    }
    
    /**
     * Gives all nations in regions that have all of the supplied tags.
     * 
     * @param tags
     * @return 
     */
    public static Set<String> nationsInRegionsWithTags(Set<String> tags)
    {
        // Check if cache already contains the values.
        Set<String> regions = CACHE.getRegionsToTagsWith(tags);

        // If not, make an API call and add the values to the cache.
        if (regions == null)
        {
            final World w = NSAPI.world(WorldShard.RegionsByTag).regionsWithTags(
                tags.toArray(new String[tags.size()])).execute();

            // This check need be done because when no regions are found, the API
            // nevertheless sends back one empty string.
            if (w != null && w.RegionsByTag() != null && (w.RegionsByTag().size() != 1 || 
                    !w.RegionsByTag().get(0).isEmpty()))
            {
                regions = new HashSet<>(w.RegionsByTag());
            }
            else
            {
                regions = new HashSet<>();
            }
            
            CACHE.mapRegionsToTagsWith(tags, regions);
        }
        
        // Find corresponding nations and return.
        return nationsInRegions(regions);
    }
    
    /**
     * Gives all nations in regions that DO NOT have all of the supplied tags.
     * 
     * @param tags
     * @return 
     */
    public static Set<String> nationsInRegionsWithoutTags(Set<String> tags)
    {
        // Check if cache already contains the values.
        Set<String> regions = CACHE.getRegionsToTagsWithout(tags);

        // If not, make an API call and add the values to the cache.
        if (regions == null)
        {
            final World w = NSAPI.world(WorldShard.RegionsByTag).regionsWithoutTags(
                tags.toArray(new String[tags.size()])).execute();

            // This check need be done because when no regions are found, the API
            // nevertheless sends back one empty string.
            if (w != null && w.RegionsByTag() != null && (w.RegionsByTag().size() != 1 || 
                    !w.RegionsByTag().get(0).isEmpty()))
            {
                regions = new HashSet<>(w.RegionsByTag());
            }
            else
            {
                regions = new HashSet<>();
            }
            
            CACHE.mapRegionsToTagsWithout(tags, regions);
        }
        
        // Find corresponding nations and return.
        return nationsInRegions(regions);
    }
    
    /**
     * Gives all nations that have recently been ejected from a region.
     * 
     * @return 
     */
    public static Set<String> ejectedNations()
    {
        final World w = NSAPI.world(WorldShard.Happenings)
                .happeningsFilter(HapFilter.eject).execute();
        return filterHappenings(new HashSet<>(w.Happenings), "ejected");
    }
    
    /**
     * Returns a list of new nations.
     * 
     * @return a list of new nations
     */
    public static Set<String> newNations()
    {
        return new HashSet<>(NSAPI.world(WorldShard.NewestNations).execute().NewestNations);
    }
    
    /**
     * Returns a list of refounded nations.
     * 
     * @return a list of refounded nations
     */
    public static Set<String> refoundedNations()
    {
        final World w = NSAPI.world(WorldShard.Happenings)
                .happeningsFilter(HapFilter.founding).execute();
        return filterHappenings(new HashSet<>(w.Happenings), "refounded");
    }
    
    /**
     * Returns a list of nations that are new delegates.
     * 
     * @return a list of nations that are new delegates
     */
    public static Set<String> newDelegates()
    {
        WorldAssembly w = NSAPI.wa(Council.SECURITY_COUNCIL)
                .shards(WAShard.RecentHappenings).execute();
        return filterHappenings(new HashSet<>(w.RecentHappenings), "became");
    }
    
    /**
     * Returns a list of all delegate nations.
     * 
     * @return a list of all delegate nations
     */
    public static Set<String> delegates()
    {
        return new HashSet<>(NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Delegates).execute().Delegates);
    }
    
    /**
     * Returns a list of all nations that are World Assembly members.
     * 
     * @return a list of all nations that are World Assembly members.
     */
    public static Set<String> worldAssemblyMembers()
    {
        return new HashSet<>(NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Members).execute().Members);
    }
    
    /**
     * Returns a list of all nations in the world.
     * 
     * @return a list of all nations in the world.
     */
    public static Set<String> allNations()
    {
        return new HashSet<>(NSAPI.world(WorldShard.Nations).execute().Nations);
    }
    
    /**
     * From a list of happenings, derives nation names from each happening that
     * contains in its description the supplied string.
     * 
     * @param toApplyTo
     * @return 
     */
    private static Set<String> filterHappenings(Set<Happening> happenings, String ifContains)
    {       
        final Set<String> nations = new HashSet<>();
        
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
