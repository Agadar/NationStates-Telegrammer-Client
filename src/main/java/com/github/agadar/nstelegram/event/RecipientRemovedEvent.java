package com.github.agadar.nstelegram.event;

import java.util.EventObject;

/**
 * Published by TelegramManager when a recipient was removed from the recipients set.
 *
 * @author Agadar
 */
public class RecipientRemovedEvent extends EventObject
{
    /** Enum for reasons why the recipient was removed. */
    public enum Reason
    {
        /** The telegram was already sent before to this recipient. */
        PREVIOUS_RECIPIENT,
        
        /** The recipient is currently blocking recruitment telegrams. */
        BLOCKING_RECRUITMENT,
        
        /** The recipient is currently blocking campaign telegrams. */
        BLOCKING_CAMPAIGN,
        
        /** The recipient was not found or doesn't exist. */
        NOT_FOUND
        ;
    }
    
    /** The recipient in question. */
    public final String Recipient;
    
    /** The reason why the recipient was removed. */
    public final Reason Reason;
    
    public RecipientRemovedEvent(Object source, String recipient, Reason reason)
    {
        super(source);
        this.Recipient = recipient;
        this.Reason = reason;
    }
}
