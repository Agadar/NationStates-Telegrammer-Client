package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.region.Embassy;
import com.github.agadar.nsapi.domain.region.Region;
import com.github.agadar.nsapi.enums.shard.RegionShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterAddOrRemove;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 *
 * @author Agadar
 */
public class FilterEmbassies extends FilterAddOrRemove
{
    private final Set<String> Regions;  // The regions to retrieve embassies from.
    
    public FilterEmbassies(Set<String> regions, boolean add)
    {
        super(add);
        this.Regions = regions;
    }

    @Override
    protected Set<String> retrieveNations()
    {
        // Query local cache.
        if (LocalCache != null)
            return LocalCache;
        
        final Set<String> embassies = new HashSet<>();  // All embassies of the selected regions.
        Regions.stream().map((region) ->                // Iterate over regions to fill embassies.
        {
            // Query global cache for embassies mapped to region.
            Set<String> currentEmbassies = GlobalCache.getEmbassies(region);
            
            // If they aren't in the global cache, retrieve from server and cache them.
            if (currentEmbassies == null)
            {
                final Region curRegion = NSAPI.region(region).shards(RegionShard.Embassies).execute();
                
                // Check copied from FilterRegionByTags.
                if (curRegion != null && curRegion.Embassies != null)
                {
                    currentEmbassies = new HashSet<>(extractEstablishedEmbassies(curRegion.Embassies));
                }
                else
                {
                    currentEmbassies = new HashSet<>();
                }

                GlobalCache.mapEmbassiesToRegion(region, embassies);
            }
            return currentEmbassies;            
        }).forEach((currentEmbassies) ->
        {
            embassies.addAll(currentEmbassies); // Add current embassies to total set.
        });
        
        System.out.println(embassies);  // print for debug purposes.

        // Query global cache. If a region is not found in the global cache,
        // then (download and) read the daily data dump and query the global cache again.
        LocalCache = new HashSet<>();        
        embassies.stream().forEach((region) ->
        {
            Set<String> nationsInRegion = GlobalCache.getNationsInRegion(region);   // Check if global cache contains the values.
            
            if (nationsInRegion == null)
            {
                GlobalCache.importDumpFile();                               // If not, then import dump file.
                nationsInRegion = GlobalCache.getNationsInRegion(region);   // Check if it contains it now.
                
                if (nationsInRegion != null)                                // If it does, then add the nations to local cache.
                    LocalCache.addAll(nationsInRegion);               
            }
            else
            {
                LocalCache.addAll(nationsInRegion);
            }
        });
        
        return LocalCache;
    }

    /**
     * Extracts all ESTABLISHED embassies from the given list and returns the
     * names of the regions those extracted embassies are in.
     * 
     * @param embassies
     * @return 
     */
    private static Set<String> extractEstablishedEmbassies(List<Embassy> embassies)
    {
        return embassies.stream().filter(embassy -> embassy.Status == null)
                .map(embassy -> embassy.RegionName).collect(Collectors.toSet());
    }   
}
