package com.github.agadar.telegrammer.core.enums;

/**
 * Enumerator for telegram types. As of writing, the known types are: Normal,
 * Campaign, and Recruitment.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum TelegramType {
    NORMAL("Normal"),
    CAMPAIGN("Campaign"),
    RECRUITMENT("Recruitment");

    // Explanation.
    private final String Text;

    /**
     * Constructor.
     *
     * @param text
     */
    private TelegramType(String text) {
        this.Text = text;
    }

    @Override
    public String toString() {
        return Text;
    }
}
