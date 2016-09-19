package com.github.agadar.nstelegram.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Holder for telegram queued statistics, containing logic to properly keep 
 * track of the statistics.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public class QueuedStats 
{
    private final Set<String> RecipientsRemaining;
    private final Set<String> QueuedSucces = new HashSet<>();
    private final Set<String> QueuedFailed = new HashSet<>();
    
    /**
     * Constructor.
     * 
     * @param recipients
     */
    public QueuedStats(Set<String> recipients)
    {
        this.RecipientsRemaining = recipients;
    }
    
    public void registerSucces(String recipient)
    {
        if (RecipientsRemaining.contains(recipient))
        {
            RecipientsRemaining.remove(recipient);
            QueuedSucces.add(recipient);
        }
        else if (QueuedFailed.contains(recipient))
        {
            QueuedFailed.remove(recipient);
            QueuedSucces.add(recipient);
        }
    }
    
    public void registerFailure(String recipient)
    {
        if (RecipientsRemaining.contains(recipient))
        {
            RecipientsRemaining.remove(recipient);
            QueuedFailed.add(recipient);
        }
    }
    
    public int getQueuedSucces()
    {
        return QueuedSucces.size();
    }
    
    public int getQueuedFailed()
    {
        return QueuedFailed.size();
    }
    
    public int getQueuedNot()
    {
        return RecipientsRemaining.size();
    }
}
