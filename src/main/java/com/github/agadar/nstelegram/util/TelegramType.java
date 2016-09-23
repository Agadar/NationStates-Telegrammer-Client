package com.github.agadar.nstelegram.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerator for telegram types. As of writing, the known types are:
 * Normal, Campaign, and Recruitment.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public enum TelegramType 
{
    NORMAL("Normal"),
    CAMPAIGN("Campaign"),
    RECRUITMENT("Recruitment")
    ;
    
    // Map for looking up addressees type via its explanation.
    private final static Map<String, TelegramType> reverse = new HashMap<>();
    
    // Static initializer for filling the reverse map.
    static
    {
        for (TelegramType type : TelegramType.values())
        {
            reverse.put(type.Text, type);
        }
    }
        
    // Explanation.
    private final String Text;

    /**
     * Constructor.
     * 
     * @param text 
     */
    private TelegramType(String text)
    {
        this.Text = text;
    }
    
    @Override
    public String toString()
    {
        return Text;
    }
    
    /**
     * Looks up a telegram type type via its explanation. May return null.
     * 
     * @param text
     * @return 
     */
    public static TelegramType getViaText(String text)
    {
        return reverse.get(text);
    }
}
