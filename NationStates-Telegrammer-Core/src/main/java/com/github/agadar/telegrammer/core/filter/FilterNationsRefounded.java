package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.enumerator.HappeningsFilter;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterHappenings;

import java.util.HashSet;

/**
 * Filter for retrieving refounded nations.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsRefounded extends FilterHappenings {

    public FilterNationsRefounded() {
        super(KeyWord.refounded);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final World w = NationStates.world(WorldShard.HAPPENINGS)
                .happeningsFilter(HappeningsFilter.FOUNDING).execute();

        // Derive refounded nations from happenings, and properly set the local and global caches.
        nations = this.filterHappenings(new HashSet<>(w.happenings));
    }

    @Override
    public boolean potentiallyInfinite() {
        return true;
    }
}
