package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.HapFilter;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterHappenings;

import java.util.HashSet;

/**
 * Filter for retrieving ejected nations.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsEjected extends FilterHappenings {

    public FilterNationsEjected() {
        super(KeyWord.ejected);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final World w = NSAPI.world(WorldShard.Happenings)
                .happeningsFilter(HapFilter.eject).execute();

        // Derive ejected nations from happenings, and properly set the cache.
        nations = this.filterHappenings(new HashSet<>(w.Happenings));
    }

    @Override
    public boolean potentiallyInfinite() {
        return true;
    }
}
