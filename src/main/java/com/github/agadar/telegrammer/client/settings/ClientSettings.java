package com.github.agadar.telegrammer.client.settings;

import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.HIDE_SKIPPED_RECIPIENTS;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.START_MINIMIZED;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.START_SENDING_ON_STARTUP;

import com.github.agadar.telegrammer.core.settings.Settings;

import lombok.NonNull;

/**
 * Application settings specific to the telegrammer client, wrapping around a
 * {@link Settings} instance.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class ClientSettings {

    private final Settings settings;

    /**
     * Constructor.
     * 
     * @param settings For registering and getting/setting telegrammer client
     *                 settings.
     */
    public ClientSettings(@NonNull Settings settings) {
        this.settings = settings;
        settings.addBooleanSetting(START_SENDING_ON_STARTUP.getKey(), false);
        settings.addBooleanSetting(HIDE_SKIPPED_RECIPIENTS.getKey(), false);
        settings.addBooleanSetting(START_MINIMIZED.getKey(), false);
    }

    public void setStartSendingOnStartup(boolean startSendingOnStartup) {
        settings.setValue(START_SENDING_ON_STARTUP.getKey(), startSendingOnStartup);
    }

    public boolean getStartSendingOnStartup() {
        return settings.getValue(START_SENDING_ON_STARTUP.getKey(), Boolean.class);
    }

    public void setHideSkippedRecipients(boolean hideSkippedRecipients) {
        settings.setValue(HIDE_SKIPPED_RECIPIENTS.getKey(), hideSkippedRecipients);
    }

    public boolean getHideSkippedRecipients() {
        return settings.getValue(HIDE_SKIPPED_RECIPIENTS.getKey(), Boolean.class);
    }

    public void setStartMinimized(boolean startMinimized) {
        settings.setValue(START_MINIMIZED.getKey(), startMinimized);
    }

    public boolean getStartMinimized() {
        return settings.getValue(START_MINIMIZED.getKey(), Boolean.class);
    }
}
