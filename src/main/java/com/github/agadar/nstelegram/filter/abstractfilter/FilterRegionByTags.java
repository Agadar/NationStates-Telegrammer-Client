package com.github.agadar.nstelegram.filter.abstractfilter;

import static com.github.agadar.nstelegram.filter.abstractfilter.Filter.GLOBAL_CACHE;
import com.github.agadar.nsapi.domain.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract parent for filter for adding/removing nations that are in/not in
 * regions with/without the supplied tags.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class FilterRegionByTags extends FilterAddOrRemove {

    /**
     * This instance's tags.
     */
    protected final Set<String> tags;

    public FilterRegionByTags(Set<String> tags, boolean add) {
        super(add);
        this.tags = tags;
    }

    @Override
    public void refresh() {
        // If we already retrieved data before, just return.
        if (nations != null) {
            return;
        }

        // Query global cache for regions mapped to tags.
        Set<String> regions = GLOBAL_CACHE.getRegionsToTagsWith(tags);

        // If they aren't in the global cache, retrieve from server and cache them.
        if (regions == null) {
            final World w = getWorld();

            // This check need be done because when no regions are found, the API
            // nevertheless sends back one empty string.
            if (w != null && w.RegionsByTag() != null && (w.RegionsByTag().size() != 1
                    || !w.RegionsByTag().get(0).isEmpty())) {
                regions = new HashSet<>(w.RegionsByTag());
            } else {
                regions = new HashSet<>();
            }

            GLOBAL_CACHE.mapRegionsToTagsWith(tags, regions);
        }

        // Query global cache. If a region is not found in the global cache,
        // then (download and) read the daily data dump and query the global cache again.
        nations = new HashSet<>();

        for (String region : regions) {
            Set<String> nationsInRegion = GLOBAL_CACHE.getNationsInRegion(region);   // Check if global cache contains the values.

            if (nationsInRegion == null) {
                GLOBAL_CACHE.importDumpFile();                               // If not, then import dump file.
                nationsInRegion = GLOBAL_CACHE.getNationsInRegion(region);   // Check if it contains it now.

                if (nationsInRegion != null) {  // If it does, then add the nations to local cache.
                    nations.addAll(nationsInRegion);
                }
            } else {
                nations.addAll(nationsInRegion);
            }
        }

        cantRetrieveMoreNations = true;
    }

    /**
     * Child-specific implementation of the World data to retrieve.
     *
     * @return
     */
    protected abstract World getWorld();
}
