package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.HapFilter;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterHappeningsFinite;

import java.util.HashSet;

/**
 * Filter for retrieving X ejected nations, where X >= 0.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsEjectedFinite extends FilterHappeningsFinite {

    public FilterNationsEjectedFinite(int amountToRetrieve) {
        super(KeyWord.ejected, amountToRetrieve);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final World w = NSAPI.world(WorldShard.Happenings)
                .happeningsFilter(HapFilter.eject).execute();

        // Derive ejected nations from happenings, and properly set the cache.
        nations = this.filterHappenings(new HashSet<>(w.Happenings));
    }
}
