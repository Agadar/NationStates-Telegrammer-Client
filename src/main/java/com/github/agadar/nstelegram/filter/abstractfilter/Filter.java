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
    protected Set<String> nations;

    /**
     * Whether or not this filter can not retrieve any new nations by more calls to refresh().
     */
    protected boolean cantRetrieveMoreNations = false;

    /**
     * Applies this filter to the supplied set of nations, removing or adding
     * nations according to this filter's rules.
     *
     * @param addresses
     */
    public void applyFilter(Set<String> addresses) {
        addresses.addAll(nations);
    }

    /**
     * Refreshes this filter. Depending on the implementation, this may mean: -
     * Doing nothing; - Caching data from the global cache; - Caching data from
     * a daily dump file; - Caching data from the server.
     */
    public void refresh() {
        cantRetrieveMoreNations = true;
    }

    /**
     * Whether or not this filter can not retrieve any new nations by more calls to refresh().
     *
     * @return
     */
    public boolean cantRetrieveMoreNations() {
        return cantRetrieveMoreNations;
    }
}
