package com.github.agadar.nstelegram.filter.abstractfilter;

import java.util.Set;

/**
 * Filter which, according to the 'Add' value, either REMOVES its containing
 * nations from the address set (Add == false), or ADDS its containing nations
 * to the address set (Add == true), then becomes exhausted and doesn't add 
 * any more nations to the address set (if this is an 'ADD') until reset.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class FilterAddOrRemove extends Filter {

    /**
     * Whether to add (true) or remove (false) this filter's nations to/from the
     * address set.
     */
    protected final boolean add;

    public FilterAddOrRemove(boolean add) {
        super();
        this.add = add;
    }

    @Override
    public void applyFilter(Set<String> nations, boolean localCacheOnly) {
        // Do proper behavior according to whether this is an 'add' or a 'remove' filter.
        if (add) {
            // If this filter is exhausted, don't add anything.
            if (isExhausted()) {
                return;
            }
            
            // If we're not restricted to the local cache, then fill the local cache.
            if (!localCacheOnly) {
                localCache = retrieveNations();
            }
            
            // Fill the address set with our local cache's contents.
            nations.addAll(localCache);
        } else {
            // If we're not restricted to the local cache, then fill the local cache.
            if (!localCacheOnly) {
                localCache = retrieveNations();
            }
            
            // Remove from the address set this filter's nations.
            nations.removeAll(localCache);
        }       
        // Exhaust this filter.
        exhausted = true;
    }
}
