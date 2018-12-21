package com.github.agadar.telegrammer.client.viewmodel;

/**
 * Implemented by views that wish to represent the TelegrammerViewModel.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public interface TelegrammerViewModelListener {

    /**
     * Fired when significant changes have occurred in the view-model that need to
     * be reflected in the listening view.
     */
    void refreshEverything();

    /**
     * Fired when the output text has changed, which needs to be reflected in the
     * listening view.
     */
    void refreshOutput();
}
