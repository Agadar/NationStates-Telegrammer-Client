package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterAdd;

import java.util.HashSet;
import java.util.Set;

/**
 * Filter for adding new nations to the address set. Is never exhausted.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsNew extends FilterAdd {

    @Override
    protected Set<String> retrieveNations() {
        // Get fresh new list from server.
        final World w = NSAPI.world(WorldShard.NewestNations).execute();

        // Properly set the local and global caches.
        localCache = new HashSet<>(w.NewestNations);
        return localCache;
    }
    
    @Override
    public boolean isExhausted() {
        return false;
    }
}
