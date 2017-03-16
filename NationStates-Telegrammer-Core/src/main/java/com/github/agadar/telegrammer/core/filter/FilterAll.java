package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.world.World;
import com.github.agadar.nationstates.shard.WorldShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.Filter;

import java.util.HashSet;

/**
 * Filter for retrieving ALL nations.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterAll extends Filter {

    @Override
    public void refresh() {
        // If we already retrieved data before, just return.
        if (nations != null) {
            return;
        }

        // Query global cache, set local cache to it if what we search was found.
        if (GLOBAL_CACHE.all != null) {
            nations = GLOBAL_CACHE.all;
            return;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, then store it in global cache and local cache.
        final World w = NationStates.world(WorldShard.NATIONS).execute();
        GLOBAL_CACHE.all = new HashSet<>(w.nations);
        nations = GLOBAL_CACHE.all;
        cantRetrieveMoreNations = true;
    }
}
