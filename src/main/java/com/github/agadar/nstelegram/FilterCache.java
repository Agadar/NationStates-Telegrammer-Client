package com.github.agadar.nstelegram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    private final Map<String, Set<String>> RegionsToTagsWith;      // regions mapped to tags they have
    private final Map<String, Set<String>> RegionsToTagsWithout;   // regions mapped to tags they don't have
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
        for (String tag : tags)
        {
            final Set<String> regionsInTag = RegionsToTagsWith.get(tag);
            
            if (regionsInTag == null)
            {
                RegionsToTagsWith.put(tag, regions);
            }
            else
            {
                regionsInTag.addAll(regions);
            }
            
            
        }
    }
    
    public List<String> getRegionsToTagsWith(Set<String> tags)
    {
        return null;
    }
    
    /**
     * Maps regions to a tag those regions do NOT have.
     * 
     * @param tag
     * @param regions 
     */
    public void mapRegionsToTagsWithout(String tag, Set<String> regions)
    {
        RegionsToTagsWithout.put(tag, regions);
    }
   
}
