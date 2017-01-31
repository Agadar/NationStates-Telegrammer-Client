package com.github.agadar.nstelegram.filter;

import com.github.agadar.nstelegram.filter.abstractfilter.FilterAddOrRemove;
import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;

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
        if (GLOBAL_CACHE.Delegates != null) {
            nations = GLOBAL_CACHE.Delegates;
            return;
        }

        // If global cache does not contain what we need, do an API call to
        // retrieve the data, then store it in global cache and local cache.
        final WorldAssembly wa = NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Delegates).execute();
        GLOBAL_CACHE.Delegates = new HashSet<>(wa.Delegates);
        nations = GLOBAL_CACHE.Delegates;
        
        cantRetrieveMoreNations = true;
    }
}
