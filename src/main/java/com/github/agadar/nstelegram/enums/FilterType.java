package com.github.agadar.nstelegram.enums;

/**
 * Filter types, with corresponding String explanations.
 * 
 * @author marti
 */
public enum FilterType
{
    ALL("All"),
    DELEGATES_INCL("Delegates (include)"),
    DELEGATES_EXCL("Delegates (exclude)"),
    DELEGATES_NEW("Delegates (new)"),
    NATIONS_INCL("Nations (include)"),
    NATIONS_EXCL("Nations (exclude)"),
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
    
    @Override
    public String toString()
    {
        return Text;
    }
}
