package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.Filter;

import java.util.HashSet;

/**
 * Filter for retrieving ALL nations.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterAll extends Filter {

    @Override
    public void refresh() {
        // If we already retrieved data before, do nothing.
        if (nations != null) {
            return;
        }

        // Query global cache, set local cache to it if what we search was found.
        if (GLOBAL_CACHE.All != null) {
            nations = GLOBAL_CACHE.All;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, then store it in global cache and local cache.
        final World w = NSAPI.world(WorldShard.Nations).execute();
        GLOBAL_CACHE.All = new HashSet<>(w.Nations);
        nations = GLOBAL_CACHE.All;
        
        cantRetrieveMoreNations = true;
    }
}
