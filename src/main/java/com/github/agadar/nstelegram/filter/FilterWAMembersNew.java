package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterHappenings;
import java.util.HashSet;

/**
 * Filter for adding new WA member nations to the address set.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterWAMembersNew extends FilterHappenings {

    public FilterWAMembersNew() {
        super(KeyWord.admitted);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final WorldAssembly w = NSAPI.wa(Council.SECURITY_COUNCIL)
                .shards(WAShard.RecentMemberLog).execute();

        // Derive new delegates from happenings, and properly set the local and global caches.
        nations = this.filterHappenings(new HashSet<>(w.RecentMemberLog));
        //System.out.println(localCache); // for debugging only
    }
}
