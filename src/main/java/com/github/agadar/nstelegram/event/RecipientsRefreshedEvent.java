package com.github.agadar.nstelegram.event;

import java.util.EventObject;

/**
 * Published by TelegramManager when the recipients set is refreshed for the
 * next loop.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RecipientsRefreshedEvent extends EventObject {

    public RecipientsRefreshedEvent(Object source) {
        super(source);
    }
}
