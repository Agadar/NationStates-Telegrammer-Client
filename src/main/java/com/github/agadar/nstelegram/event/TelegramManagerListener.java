package com.github.agadar.nstelegram.event;

import com.github.agadar.nsapi.event.TelegramSentEvent;

/**
 * Listener for TelegramManager events.
 * 
 * @author marti
 */
public interface TelegramManagerListener
{
    void handleTelegramSent(TelegramSentEvent event);    
    void handleNoRecipientsFound(NoRecipientsFoundEvent event);  
    void handleStoppedSending(StoppedSendingEvent event);
    void handleRecipientRemoved(RecipientRemovedEvent event);
    void handleRecipientsRefreshed(RecipientsRefreshedEvent event);
}
