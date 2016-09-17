package com.github.agadar.nstelegram.event;

import java.util.EventObject;

/**
 * Published by TelegramManager when a recipient was removed from the recipients set.
 *
 * @author Agadar
 */
public class RemovedAddresseeEvent extends EventObject
{
    /** Enum for reasons why the recipient was removed. */
    public enum Reason
    {
        /** The telegram was already sent before to the recipient. */
        AlreadyReceivedBefore,
        
        /** The recipient is currently blocking recruitment telegrams. */
        NotAcceptingRecruitment;
    }
    
    /** The recipient in question. */
    public final String Recipient;
    
    /** The reason why the recipient was removed. */
    public final Reason Reason;
    
    public RemovedAddresseeEvent(Object source, String recipient, Reason reason)
    {
        super(source);
        this.Recipient = recipient;
        this.Reason = reason;
    }
}
