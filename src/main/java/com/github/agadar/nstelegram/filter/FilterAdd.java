package com.github.agadar.nstelegram.filter;

import java.util.Set;

/**
 * Abstract parent for filters that always simply add their nations to the address set.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public abstract class FilterAdd extends FilterAbstract
{
    @Override
    public void applyFilter(Set<String> nations)
    {
        LocalCache = retrieveNations();
        nations.addAll(LocalCache);
    }    
}
