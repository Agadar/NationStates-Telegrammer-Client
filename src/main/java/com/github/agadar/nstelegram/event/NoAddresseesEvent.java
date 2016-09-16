package com.github.agadar.nstelegram.event;

import java.util.EventObject;

/**
 * Fired by the TelegramManager when it is looping but did not find any valid
 * addressees to send the telegram to.
 *
 * @author Agadar
 */
public class NoAddresseesEvent extends EventObject
{
    /** Duration of the time-out in milliseconds. */
    public final int TimeOut;
    
    /**
     * @param source
     * @param timeout Duration of the time-out in milliseconds.
     */
    public NoAddresseesEvent(Object source, int timeout)
    {
        super(source);
        this.TimeOut = timeout;
    }
}
