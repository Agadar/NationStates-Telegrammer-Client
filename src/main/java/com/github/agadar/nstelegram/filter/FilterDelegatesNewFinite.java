package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterHappenings;

import java.util.HashSet;
import java.util.Set;

/**
 * Filter for retrieving X new delegates, where X > 0.
 * 
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterDelegatesNewFinite extends FilterHappenings {
    
    /** The amount of nations left to retrieve. */
    int amountToRetrieve;   
    
    public FilterDelegatesNewFinite(int amountToRetrieve) {
        super(KeyWord.became);
        this.amountToRetrieve = Math.min(1, amountToRetrieve);
    }
    
    @Override
    public void applyFilter(Set<String> addresses) {
        addresses.addAll(nations);
    }
    
    @Override
    public void refresh() {
        // Back-up old nations.
        Set<String> oldNations = new HashSet<>(nations);
        
        // Get fresh new list from server.
        final WorldAssembly w = NSAPI.wa(Council.SECURITY_COUNCIL)
                .shards(WAShard.RecentHappenings).execute();

        // Derive new delegates from happenings, and properly set the local cache.
        nations = this.filterHappenings(new HashSet<>(w.RecentHappenings));
        
        // Remove from 
        
        // Update amountToRetrieve.
        int spill = amountToRetrieve - nations.size();
        amountToRetrieve -= nations.size();
        
        // Trim local cache to proper size of needed.
        
    }

    @Override
    public boolean cantRetrieveMoreNations() {
        return amountToRetrieve <= 0;
    }
}
