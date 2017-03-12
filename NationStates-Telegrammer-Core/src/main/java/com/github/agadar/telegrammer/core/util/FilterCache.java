package com.github.agadar.telegrammer.core.util;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.DailyDumpNations;
import com.github.agadar.nationstates.domain.nation.Nation;
import com.github.agadar.nationstates.enumerator.DailyDumpMode;
import com.github.agadar.nationstates.enumerator.RegionTag;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Cache used for reducing the number of calls made to the API.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterCache {

    private final Map<String, Set<String>> NationsToRegions;            // nations mapped to regions
    private final Map<Set<RegionTag>, Set<String>> RegionsToTagsWith;      // regions mapped to tags they have
    private final Map<Set<RegionTag>, Set<String>> RegionsToTagsWithout;   // regions mapped to tags they don't have
    private final Map<String, Set<String>> EmbassiesToRegions;          // embassy regions mapped to regions
    private boolean HasImportedDumpFile = false;                        // indicates whether or not the dump file has been imported yet

    public Set<String> Delegates;   // world assembly delegates
    public Set<String> WaMembers;   // world assembly members
    public Set<String> All;         // all nations

    public FilterCache() {
        NationsToRegions = new HashMap<>();
        RegionsToTagsWith = new HashMap<>();
        RegionsToTagsWithout = new HashMap<>();
        EmbassiesToRegions = new HashMap<>();
    }

    public void importDumpFile() {
        // Only if the dump file hasn't already been imported, import the dump file.
        if (HasImportedDumpFile) {
            return;
        }

        DailyDumpNations ddn;

        try {
            ddn = NationStates.nationdump(DailyDumpMode.READ_LOCAL).execute();
        } catch (Exception ex) {
            // If the exception isn't just a FileNotFoundException, throw this.
            if (ex.getCause().getClass() != FileNotFoundException.class) {
                throw ex;
            }

            // Else, try download the dump file from the server.
            ddn = NationStates.nationdump(DailyDumpMode.DOWNLOAD_THEN_READ_LOCAL).execute();
        }

        // ddn should now be filled. Use it to fill the caches.
        for (Nation n : ddn.nations) {
            mapNationToRegion(n.regionName, n.name);

            switch (n.worldAssemblyStatus) // Using hard-coded strings for now. Replace with enum once implemented in wrapper.
            {
                case MEMBER:
                    if (WaMembers == null) // Instantiate first if set is null.
                    {
                        WaMembers = new HashSet<>();
                    }
                    WaMembers.add(n.name);              // Now add to WA members.
                    break;
                case DELEGATE:
                    if (Delegates == null) // Instantiate first if set is null.
                    {
                        Delegates = new HashSet<>();
                    }
                    Delegates.add(n.name);              // Now add to WA delegates.
                    break;
            }
        }

        HasImportedDumpFile = true;
    }

    /**
     * Maps a single embassy region to a region.
     *
     * @param region
     * @param embassy
     */
    public void mapEmbassyToRegion(String region, String embassy) {
        Set<String> embassies = EmbassiesToRegions.get(region);

        if (embassies == null) {
            embassies = new HashSet<>();
            EmbassiesToRegions.put(region, embassies);
        }

        embassies.add(embassy);
    }

    /**
     * Maps embassy regions to a region.
     *
     * @param region
     * @param embassies
     */
    public void mapEmbassiesToRegion(String region, Set<String> embassies) {
        EmbassiesToRegions.put(region, embassies);
    }

    /**
     * Gets a region's embassy regions.
     *
     * @param region
     * @return
     */
    public Set<String> getEmbassies(String region) {
        return EmbassiesToRegions.get(region);
    }

    /**
     * Maps a single nation to a region.
     *
     * @param region
     * @param nation
     */
    public void mapNationToRegion(String region, String nation) {
        Set<String> nations = NationsToRegions.get(region);

        if (nations == null) {
            nations = new HashSet<>();
            NationsToRegions.put(region, nations);
        }

        nations.add(nation);
    }

    /**
     * Maps nations to a region.
     *
     * @param region
     * @param nations
     */
    public void mapNationsToRegion(String region, Set<String> nations) {
        NationsToRegions.put(region, nations);
    }

    /**
     * Gets nations in a region.
     *
     * @param region
     * @return
     */
    public Set<String> getNationsInRegion(String region) {
        return NationsToRegions.get(region);
    }

    /**
     * Maps regions to tags those regions have.
     *
     * @param tags
     * @param regions
     */
    public void mapRegionsToTagsWith(Set<RegionTag> tags, Set<String> regions) {
        RegionsToTagsWith.put(tags, regions);
    }

    /**
     * Gets regions that have the supplied tags.
     *
     * @param tags
     * @return
     */
    public Set<String> getRegionsToTagsWith(Set<RegionTag> tags) {
        return RegionsToTagsWith.get(tags);
    }

    /**
     * Maps regions to tags those regions DO NOT have.
     *
     * @param tags
     * @param regions
     */
    public void mapRegionsToTagsWithout(Set<RegionTag> tags, Set<String> regions) {
        RegionsToTagsWithout.put(tags, regions);
    }

    /**
     * Gets regions that DO NOT have the supplied tags.
     *
     * @param tags
     * @return
     */
    public Set<String> getRegionsToTagsWithout(Set<RegionTag> tags) {
        return RegionsToTagsWithout.get(tags);
    }
}
