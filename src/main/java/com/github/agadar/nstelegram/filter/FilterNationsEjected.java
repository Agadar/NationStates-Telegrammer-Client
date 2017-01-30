package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.HapFilter;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterHappenings;

import java.util.HashSet;
import java.util.Set;

/**
 * Filter for retrieving ejected nations. Is never exhausted.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsEjected extends FilterHappenings {

    public FilterNationsEjected() {
        super(KeyWord.ejected);
    }

    @Override
    protected Set<String> retrieveNations() {
        // Get fresh new list from server.
        final World w = NSAPI.world(WorldShard.Happenings)
                .happeningsFilter(HapFilter.eject).execute();

        // Derive ejected nations from happenings, and properly set the local and global caches.
        localCache = this.filterHappenings(new HashSet<>(w.Happenings));
        return localCache;
    }
    
    @Override
    public boolean isExhausted() {
        return false;
    }
}
