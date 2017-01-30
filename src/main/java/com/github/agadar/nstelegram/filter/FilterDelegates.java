package com.github.agadar.nstelegram.filter;

import com.github.agadar.nstelegram.filter.abstractfilter.FilterAddOrRemove;
import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;

import java.util.HashSet;
import java.util.Set;

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
    protected Set<String> retrieveNations() {
        // Query local cache, return if it exists.
        if (localCache != null) {
            return localCache;
        }

        // Query global cache, set local cache to it if what we search was found,
        // then return local cache.
        if (GLOBAL_CACHE.Delegates != null) {
            localCache = GLOBAL_CACHE.Delegates;
            return localCache;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, store it in global cache and local cache, then return it.
        final WorldAssembly wa = NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Delegates).execute();
        GLOBAL_CACHE.Delegates = new HashSet<>(wa.Delegates);
        localCache = GLOBAL_CACHE.Delegates;
        return localCache;
    }
}
