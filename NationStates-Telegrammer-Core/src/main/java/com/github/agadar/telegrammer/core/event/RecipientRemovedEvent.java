package com.github.agadar.telegrammer.core.event;

import com.github.agadar.telegrammer.core.enums.SkippedRecipientReason;
import java.util.EventObject;

/**
 * Published by TelegramManager when a recipient was removed from the recipients
 * set.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RecipientRemovedEvent extends EventObject {

    /**
     * The recipient in question.
     */
    public final String Recipient;

    /**
     * The reason why the recipient was removed.
     */
    public final SkippedRecipientReason Reason;

    public RecipientRemovedEvent(Object source, String recipient, SkippedRecipientReason reason) {
        super(source);
        this.Recipient = recipient;
        this.Reason = reason;
    }
}
