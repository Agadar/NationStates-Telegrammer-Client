package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.Filter;

import java.util.HashSet;

/**
 * Filter for adding new nations to the address set. Is never exhausted.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsNew extends Filter {

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final World w = NationStates.world(WorldShard.NEWEST_NATIONS).execute();

        // Properly set the local and global caches.
        nations = new HashSet<>(w.newestNations);
    }

    @Override
    public boolean potentiallyInfinite() {
        return true;
    }
}
