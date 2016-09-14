package com.github.agadar.nstelegram.filter;

import com.github.agadar.nstelegram.filter.abstractfilter.FilterAddOrRemove;
import java.util.Set;

/**
 * Filter for adding/removing individual nations from address list.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public class FilterNations extends FilterAddOrRemove
{
    public FilterNations(Set<String> nations, boolean add)
    {
        super(add);
        this.LocalCache = nations;
    }
    
    @Override
    protected Set<String> retrieveNations()
    {
        return LocalCache;
    }
}
