package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterAddOrRemove;

import java.util.HashSet;

/**
 * Filter for adding/removing World Assembly Delegates from the address set.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterDelegates extends FilterAddOrRemove {

    public FilterDelegates(boolean add) {
        super(add);
    }

    @Override
    public void refresh() {
        // If we already retrieved data before, do nothing
        if (nations != null) {
            return;
        }

        // Query global cache, set local cache to it if what we search was found.
        if (GLOBAL_CACHE.delegates != null) {
            nations = GLOBAL_CACHE.delegates;
            return;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, then store it in global cache and local cache.
        final WorldAssembly wa = NationStates.worldAssembly(Council.SECURITY_COUNCIL).shards(WorldAssemblyShard.DELEGATES).execute();
        GLOBAL_CACHE.delegates = new HashSet<>(wa.delegates);
        nations = GLOBAL_CACHE.delegates;
        cantRetrieveMoreNations = true;
    }
}
