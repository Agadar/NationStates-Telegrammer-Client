package com.github.agadar.nstelegram;

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
    private static final String fileName = "nationstates-telegrammer.properties";
    
    // Variables loaded from and saved to the properties file.
    public String ClientKey;
    public String TelegramId;
    public String SecretKey;
    public boolean IsRecruitment;
    public boolean IsLooping;

    /**
     * Saves the application's properties to the file.
     */
    public void saveProperties()
    {
        // Prepare properties object.
        Properties props = new Properties();
        props.setProperty("ClientKey", ClientKey);
        props.setProperty("TelegramId", TelegramId);
        props.setProperty("SecretKey", SecretKey);
        props.setProperty("IsRecruitment", Boolean.toString(IsRecruitment));
        props.setProperty("IsLooping", Boolean.toString(IsLooping));
        
        // Save to file.
        try (OutputStream output = new FileOutputStream(fileName))
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
        try (InputStream input = new FileInputStream(fileName);)
        {
            props.load(input);
        } 
        catch (IOException ex)
        {
            // Silently ignore this, as this means the properties file simply doesn't exist yet.
            return;
        }
        
        // Set variables.
        ClientKey = props.getProperty("ClientKey");
        TelegramId = props.getProperty("TelegramId");
        SecretKey = props.getProperty("SecretKey");
        IsRecruitment = Boolean.valueOf(props.getProperty("IsRecruitment"));
        IsLooping = Boolean.valueOf(props.getProperty("IsLooping"));
    }
}
