package com.github.agadar.telegrammer.client.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The keys of the telegrammer client specific settings.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
@RequiredArgsConstructor
@Getter
public enum ClientSettingsKeys {

    START_SENDING_ON_STARTUP("startSendingOnStartup"),
    HIDE_SKIPPED_RECIPIENTS("hideSkippedRecipients"),
    START_MINIMIZED("startMinimized");

    private final String key;
}
