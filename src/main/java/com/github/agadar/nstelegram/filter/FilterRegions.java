package com.github.agadar.nstelegram.filter;

import com.github.agadar.nstelegram.filter.abstractfilter.FilterAddOrRemove;
import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.region.Region;
import com.github.agadar.nsapi.enums.shard.RegionShard;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter for adding/removing nations in specified regions from the address
 * list.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterRegions extends FilterAddOrRemove {

    /**
     * This instance's set of regions to retrieve nations from.
     */
    private final Set<String> Regions;

    public FilterRegions(Set<String> regions, boolean add) {
        super(add);
        this.Regions = regions;
    }

    @Override
    protected Set<String> retrieveNations() {
        // Query local cache.
        if (LocalCache != null) {
            return LocalCache;
        }

        // Query global cache. For every region not found in the global cache,
        // retrieve its nations from the server and also update the global cache.
        LocalCache = new HashSet<>();

        for (String region : Regions) {
            // Check if global cache contains the values.
            Set<String> nationsInRegion = GlobalCache.getNationsInRegion(region);

            // If not, retrieve them from the server and also update global cache.
            if (nationsInRegion == null) {
                final Region r = NSAPI.region(region)
                        .shards(RegionShard.NationNames).execute();

                // If region does not exist, just add empty map to global cache.
                if (r == null) {
                    GlobalCache.mapNationsToRegion(region, new HashSet<>());
                } // Else, do proper mapping.
                else {
                    nationsInRegion = new HashSet<>(r.NationNames);
                    LocalCache.addAll(nationsInRegion);
                    GlobalCache.mapNationsToRegion(region, nationsInRegion);
                }
            } else {
                LocalCache.addAll(nationsInRegion);
            }
        }

        return LocalCache;
    }
}
