package com.github.agadar.nstelegram.filter;

import com.github.agadar.nstelegram.filter.abstractfilter.FilterAdd;
import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.shard.WorldShard;

import java.util.HashSet;
import java.util.Set;

/**
 * Filter for retrieving ALL nations.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterAll extends FilterAdd {

    @Override
    protected Set<String> retrieveNations() {
        // Query local cache, return if it exists.
        if (localCache != null) {
            return localCache;
        }

        // Query global cache, set local cache to it if what we search was found,
        // then return local cache.
        if (GLOBAL_CACHE.All != null) {
            localCache = GLOBAL_CACHE.All;
            return localCache;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, store it in global cache and local cache, then return it.
        final World w = NSAPI.world(WorldShard.Nations).execute();
        GLOBAL_CACHE.All = new HashSet<>(w.Nations);
        localCache = GLOBAL_CACHE.All;
        return localCache;
    }
}
