package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterHappeningsFinite;

import java.util.HashSet;

/**
 * Filter for retrieving X new delegates, where X >= 0.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterDelegatesNewFinite extends FilterHappeningsFinite {

    public FilterDelegatesNewFinite(int amountToRetrieve) {
        super(KeyWord.became, amountToRetrieve);
    }

    @Override
    public void refresh() {
        // Get fresh new list from server.
        final WorldAssembly w = NationStates.worldAssembly(Council.SECURITY_COUNCIL)
                .shards(WorldAssemblyShard.RECENT_HAPPENINGS).execute();

        // Derive new delegates from happenings, and properly set the local cache.
        nations = this.filterHappenings(new HashSet<>(w.recentHappenings));
    }
}
