package com.github.agadar.telegrammer.core.manager;

import com.github.agadar.telegrammer.core.enums.TelegramType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Assists in saving and loading the property file for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class PropertiesManager {

    // Standard name for this app's properties file.
    private static PropertiesManager INSTANCE;
    private static final String FILENAME = ".nationstates-telegrammer.properties";
    private static final String DEFAULT_STRING_VAL = "";
    private static final String DEFAULT_BOOL_VAL = "false";

    // Variables loaded from and saved to the properties file.
    public String clientKey;
    public String telegramId;
    public String secretKey;
    public TelegramType lastTelegramType;
    public String fromRegion;
    public boolean dryRun;

    public static PropertiesManager get() {
        if (INSTANCE == null) {
            INSTANCE = new PropertiesManager();
        }
        return INSTANCE;
    }

    private PropertiesManager() {
    }

    /**
     * Saves the application's properties to the file.
     * 
     * @return True if saving succeeded, false otherwise.
     */
    public boolean saveProperties() {
        // Prepare properties object.
        final Properties props = new Properties();
        props.setProperty("clientKey", clientKey == null ? DEFAULT_STRING_VAL : clientKey);
        props.setProperty("telegramId", telegramId == null ? DEFAULT_STRING_VAL : telegramId);
        props.setProperty("secretKey", secretKey == null ? DEFAULT_STRING_VAL : secretKey);
        props.setProperty("telegramType", lastTelegramType != null ? lastTelegramType.name() : TelegramType.NORMAL.name());
        props.setProperty("fromRegion", fromRegion == null ? DEFAULT_STRING_VAL : fromRegion);
        props.setProperty("dryRun", Boolean.toString(dryRun));

        // Save to file.
        try (OutputStream output = new FileOutputStream(FILENAME)) {
            props.store(output, null);
        } catch (IOException io) {
            return false;
        }
        return true;
    }

    /**
     * Loads the application's properties from the file.
     * 
     * @return True if loading succeeded, false otherwise.
     */
    public boolean loadProperties() {
        final Properties props = new Properties();

        // Load from file.
        try (InputStream input = new FileInputStream(FILENAME);) {
            props.load(input);
        } catch (IOException ex) {
            return false;
        }

        // Set variables.
        clientKey = props.getProperty("clientKey", DEFAULT_STRING_VAL);
        telegramId = props.getProperty("telegramId", DEFAULT_STRING_VAL);
        secretKey = props.getProperty("secretKey", DEFAULT_STRING_VAL);
        lastTelegramType = valueOf(TelegramType.class, props.getProperty("telegramType"), TelegramType.NORMAL);
        fromRegion = props.getProperty("fromRegion", DEFAULT_STRING_VAL);
        dryRun = Boolean.valueOf(props.getProperty("dryRun", DEFAULT_BOOL_VAL));
        return true;
    }

    /**
     * Calls Enum.valueOf(...) only instead of throwing an
     * IllegalArgumentException if the specified enum type has no constant with
     * the specified name, it returns the specified default value.
     *
     * @param <T>
     * @param type
     * @param string
     * @param defaultValue
     * @return
     */
    private static <T extends Enum<T>> T valueOf(Class<T> type, String string, T defaultValue) {
        try {
            return Enum.valueOf(type, string);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return defaultValue;
        }
    }
}
