package com.github.agadar.telegrammer.client.properties;

import java.util.Properties;

import com.github.agadar.telegrammer.core.properties.manager.AbstractPropertiesManager;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslator;

/**
 * Manages the telegrammer client's properties.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class TelegrammerClientPropertiesManager extends AbstractPropertiesManager<TelegrammerClientProperties> {

    public TelegrammerClientPropertiesManager(RecipientsListBuilderTranslator builderTranslator,
            String propertiesFileName) {
        super(builderTranslator, propertiesFileName);
    }

    @Override
    protected TelegrammerClientProperties createApplicationProperties() {
        return new TelegrammerClientProperties();
    }

    @Override
    protected void setApplicationPropertiesFromProperties(TelegrammerClientProperties target, Properties source) {
        super.setApplicationPropertiesFromProperties(target, source);
        target.setHideSkippedRecipients(
                Boolean.valueOf(source.getProperty("hideSkippedRecipients", defaultBooleanValue)));
        target.setStartSendingOnStartup(
                Boolean.valueOf(source.getProperty("startSendingOnStartup", defaultBooleanValue)));
        target.setStartMinimized(Boolean.valueOf(source.getProperty("startMinimized", defaultBooleanValue)));
    }

    @Override
    protected void setPropertiesFromApplicationProperties(Properties target, TelegrammerClientProperties source) {
        super.setPropertiesFromApplicationProperties(target, source);
        target.setProperty("hideSkippedRecipients", Boolean.toString(source.isHideSkippedRecipients()));
        target.setProperty("startSendingOnStartup", Boolean.toString(source.isStartSendingOnStartup()));
        target.setProperty("startMinimized", Boolean.toString(source.isStartMinimized()));
    }

}
