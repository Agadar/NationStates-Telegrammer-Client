package com.github.agadar.nstelegram.filter;

import com.github.agadar.nstelegram.filter.abstractfilter.FilterHappenings;
import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.HapFilter;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter for retrieving refounded nations.
 *
 * @author Agadar
 */
public class FilterNationsRefounded extends FilterHappenings
{
    public FilterNationsRefounded()
    {
        super(KeyWord.refounded);
    }

    @Override
    protected Set<String> retrieveNations()
    {
        // Get fresh new list from server.
        final World w = NSAPI.world(WorldShard.Happenings)
                .happeningsFilter(HapFilter.founding).execute();
        
        // Derive refounded nations from happenings, and properly set the local and global caches.
        LocalCache = this.filterHappenings(new HashSet<>(w.Happenings));
        GlobalCache.updateNationsRefounded(LocalCache);        
        return LocalCache;
    }
}
