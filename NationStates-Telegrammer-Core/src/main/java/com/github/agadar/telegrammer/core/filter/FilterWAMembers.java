package com.github.agadar.telegrammer.core.filter;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.worldassembly.WorldAssembly;
import com.github.agadar.nationstates.enumerator.Council;
import com.github.agadar.nationstates.shard.WorldAssemblyShard;

import com.github.agadar.telegrammer.core.filter.abstractfilter.FilterAddOrRemove;

import java.util.HashSet;

/**
 * Filter for adding/removing World Assembly members from the address set.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterWAMembers extends FilterAddOrRemove {

    public FilterWAMembers(boolean add) {
        super(add);
    }

    @Override
    public void refresh() {
        // If we already retrieved data before, do nothing..
        if (nations != null) {
            return;
        }

        // Query global cache, set local cache to it if what we search was found.
        if (GLOBAL_CACHE.waMembers != null) {
            nations = GLOBAL_CACHE.waMembers;
            return;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, then store it in global cache and local cache.
        final WorldAssembly wa = NationStates.worldAssembly(Council.SECURITY_COUNCIL).shards(WorldAssemblyShard.MEMBERS).execute();
        GLOBAL_CACHE.waMembers = new HashSet<>(wa.members);
        nations = GLOBAL_CACHE.waMembers;
        cantRetrieveMoreNations = true;
    }
}
