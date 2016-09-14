package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter for adding/removing World Assembly Delegates from the address set.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public class FilterDelegates extends FilterAddOrRemove
{
    public FilterDelegates(boolean add)
    {
        super(add);
    }
    
    @Override
    protected Set<String> retrieveNations()
    {
        // Query local cache.
        if (LocalCache != null)
            return LocalCache;
        
        // Query global cache.
        if (GlobalCache.Delegates != null)
        {
            LocalCache = GlobalCache.Delegates;
            return LocalCache;
        }
        
        // Make API call.
        WorldAssembly wa = NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Delegates).execute();
        GlobalCache.Delegates = new HashSet<>(wa.Delegates);
        LocalCache = GlobalCache.Delegates;
        return LocalCache;
    }
}
