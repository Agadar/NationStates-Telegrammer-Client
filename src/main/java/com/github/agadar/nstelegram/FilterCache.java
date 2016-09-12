package com.github.agadar.nstelegram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Cache used for reducing the number of calls made to the API.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public class FilterCache 
{
    private final Map<String, Set<String>> NationsToRegions;       // nations mapped to regions
    private final Map<Set<String>, Set<String>> RegionsToTagsWith;      // regions mapped to tags they have
    private final Map<Set<String>, Set<String>> RegionsToTagsWithout;   // regions mapped to tags they don't have
    private final Set<String> Delegates;   // world assembly delegates
    private final Set<String> WaMembers;   // world assembly members
    private final Set<String> All;         // all nations
    
    public FilterCache()
    {
        NationsToRegions = new HashMap<>();
        RegionsToTagsWith = new HashMap<>();
        RegionsToTagsWithout = new HashMap<>();
        Delegates = new HashSet<>();
        WaMembers = new HashSet<>();
        All = new HashSet<>();
    }
    
    /**
     * Maps nations to a region.
     * 
     * @param region
     * @param nations 
     */
    public void mapNationsToRegion(String region, Set<String> nations)
    {
        NationsToRegions.put(region, nations);
    }
    
    /**
     * Gets nations in a region.
     * 
     * @param region
     * @return 
     */
    public Set<String> getNationsInRegion(String region)
    {
        return NationsToRegions.get(region);
    }
    
    /**
     * Maps regions to tags those regions have.
     * 
     * @param tags
     * @param regions 
     */
    public void mapRegionsToTagsWith(Set<String> tags, Set<String> regions)
    {
        RegionsToTagsWith.put(tags, regions);
    }
    
    /**
     * Gets regions that have the supplied tags.
     * 
     * @param tags
     * @return 
     */
    public Set<String> getRegionsToTagsWith(Set<String> tags)
    {
        return RegionsToTagsWith.get(tags);
    }
    
    /**
     * Maps regions to tags those regions DO NOT have.
     * 
     * @param tags
     * @param regions 
     */
    public void mapRegionsToTagsWithout(Set<String> tags, Set<String> regions)
    {
        RegionsToTagsWithout.put(tags, regions);
    }
   
    /**
     * Gets regions that DO NOT have the supplied tags.
     * 
     * @param tags
     * @return 
     */
    public Set<String> getRegionsToTagsWithout(Set<String> tags)
    {
        return RegionsToTagsWithout.get(tags);
    }
}
