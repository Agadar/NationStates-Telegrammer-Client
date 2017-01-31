package com.github.agadar.nstelegram.enums;

/**
 * Filter types, with corresponding String explanations.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum FilterType {
    ALL("+ all nations"),
    NATIONS_EJECTED("+ ejected nations (indefinitely)"),
    EMBASSIES_INCL("+ embassies of regions"),
    NATIONS_NEW("+ new nations (indefinitely)"),
    DELEGATES_NEW_MAX("+ new WA delegates"),
    DELEGATES_NEW("+ new WA delegates (indefinitely)"),
    WA_MEMBERS_NEW("+ new WA members (indefinitely)"),
    NATIONS_REFOUNDED("+ refounded nations (indefinitely)"),
    REGIONS_INCL("+ regions"),
    REGIONS_WITH_TAGS_INCL("+ regions with tags"),
    REGIONS_WO_TAGS_INCL("+ regions without tags"),
    NATIONS_INCL("+ specific nations"),
    DELEGATES_EXCL("+ WA delegates"),
    WA_MEMBERS_INCL("+ WA members"),
    EMBASSIES_EXCL("- embassies of regions"),
    REGIONS_EXCL("- regions"),
    REGIONS_WITH_TAGS_EXCL("- regions with tags"),
    REGIONS_WO_TAGS_EXCL("- regions without tags"),
    NATIONS_EXCL("- specific nations"),
    DELEGATES_INCL("- WA delegates"),
    WA_MEMBERS_EXCL("- WA members")
    ;

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
