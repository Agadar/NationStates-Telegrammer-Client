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
    
    public Set<String> Delegates;   // world assembly delegates
    public Set<String> WaMembers;   // world assembly members
    public Set<String> All;         // all nations
    
    // These are all continuous sets, usable for infinite loops.
    private final Set<String> NationsNew;
    private final Set<String> NationsEjected;
    private final Set<String> NationsRefounded;
    private final Set<String> DelegatesNew;
    
    public FilterCache()
    {
        NationsToRegions = new HashMap<>();
        RegionsToTagsWith = new HashMap<>();
        RegionsToTagsWithout = new HashMap<>();
        
        NationsNew = new HashSet<>();
        NationsEjected = new HashSet<>();
        NationsRefounded = new HashSet<>();
        DelegatesNew = new HashSet<>();
    }
    
    /**
     * Updates cache for new nations and alters supplied accordingly.
     * 
     * @param nations 
     */
    public void updateNationsNew(Set<String> nations)
    {
        updateContinuousSets(NationsNew, nations);
    }
    
    /**
     * Updates cache for ejected nations and alters supplied accordingly.
     * 
     * @param nations 
     */
    public void updateNationsEjected(Set<String> nations)
    {
        updateContinuousSets(NationsEjected, nations);
    }
    
    /**
     * Updates cache for refounded nations and alters supplied accordingly.
     * 
     * @param nations 
     */
    public void updateNationsRefounded(Set<String> nations)
    {
        updateContinuousSets(NationsRefounded, nations);
    }
    
    /**
     * Updates cache for new delegates and alters supplied accordingly.
     * 
     * @param nations 
     */
    public void updateDelegatesNew(Set<String> nations)
    {
        updateContinuousSets(DelegatesNew, nations);
    }
    
    /**
     * Alters the two supplied sets as such that:
     * 
     * 1. If a string is in cacheSet but not in newSet, it is removed from cacheSet;
     * 2. If a string is in newSet but not in cacheSet, it is added to cacheSet;
     * 3. If a string is in both sets, it is removed from newSet.
     * 
     * @param cacheSet
     * @param newSet 
     */
    private void updateContinuousSets(Set<String> cacheSet, Set<String> newSet)
    {
        final Set<String> newCacheSet = new HashSet<>(newSet);
        newSet.removeAll(cacheSet);
        cacheSet.clear();
        cacheSet.addAll(newCacheSet);
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
