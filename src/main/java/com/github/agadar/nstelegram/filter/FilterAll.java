package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter for retrieving ALL nations.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public class FilterAll extends FilterAdd
{
    @Override
    protected Set<String> retrieveNations()
    {
        // Query local cache.
        if (LocalCache != null)
            return LocalCache;
        
        // Query global cache.
        if (GlobalCache.All != null)
        {
            LocalCache = GlobalCache.All;
            return LocalCache;
        }
        
        // Make API call.
        World w = NSAPI.world(WorldShard.Nations).execute();
        GlobalCache.All = new HashSet<>(w.Nations);
        LocalCache = GlobalCache.All;
        return LocalCache;
    }  
}
