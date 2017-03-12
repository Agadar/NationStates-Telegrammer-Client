package com.github.agadar.telegrammer.core.enums;

/**
 * Enum for reasons why the recipient was skipped.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public enum SkippedRecipientReason {
    /**
     * The telegram was already sent before to this recipient.
     */
    PREVIOUS_RECIPIENT("recipient already received this telegram"),
    /**
     * The recipient is currently blocking recruitment telegrams.
     */
    BLOCKING_RECRUITMENT("recipient is blocking recruitment telegrams"),
    /**
     * The recipient is currently blocking campaign telegrams.
     */
    BLOCKING_CAMPAIGN("recipient is blocking campaign telegrams"),
    /**
     * The recipient was not found or doesn't exist.
     */
    NOT_FOUND("recipient was not found or doesn't exist");

    // Explanation.
    private final String Text;

    /**
     * Constructor.
     *
     * @param text
     */
    private SkippedRecipientReason(String text) {
        this.Text = text;
    }

    @Override
    public String toString() {
        return Text;
    }
}
