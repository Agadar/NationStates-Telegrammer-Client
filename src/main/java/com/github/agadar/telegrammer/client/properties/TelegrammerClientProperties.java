package com.github.agadar.telegrammer.client.properties;

import com.github.agadar.telegrammer.core.properties.ApplicationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Extends the basic ApplicationProperties, adding new properties.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
@Getter
@Setter
public class TelegrammerClientProperties extends ApplicationProperties {

    private boolean startSendingOnStartup;
    private boolean hideSkippedRecipients;
    private boolean startMinimized;
}
