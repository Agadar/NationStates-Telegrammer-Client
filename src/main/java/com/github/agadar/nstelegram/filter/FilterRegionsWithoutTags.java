package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterRegionByTags;
import java.util.Set;

/**
 * Filter for adding/removing nations that are in regions without the supplied
 * tags.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterRegionsWithoutTags extends FilterRegionByTags {

    public FilterRegionsWithoutTags(Set<String> tags, boolean add) {
        super(tags, add);
    }

    @Override
    protected World getWorld() {
        return NSAPI.world(WorldShard.RegionsByTag).regionsWithoutTags(Tags
                .toArray(new String[Tags.size()])).execute();
    }
}
