package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.Filter;

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
        final World w = NSAPI.world(WorldShard.NewestNations).execute();

        // Properly set the local and global caches.
        nations = new HashSet<>(w.NewestNations);
    }

    @Override
    public boolean potentiallyInfinite() {
        return true;
    }
}
