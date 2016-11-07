package com.github.agadar.nstelegram.manager;

import com.github.agadar.nstelegram.enums.TelegramType;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Assists in saving and loading the property file for this application.
 *
 * @author Agadar
 */
public class PropertiesManager
{
    // Standard name for this app's properties file.
    private static final String FILENAME = "nationstates-telegrammer.properties";
    private static final String DEFAULT_STRING_VAL = "";
    private static final String DEFAULT_BOOL_VAL = "false";
    
    // Variables loaded from and saved to the properties file.
    public String ClientKey;
    public String TelegramId;
    public String SecretKey;
    public TelegramType LastTelegramType;
    public String FromRegion;
    public boolean IsLooping;
    public boolean NoDuplicateTGs;

    /**
     * Saves the application's properties to the file.
     */
    public void saveProperties()
    {
        // Prepare properties object.
        Properties props = new Properties();
        props.setProperty("ClientKey", ClientKey == null ? DEFAULT_STRING_VAL: ClientKey);
        props.setProperty("TelegramId", TelegramId == null ? DEFAULT_STRING_VAL : TelegramId);
        props.setProperty("SecretKey", SecretKey == null ? DEFAULT_STRING_VAL : SecretKey);
        props.setProperty("TelegramType", LastTelegramType != null ? LastTelegramType.name() : TelegramType.NORMAL.name());
        props.setProperty("FromRegion", FromRegion == null ? DEFAULT_STRING_VAL : FromRegion);
        props.setProperty("IsLooping", Boolean.toString(IsLooping));
        props.setProperty("NoDuplicateTGs", Boolean.toString(NoDuplicateTGs));
        
        // Save to file.
        try (OutputStream output = new FileOutputStream(FILENAME))
        {
            props.store(output, null);
        } 
        catch (IOException io)
        {
            // Silently ignore this, as saving properties is only optional anyway.
        }
    }

    /**
     * Loads the application's properties from the file.
     */
    public void loadProperties()
    {
        Properties props = new Properties();

        // Load from file.
        try (InputStream input = new FileInputStream(FILENAME);)
        {
            props.load(input);
        } 
        catch (IOException ex)
        {
            // Properties file doesn't exist yet. We silently ignore this.
        }
        
        // Set variables.
        ClientKey = props.getProperty("ClientKey", DEFAULT_STRING_VAL);
        TelegramId = props.getProperty("TelegramId", DEFAULT_STRING_VAL);
        SecretKey = props.getProperty("SecretKey", DEFAULT_STRING_VAL);
        LastTelegramType = valueOf(TelegramType.class, props.getProperty("TelegramType"), TelegramType.NORMAL);
        FromRegion = props.getProperty("FromRegion", DEFAULT_STRING_VAL);
        IsLooping = Boolean.valueOf(props.getProperty("IsLooping", DEFAULT_BOOL_VAL));
        NoDuplicateTGs = Boolean.valueOf(props.getProperty("NoDuplicateTGs", DEFAULT_BOOL_VAL));
    }
    
    /**
     * Calls Enum.valueOf(...) only instead of throwing an IllegalArgumentException
     * if the specified enum type has no constant with the specified name, it
     * returns the specified default value.
     * 
     * @param <T>
     * @param type
     * @param string
     * @param defaultValue
     * @return 
     */
    private static <T extends Enum<T>> T valueOf(Class<T> type, String string, T defaultValue)
    {
        try
        {
            return Enum.valueOf(type, string);
        }
        catch (IllegalArgumentException | NullPointerException ex)
        {
            return defaultValue;
        }
    }
}
