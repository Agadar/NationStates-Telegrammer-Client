package com.github.agadar.telegrammer.core.filter.abstractfilter;

import java.util.Set;

/**
 * Filter which, according to the 'Add' value, either REMOVES its containing
 * nations from the address set (Add == false), or ADDS its containing nations
 * to the address set (Add == true), then becomes exhausted and doesn't add any
 * more nations to the address set (if this is an 'ADD') until reset.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class FilterAddOrRemove extends Filter {

    /**
     * Whether to add (true) or remove (false) this filter's nations to/from the
     * address set.
     */
    protected final boolean add;

    public FilterAddOrRemove(boolean add) {
        super();
        this.add = add;
    }

    @Override
    public void applyFilter(Set<String> addresses) {
        // Do proper behavior according to whether this is an 'add' or a 'remove' filter.
        if (add) {
            super.applyFilter(addresses);
        } else {
            // Remove from the address set this filter's nations.
            addresses.removeAll(this.nations);
        }
    }
}
