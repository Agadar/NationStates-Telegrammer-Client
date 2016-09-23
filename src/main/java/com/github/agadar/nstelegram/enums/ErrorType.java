package com.github.agadar.nstelegram.enums;

/**
 * 
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public enum ErrorType 
{
    // No error; telegram succesfully queued.
    NONE("queued telegram for '%s'"),
    // Telegram not queued: recipient already received this telegram.
    PREVIOUS_RECIPIENT("skipping telegram for '%s': already received telegram"),
    // Telegram not queued: recipient is blocking recruitment telegrams.
    BLOCKING_RECRUITMENT("skipping telegram for '%s': recipient blocking recruitment telegrams"),
    // Telegram not queued: recipient is blocking campaign telegrams.
    BLOCKING_CAMPAIGN("skipping telegram for '%s': recipient blocking campaign telegrams"),
    // Telegram not queued: rate limit violated.
    RATELIMIT_VIOLATED("skipping telegram for '%s': rate limit violated"),
    // Telegram not queued: failed to reach server.
    COULDNT_REACH_SERVER("skipping telegram for '%s': failed to reach server"),
    // Telegram not queued: recipient does not exist.
    NONEXISTANT_RECIPIENT("skipping telegram for '%s': recipient does not exist"),
    // Telegram not queued: unexpected error. See logs.
    OTHER("skipping telegram for '%s': unexpected error, check logs")
    ;
    
    // Explanation.
    private final String Text;

    /**
     * Constructor.
     * 
     * @param text 
     */
    private ErrorType(String text)
    {
        this.Text = text;
    }
    
    @Override
    public String toString()
    {
        return Text;
    }
}
