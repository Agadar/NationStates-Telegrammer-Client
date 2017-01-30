package com.github.agadar.nstelegram.filter.abstractfilter;

import java.util.Set;

/**
 * Abstract parent for filters that always simply add their nations to the
 * address set, then becomes exhausted and doesn't add any more nations to
 * the address set until reset.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class FilterAdd extends Filter {

    @Override
    public void applyFilter(Set<String> nations, boolean localCacheOnly) {
        // If this filter is exhausted, don't add anything.
        if (isExhausted()) {
            return;
        }
        
        // If we're not restricted to the local cache, then fill the local cache.
        if (!localCacheOnly) {
            localCache = retrieveNations();
        }
        
        // Fill the address set with our local cache's contents, then exhaust.
        nations.addAll(localCache);
        exhausted = true;
    }
}
