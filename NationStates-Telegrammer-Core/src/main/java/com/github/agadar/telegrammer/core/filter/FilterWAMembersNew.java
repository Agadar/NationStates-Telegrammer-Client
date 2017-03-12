package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterHappenings;

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
        final WorldAssembly w = NationStates.worldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.RECENT_MEMBER_LOG).execute();

        // Derive new delegates from happenings, and properly set the local and global caches.
        nations = this.filterHappenings(new HashSet<>(w.recentMemberLog));
    }

    @Override
    public boolean potentiallyInfinite() {
        return true;
    }
}
