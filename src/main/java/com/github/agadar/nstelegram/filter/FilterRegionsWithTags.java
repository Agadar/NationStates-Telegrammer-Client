package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterRegionByTags;

import java.util.Set;

/**
 * Filter for adding/removing nations that are in regions with the supplied
 * tags.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterRegionsWithTags extends FilterRegionByTags {

    public FilterRegionsWithTags(Set<String> tags, boolean add) {
        super(tags, add);
    }

    @Override
    protected World getWorld() {
        return NSAPI.world(WorldShard.RegionsByTag).regionsWithTags(tags
                .toArray(new String[tags.size()])).execute();
    }
}
