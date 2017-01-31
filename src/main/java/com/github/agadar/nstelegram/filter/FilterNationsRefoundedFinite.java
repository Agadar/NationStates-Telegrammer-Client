package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.HapFilter;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterHappeningsFinite;
import java.util.HashSet;

/**
 * Filter for retrieving X refounded nations, where X >= 0.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterNationsRefoundedFinite extends FilterHappeningsFinite {

    public FilterNationsRefoundedFinite(int amountToRetrieve) {
        super(KeyWord.refounded, amountToRetrieve);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final World w = NSAPI.world(WorldShard.Happenings)
                .happeningsFilter(HapFilter.founding).execute();

        // Derive refounded nations from happenings, and properly set the local and global caches.
        nations = this.filterHappenings(new HashSet<>(w.Happenings));
    }
}
