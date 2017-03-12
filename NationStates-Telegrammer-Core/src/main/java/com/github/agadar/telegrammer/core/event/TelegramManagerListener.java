package com.github.agadar.telegrammer.core.event;

import com.github.agadar.nationstates.event.TelegramSentEvent;

/**
 * Listener for TelegramManager events.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public interface TelegramManagerListener {

    void handleTelegramSent(TelegramSentEvent event);

    void handleNoRecipientsFound(NoRecipientsFoundEvent event);

    void handleStoppedSending(StoppedSendingEvent event);

    void handleRecipientRemoved(RecipientRemovedEvent event);

    void handleRecipientsRefreshed(RecipientsRefreshedEvent event);
}
