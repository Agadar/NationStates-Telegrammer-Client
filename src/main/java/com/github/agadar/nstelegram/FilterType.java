package com.github.agadar.nstelegram;

import java.util.HashMap;
import java.util.Map;

/**
 * Addressees types, with corresponding string explanations.
 * 
 * @author marti
 */
public enum FilterType
{
    ALL("All"),//
    DELEGATES_INCL("Delegates (include)"),//
    DELEGATES_EXCL("Delegates (exclude)"),//
    DELEGATES_NEW("Delegates (new)"),
    NATIONS_INCL("Nations (include)"),//
    NATIONS_EXCL("Nations (exclude)"),//
    NATIONS_NEW("Nations (new)"),
    NATIONS_REFOUNDED("Nations (refounded)"),
    NATIONS_EJECTED("Nations (ejected)"),
    REGIONS_INCL("Regions (include)"),
    REGIONS_EXCL("Regions (exclude)"),
    REGIONS_WITH_TAGS_INCL("Regions (include with tags)"),
    REGIONS_WITH_TAGS_EXCL("Regions (exclude with tags)"),
    REGIONS_WO_TAGS_INCL("Regions (include w/o tags)"),
    REGIONS_WO_TAGS_EXCL("Regions (exclude w/o tags)"),
    WA_MEMBERS_INCL("WA Members (include)"),
    WA_MEMBERS_EXCL("WA Members (exclude)"),
    ;
     
    // Map for looking up addressees type via its explanation.
    private final static Map<String, FilterType> reverse = new HashMap<>();
    
    // Static initializer for filling the reverse map.
    static
    {
        for (FilterType type : FilterType.values())
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
    private FilterType(String text)
    {
        this.Text = text;
    }
    
    /**
     * Gets the string explanation.
     * 
     * @return 
     */
    public String getText()
    {
        return Text;
    }
    
    /**
     * Looks up an addressees type via its explanation. May return null.
     * 
     * @param text
     * @return 
     */
    public static FilterType getViaText(String text)
    {
        return reverse.get(text);
    }
    
    /**
     * Returns all string explanations.
     * 
     * @return 
     */
    public static String[] getTexts()
    {
        final FilterType[] vals = values();
        final String[] help = new String[vals.length];
        
        for (int i = 0; i < help.length; i++)
        {
            help[i] = vals[i].getText();
        }
        
        return help;
    }
}
