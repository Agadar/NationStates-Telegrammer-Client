package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterHappenings;

import java.util.HashSet;
import java.util.Set;

/**
 * Filter for retrieving new delegates. Is never exhausted.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterDelegatesNew extends FilterHappenings {

    public FilterDelegatesNew() {
        super(KeyWord.became);
    }

    @Override
    protected Set<String> retrieveNations() {
        // Get fresh new list from server.
        final WorldAssembly w = NSAPI.wa(Council.SECURITY_COUNCIL)
                .shards(WAShard.RecentHappenings).execute();

        // Derive new delegates from happenings, and properly set the local cache.
        localCache = this.filterHappenings(new HashSet<>(w.RecentHappenings));
        return localCache;
    }

    @Override
    public boolean isExhausted() {
        return false;
    }
}
