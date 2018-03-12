package com.github.agadar.telegrammer.client.properties;

import com.github.agadar.telegrammer.core.properties.ApplicationProperties;

/**
 * Extends the basic ApplicationProperties, adding new properties.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class TelegrammerClientProperties extends ApplicationProperties {

    public boolean startSendingOnStartup;
    public boolean hideSkippedRecipients;
    public boolean startMinimized;
}
