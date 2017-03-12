package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;

/**
 * Fired by the TelegramManager when it is looping but did not find any valid
 * addressees to send the telegram to.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class NoRecipientsFoundEvent extends EventObject {

    /**
     * Duration of the time-out in milliseconds.
     */
    public final int TimeOut;

    /**
     * @param source
     * @param timeout Duration of the time-out in milliseconds.
     */
    public NoRecipientsFoundEvent(Object source, int timeout) {
        super(source);
        this.TimeOut = timeout;
    }
}
