package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.region.Embassy;
import com.github.agadar.nationstates.domain.region.Region;
import com.github.agadar.nationstates.enumerator.EmbassyStatus;
import com.github.agadar.nationstates.shard.RegionShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterAddOrRemove;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filter for adding or removing from the address set nations that are
 * inhabitants of regions that have embassies with any of the specified regions.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterEmbassies extends FilterAddOrRemove {

    private final Set<String> Regions;  // The regions to retrieve embassies from.

    public FilterEmbassies(Set<String> regions, boolean add) {
        super(add);
        this.Regions = regions;
    }

    @Override
    public void refresh() {
        // If we already retrieved data before, do nothing.
        if (nations != null) {
            return;
        }

        final Set<String> embassies = new HashSet<>();  // All embassies of the selected regions.
        Regions.stream().map((region)
                -> // Iterate over regions to fill embassies.
                {
                    // Query global cache for embassies mapped to region.
                    Set<String> currentEmbassies = GLOBAL_CACHE.getEmbassies(region);

                    // If they aren't in the global cache, retrieve from server and cache them.
                    if (currentEmbassies == null) {
                        final Region curRegion = NationStates.region(region).shards(RegionShard.EMBASSIES).execute();

                        // Check copied from FilterRegionByTags.
                        if (curRegion != null && curRegion.embassies != null) {
                            currentEmbassies = new HashSet<>(extractEstablishedEmbassies(curRegion.embassies));
                        } else {
                            currentEmbassies = new HashSet<>();
                        }

                        GLOBAL_CACHE.mapEmbassiesToRegion(region, currentEmbassies);
                        //GLOBAL_CACHE.mapEmbassiesToRegion(region, embassies);
                    }
                    return currentEmbassies;
                }).forEach((currentEmbassies)
                -> {
            embassies.addAll(currentEmbassies); // Add current embassies to total set.
        });

        //System.out.println(embassies);  // print for debug purposes.
        // Query global cache. If a region is not found in the global cache,
        // then (download and) read the daily data dump and query the global cache again.
        nations = new HashSet<>();
        embassies.stream().forEach((region)
                -> {
            Set<String> nationsInRegion = GLOBAL_CACHE.getNationsInRegion(region);   // Check if global cache contains the values.

            if (nationsInRegion == null) {
                GLOBAL_CACHE.importDumpFile();                               // If not, then import dump file.
                nationsInRegion = GLOBAL_CACHE.getNationsInRegion(region);   // Check if it contains it now.

                if (nationsInRegion != null) // If it does, then add the nations to local cache.
                {
                    nations.addAll(nationsInRegion);
                }
            } else {
                nations.addAll(nationsInRegion);
            }
        });

        cantRetrieveMoreNations = true;
    }

    /**
     * Extracts all ESTABLISHED embassies from the given list and returns the
     * names of the regions those extracted embassies are in.
     *
     * @param embassies
     * @return
     */
    private static Set<String> extractEstablishedEmbassies(List<Embassy> embassies) {
        return embassies.stream().filter(embassy -> embassy.status == EmbassyStatus.ESTABLISHED)
                .map(embassy -> embassy.regionName).collect(Collectors.toSet());
    }
}
