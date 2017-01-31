package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.wa.WorldAssembly;
import com.github.agadar.nsapi.enums.Council;
import com.github.agadar.nsapi.enums.shard.WAShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterHappenings;
import com.github.agadar.nstelegram.manager.TelegramManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Filter for retrieving X new delegates, where X > 0.
 * 
 * @author Agadar (https://github.com/Agadar/)
 */
public class FilterDelegatesNewFinite extends FilterHappenings {
    
    /** The original amount of nations to retrieve. */
    final int orgAmountToRetrieve;
    
    /** The amount of nations left to retrieve. */
    int amountToRetrieve;   
    
    public FilterDelegatesNewFinite(int amountToRetrieve) {
        super(KeyWord.became);
        this.amountToRetrieve = Math.max(1, amountToRetrieve);
        this.orgAmountToRetrieve = amountToRetrieve;
    }
    
    @Override
    public void applyFilter(Set<String> addresses) {
        Set<String> copy = new HashSet<>(nations);
        TelegramManager.get().removeOldRecipients(copy);
        int diff = amountToRetrieve - copy.size();
        amountToRetrieve = Math.max(0, diff);

        // If amountToRetrieve is negative, trim the set with the difference.
        for (Iterator<String> it = copy.iterator(); it.hasNext() && diff < 0; diff++) {
            it.next();
            it.remove();
        }       
        addresses.addAll(copy);
    }
    
    @Override
    public void refresh() {
        // Get fresh new list from server.
        final WorldAssembly w = NSAPI.wa(Council.SECURITY_COUNCIL)
                .shards(WAShard.RecentHappenings).execute();

        // Derive new delegates from happenings, and properly set the local cache.
        nations = this.filterHappenings(new HashSet<>(w.RecentHappenings));        
    }

    @Override
    public boolean cantRetrieveMoreNations() {
        return amountToRetrieve < 1;
    }
    
    @Override
    public void reset() {
        amountToRetrieve = orgAmountToRetrieve;
    }
}
