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
    void handleNoAddresseesEvent(NoAddresseesEvent event);  
    void handleStoppedEvent(StoppedEvent event);
}
