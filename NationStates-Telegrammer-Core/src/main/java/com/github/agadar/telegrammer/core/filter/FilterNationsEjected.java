package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.HappeningsFilter;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterHappenings;

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
        final World w = NationStates.world(WorldShard.HAPPENINGS)
                .happeningsFilter(HappeningsFilter.EJECT).execute();

        // Derive ejected nations from happenings, and properly set the cache.
        nations = this.filterHappenings(new HashSet<>(w.happenings));
    }

    @Override
    public boolean potentiallyInfinite() {
        return true;
    }
}
