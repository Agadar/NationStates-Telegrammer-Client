package com.github.agadar.nstelegram.event;

import java.util.EventObject;

/**
 * Published by TelegramManager when the recipients set is refreshed for the
 * next loop.
 *
 * @author Agadar
 */
public class RefreshingRecipientsEvent extends EventObject
{
    public RefreshingRecipientsEvent(Object source)
    {
        super(source);
    }
}
