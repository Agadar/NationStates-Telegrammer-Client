package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterRegionByTags;

import java.util.Set;

/**
 * Filter for adding/removing nations that are in regions without the supplied
 * tags.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterRegionsWithoutTags extends FilterRegionByTags {

    public FilterRegionsWithoutTags(Set<RegionTag> tags, boolean add) {
        super(tags, add);
    }

    @Override
    protected World getWorld() {
        return NationStates.world(WorldShard.REGIONS_BY_TAG).regionsWithoutTags(tags
                .toArray(new RegionTag[tags.size()])).execute();
    }
}
