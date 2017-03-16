package com.github.agadar.telegrammer.core.util;

import java.util.Objects;

/**
 * Simple tuple implementation. Used as composite primary key for mapping
 * Reasons to Telegram Id + Recipient Name combinations.
 *
 * @author Agadar (https://github.com/Agadar/)
 * @param <X>
 * @param <Y>
 */
public class Tuple<X, Y> {

    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.x);
        hash = 53 * hash + Objects.hashCode(this.y);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tuple<?, ?> other = (Tuple<?, ?>) obj;
        if (!Objects.equals(this.x, other.x)) {
            return false;
        }
        return Objects.equals(this.y, other.y);
    }
}
