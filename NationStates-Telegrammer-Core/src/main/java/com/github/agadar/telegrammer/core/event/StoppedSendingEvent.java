package com.github.agadar.telegrammer.core.event;

import java.util.EventObject;

/**
 * Fired by TelegramManager when it's stopped sending telegrams.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class StoppedSendingEvent extends EventObject {

    /**
     * True if the manager stopped as result of an error.
     */
    public final boolean CausedByError;
    /**
     * Contains an error message if stopped as result of an error.
     */
    public final String ErrorMsg;
    /**
     * Number of telegrams queued successfully.
     */
    public final int QueuedSucces;
    /**
     * Number of telegrams that failed to queue because the recipient didn't
     * exist.
     */
    public final int RecipientDidntExist;
    /**
     * Number of telegrams that failed to queue because the recipient is
     * blocking telegrams of that type.
     */
    public final int RecipientIsBlocking;
    /**
     * Number of telegrams that failed to queue because of some other reason,
     * such as disconnect.
     */
    public final int DisconnectOrOtherReason;

    public StoppedSendingEvent(Object source, boolean causedByError, String errorMsg,
            int queuedSucces, int recipientDidntExist, int recipientIsBlocking,
            int disconnectOrOtherReason) {
        super(source);
        this.CausedByError = causedByError;
        this.ErrorMsg = errorMsg;
        this.QueuedSucces = queuedSucces;
        this.RecipientDidntExist = recipientDidntExist;
        this.RecipientIsBlocking = recipientIsBlocking;
        this.DisconnectOrOtherReason = disconnectOrOtherReason;
    }
}
