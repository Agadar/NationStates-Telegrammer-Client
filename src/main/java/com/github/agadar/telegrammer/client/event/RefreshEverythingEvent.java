package com.github.agadar.telegrammer.client.event;

import java.util.EventObject;

import com.github.agadar.telegrammer.client.viewmodel.TelegrammerViewModel;

/**
 * Thrown by {@link TelegrammerViewModel} when everything needs to be refreshed.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */

public class RefreshEverythingEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    public RefreshEverythingEvent(Object source) {
        super(source);
    }

}
