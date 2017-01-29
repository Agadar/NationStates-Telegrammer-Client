package com.github.agadar.nstelegram.filter.abstractfilter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.NationStatesAPIException;
import com.github.agadar.nsapi.domain.DailyDumpNations;
import com.github.agadar.nsapi.domain.nation.Nation;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.DailyDumpMode;
import static com.github.agadar.nstelegram.filter.abstractfilter.Filter.GlobalCache;
import java.io.FileNotFoundException;
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
    protected final Set<String> Tags;

    public FilterRegionByTags(Set<String> tags, boolean add) {
        super(add);
        this.Tags = tags;
    }

    @Override
    protected final Set<String> retrieveNations() {
        // Query local cache.
        if (LocalCache != null) {
            return LocalCache;
        }

        // Query global cache for regions mapped to tags.
        Set<String> regions = GlobalCache.getRegionsToTagsWith(Tags);

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

            GlobalCache.mapRegionsToTagsWith(Tags, regions);
        }

        // Query global cache. If a region is not found in the global cache,
        // then (download and) read the daily data dump and query the global cache again.
        LocalCache = new HashSet<>();

        for (String region : regions) {
            Set<String> nationsInRegion = GlobalCache.getNationsInRegion(region);   // Check if global cache contains the values.

            if (nationsInRegion == null) {
                GlobalCache.importDumpFile();                               // If not, then import dump file.
                nationsInRegion = GlobalCache.getNationsInRegion(region);   // Check if it contains it now.

                if (nationsInRegion != null) // If it does, then add the nations to local cache.
                {
                    LocalCache.addAll(nationsInRegion);
                }
            } else {
                LocalCache.addAll(nationsInRegion);
            }
        }

        return LocalCache;
    }

    /**
     * Child-specific implementation of the World data to retrieve.
     *
     * @return
     */
    protected abstract World getWorld();
}
