package com.github.agadar.nstelegram;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author marti
 */
public enum AddresseesType
{
    ALL("All"),
    DELEGATES_INCL("Delegates (include)"),
    DELEGATES_EXCL("Delegates (exclude)"),
    NATIONS_INCL("Nations (include)"),
    NATIONS_EXCL("Nations (exclude)"),
    NEW_NATIONS("New Nations"),
    REGIONS_INCL("Regions (include)"),
    REGIONS_EXCL("Regions (exclude)"),
    WA_MEMBERS_INCL("WA Members (include)"),
    WA_MEMBERS_EXCL("WA Members (exclude)"),
    ;
     
    private final static Map<String, AddresseesType> reverse = new HashMap<>();
    
    static
    {
        for (AddresseesType type : AddresseesType.values())
        {
            reverse.put(type.Text, type);
        }
    }
    
    private final String Text;

    private AddresseesType(String text)
    {
        this.Text = text;
    }
    
    public String getText()
    {
        return Text;
    }
    
    /**
     * May return null.
     * 
     * @param text
     * @return 
     */
    public static AddresseesType getViaText(String text)
    {
        return reverse.get(text);
    }
}
