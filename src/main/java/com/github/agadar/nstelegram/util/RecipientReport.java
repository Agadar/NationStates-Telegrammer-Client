package com.github.agadar.nstelegram.util;

import com.github.agadar.nstelegram.enums.ErrorType;

/**
 * Report on a recipient.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public class RecipientReport 
{   
    /** Name of the recipient. */
    public final String Recipient;
    
    /** The possible error. */
    public final ErrorType Error;
    
    public RecipientReport(String recipient, ErrorType error)
    {
        this.Recipient = recipient;
        this.Error = error;
    }
    
    @Override
    public String toString()
    {
        return String.format(Error.toString(), Recipient);
    }
}
