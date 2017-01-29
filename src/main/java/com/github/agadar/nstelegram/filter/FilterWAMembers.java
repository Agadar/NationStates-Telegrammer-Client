package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterAddOrRemove;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter for adding/removing World Assembly members from the address set.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterWAMembers extends FilterAddOrRemove {

    public FilterWAMembers(boolean add) {
        super(add);
    }

    @Override
    protected Set<String> retrieveNations() {
        // Query local cache.
        if (LocalCache != null) {
            return LocalCache;
        }

        // Query global cache.
        if (GlobalCache.WaMembers != null) {
            LocalCache = GlobalCache.WaMembers;
            return LocalCache;
        }

        // Make API call.
        final WorldAssembly wa = NSAPI.wa(Council.SECURITY_COUNCIL).shards(WAShard.Members).execute();
        GlobalCache.WaMembers = new HashSet<>(wa.Members);
        LocalCache = GlobalCache.WaMembers;
        return LocalCache;
    }
}
