package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterAddOrRemove;

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
        if (GLOBAL_CACHE.WaMembers != null) {
            nations = GLOBAL_CACHE.WaMembers;
            return;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, then store it in global cache and local cache.
        final WorldAssembly wa = NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Members).execute();
        GLOBAL_CACHE.WaMembers = new HashSet<>(wa.Members);
        nations = GLOBAL_CACHE.WaMembers;
        
        cantRetrieveMoreNations = true;
    }
}
