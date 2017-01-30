package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterAddOrRemove;

import java.util.HashSet;
import java.util.Set;

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
    protected Set<String> retrieveNations() {
        // Query local cache, return if it exists.
        if (localCache != null) {
            return localCache;
        }

        // Query global cache, set local cache to it if what we search was found,
        // then return local cache.
        if (GLOBAL_CACHE.WaMembers != null) {
            localCache = GLOBAL_CACHE.WaMembers;
            return localCache;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, store it in global cache and local cache, then return it.
        final WorldAssembly wa = NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Members).execute();
        GLOBAL_CACHE.WaMembers = new HashSet<>(wa.Members);
        localCache = GLOBAL_CACHE.WaMembers;
        return localCache;
    }
}
