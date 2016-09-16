package com.github.agadar.nstelegram.event;

import java.util.EventObject;

/**
 * Fired by TelegramManager when it's stopped sending telegrams.
 *
 * @author Agadar
 */
public class StoppedEvent extends EventObject
{
    /** True if the manager stopped as result of an error. */
    public final boolean CausedByError;
    /** Contains an error message if stopped as result of an error. */
    public final String ErrorMsg;
    /** Number of telegrams queued successfully. */
    public final int QueuedSucces;
    /** Number of telegrams that failed to queue. */
    public final int QueuedFailed;
    /** Number of telegrams that were never queued. */
    public final int QueuedNot;
    
    public StoppedEvent(Object source, boolean causedByError, String errorMsg,
            int queuedSucces, int queuedFailed, int queuedNot)
    {
        super(source);
        this.CausedByError = causedByError;
        this.ErrorMsg = errorMsg;
        this.QueuedSucces = queuedSucces;
        this.QueuedFailed = queuedFailed;
        this.QueuedNot = queuedNot;
    }
}
