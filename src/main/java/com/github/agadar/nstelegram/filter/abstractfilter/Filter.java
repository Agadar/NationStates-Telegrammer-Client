package com.github.agadar.nstelegram.filter.abstractfilter;

import com.github.agadar.nstelegram.util.FilterCache;
import java.util.Set;

/**
 * Abstract parent for all filters.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class Filter {

    /**
     * The cache that is to be used by all child filters.
     */
    protected final static FilterCache GLOBAL_CACHE = new FilterCache();

    /**
     * Set containing the nations from the last time retrieveNations() was
     * called.
     */
    protected Set<String> localCache;
    
    /**
     * Whether or not this filter has been exhausted.
     */
    protected boolean exhausted = false;

    /**
     * Applies this filter to the supplied set of nations, removing and/or
     * adding nations according to this filter's rules.
     *
     * @param nations
     * @param localCacheOnly if true, explicitly uses the local cache for
     * returning this filter's nations list instead of allowing the possibility
     * for using the global cache, daily dump file, or calls to the server.
     */
    public abstract void applyFilter(Set<String> nations, boolean localCacheOnly);

    /**
     * Accesses either the local cache, the global cache, a daily dump file, or
     * the server directly, in order to retrieve the nations defined by this
     * filter. Called by applyFilter(...). Updates the local cache and possibly
     * also the caches depending on implementation.
     *
     * @return
     */
    protected abstract Set<String> retrieveNations();
    
    /**
     * Whether or not this filter has been exhausted.
     * @return 
     */
    public boolean isExhausted() {
        return exhausted;
    }
    
    /**
     * Resets this filter so that it is no longer exhausted.
     */
    public void reset() {
        exhausted = false;
    }
}
