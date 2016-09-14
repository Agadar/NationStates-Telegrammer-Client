package com.github.agadar.nstelegram.filter;

import com.github.agadar.nstelegram.FilterCache;
import java.util.Set;

/**
 * Abstract parent for all filters.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public abstract class FilterAbstract 
{
    /** The cache that is to be used by all child filters. */
    protected final static FilterCache GlobalCache = new FilterCache();
    
    /** Set containing the nations from the last time retrieveNations() was called. */
    protected Set<String> LocalCache;
    
    /**
     * Applies this filter to the supplied set of nations, removing and/or
     * adding nations according to this filter's rules.
     * 
     * @param nations 
     */
    public abstract void applyFilter(Set<String> nations);
    
    /**
     * Accesses either the local cache, the global cache, a daily dump file, 
     * or the server directly, in order to retrieve the nations defined by this 
     * filter. Called by applyFilter(...). Updates the local cache and possibly 
     * also the caches depending on implementation.
     * 
     * @return 
     */
    protected abstract Set<String> retrieveNations();
}
