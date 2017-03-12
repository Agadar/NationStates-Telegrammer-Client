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

    public final X X;
    public final Y Y;

    public Tuple(X x, Y y) {
        this.X = x;
        this.Y = y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.X);
        hash = 53 * hash + Objects.hashCode(this.Y);
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
        if (!Objects.equals(this.X, other.X)) {
            return false;
        }
        return Objects.equals(this.Y, other.Y);
    }
}
