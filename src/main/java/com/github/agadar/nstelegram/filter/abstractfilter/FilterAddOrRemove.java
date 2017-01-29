package com.github.agadar.nstelegram.filter.abstractfilter;

import java.util.Set;

/**
 * Filter which, according to the 'Add' value, either REMOVES its containing
 * nations from the address set (Add == false), or ADDS its containing nations
 * to the address set (Add == true).
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class FilterAddOrRemove extends Filter {

    /**
     * Whether to add (true) or remove (false) this filter's nations to/from the
     * address set.
     */
    protected final boolean Add;

    public FilterAddOrRemove(boolean add) {
        super();
        this.Add = add;
    }

    @Override
    public void applyFilter(Set<String> nations, boolean localCacheOnly) {
        if (!localCacheOnly) {
            LocalCache = retrieveNations();
        }

        if (Add) {
            nations.addAll(LocalCache);
        } else {
            nations.removeAll(LocalCache);
        }
    }
}
