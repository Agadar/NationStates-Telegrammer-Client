package com.github.agadar.telegrammer.client.viewmodel;

import com.github.agadar.telegrammer.client.event.RefreshEverythingEvent;
import com.github.agadar.telegrammer.client.event.RefreshOutputEvent;

/**
 * Implemented by views that wish to represent the TelegrammerViewModel.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public interface TelegrammerViewModelListener {

    /**
     * Fired when significant changes have occurred in the viewmodel that need to be
     * reflected in the listening view.
     * 
     * @param event The event.
     */
    void onRefreshEverything(RefreshEverythingEvent event);

    /**
     * Fired when the output text has changed, which needs to be reflected in the
     * listening view.
     * 
     * @param event The event.
     */
    void onRefreshOutput(RefreshOutputEvent event);
}
