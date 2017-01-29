package com.github.agadar.nstelegram.enums;

/**
 * Filter types, with corresponding String explanations.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum FilterType {
    ALL("All"),
    DELEGATES_EXCL("Delegates (exclude)"),
    DELEGATES_INCL("Delegates (include)"),
    DELEGATES_NEW("Delegates (new)"),
    EMBASSIES_EXCL("Embassies (exclude)"),
    EMBASSIES_INCL("Embassies (include)"),
    NATIONS_EJECTED("Nations (ejected)"),
    NATIONS_EXCL("Nations (exclude)"),
    NATIONS_INCL("Nations (include)"),
    NATIONS_NEW("Nations (new)"),
    NATIONS_REFOUNDED("Nations (refounded)"),
    REGIONS_EXCL("Regions (exclude)"),
    REGIONS_INCL("Regions (include)"),
    REGIONS_WITH_TAGS_EXCL("Regions (exclude with tags)"),
    REGIONS_WITH_TAGS_INCL("Regions (include with tags)"),
    REGIONS_WO_TAGS_EXCL("Regions (exclude w/o tags)"),
    REGIONS_WO_TAGS_INCL("Regions (include w/o tags)"),
    WA_MEMBERS_EXCL("WA Members (exclude)"),
    WA_MEMBERS_INCL("WA Members (include)"),
    WA_MEMBERS_NEW("WA Members (new)");

    // Explanation.
    private final String Text;

    /**
     * Constructor.
     *
     * @param text
     */
    private FilterType(String text) {
        this.Text = text;
    }

    @Override
    public String toString() {
        return Text;
    }
}
