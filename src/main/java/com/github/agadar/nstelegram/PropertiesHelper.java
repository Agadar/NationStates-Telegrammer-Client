package com.github.agadar.nstelegram;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Assists in saving and loading property files.
 *
 * @author Agadar
 */
public class PropertiesHelper
{
    private static final String fileName = "nationstates-telegrammer.properties";

    public static void saveProperties(Properties props)
    {
        try (OutputStream output = new FileOutputStream(fileName))
        {
            props.store(output, null);
        } 
        catch (IOException io)
        {
            // Silently ignore this, as saving properties is only optional anyway.
        }
    }

    public static Properties loadProperties()
    {
        Properties props = new Properties();

        try (InputStream input = new FileInputStream(fileName);)
        {
            props.load(input);
        } 
        catch (IOException ex)
        {
            // Silently ignore this, as this means the properties file simply doesn't exist yet.
        }
        
        return props;
    }
}
