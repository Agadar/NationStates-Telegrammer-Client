package com.github.agadar.nstelegram.filter.abstractfilter;

import java.util.Set;

/**
 * Abstract parent for filters that always simply add their nations to the address set.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public abstract class FilterAdd extends Filter
{
    @Override
    public void applyFilter(Set<String> nations, boolean localCacheOnly)
    {
        if (!localCacheOnly)
            LocalCache = retrieveNations();
        
        nations.addAll(LocalCache);
    }    
}
