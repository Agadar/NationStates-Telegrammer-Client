package com.github.agadar.nstelegram.runnable;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.query.TelegramQuery;
import com.github.agadar.nstelegram.event.NoRecipientsFoundEvent;
import com.github.agadar.nstelegram.event.RecipientsRefreshedEvent;
import com.github.agadar.nstelegram.event.StoppedSendingEvent;
import com.github.agadar.nstelegram.event.TelegramManagerListener;
import com.github.agadar.nstelegram.util.TelegramManager;
import java.util.Set;

/**
 * Runnable used by TelegramManager which sends the telegrams to the recipients.
 *
 * @author Agadar
 */
public class SendTelegramsRunnable implements Runnable
{
    // References to TelegramManager and its private fields that are required
    // for this runnable to do its work.
    private final TelegramManager Tm;
    private final Set<String> Recipients;
    private final Set<TelegramManagerListener> Listeners;
    private final int NoRecipientsFoundTimeOut;
    
    public SendTelegramsRunnable(TelegramManager telegramManager, Set<String> recipients,
            Set<TelegramManagerListener> listeners, int noRecipientsFoundTimeOut)
    {
        this.Tm = telegramManager;
        this.Recipients = recipients;
        this.Listeners = listeners;
        this.NoRecipientsFoundTimeOut = noRecipientsFoundTimeOut;
    }    
    
    @Override
    public void run()
    {
        boolean causedByError = false;
        String errorMsg = null;

        try
        {
            do
            {
                // Prepare query.
                final TelegramQuery q = NSAPI.telegram(Tm.ClientKey, Tm.TelegramId, Tm.SecretKey,
                        Recipients.toArray(new String[Recipients.size()]))
                        .addListeners(Tm);

                // Tag as recruitment telegram if needed.
                if (Tm.SendAsRecruitment)
                    q.isRecruitment();

                q.execute(null);    // send the telegrams

                // If looping, update recipients until there's recipients available.
                if (Tm.IsLooping)
                {
                    final RecipientsRefreshedEvent refrevent = new RecipientsRefreshedEvent(this);
                    synchronized (Listeners)
                    {
                        // Publish recipients refreshed event.
                        Listeners.stream().forEach((tsl) -> 
                        {
                            tsl.handleRecipientsRefreshed(refrevent);
                        });
                    }

                    Tm.refreshFilters(false);
                    Tm.removeOldRecipients();

                    while (Recipients.isEmpty() && !Thread.interrupted())
                    {
                        final NoRecipientsFoundEvent event
                                = new NoRecipientsFoundEvent(this, NoRecipientsFoundTimeOut);

                        synchronized (Listeners)
                        {
                            // Publish no recipients found event.
                            Listeners.stream().forEach((tsl)  -> 
                            {
                                tsl.handleNoRecipientsFound(event);
                            });
                        }
                        Thread.sleep(NoRecipientsFoundTimeOut);

                        synchronized (Listeners)
                        {
                            // Publish recipients refreshed event.
                            Listeners.stream().forEach((tsl) -> 
                            {
                                tsl.handleRecipientsRefreshed(refrevent);
                            });
                        }

                        Tm.refreshFilters(false);
                        Tm.removeOldRecipients();
                    }
                }
            } while (Tm.IsLooping && !Thread.interrupted());
        } 
        catch (InterruptedException ex) { /* Just fall through to finally. */ } 
        catch (Exception ex)
        {
            // Dirty solution to not have ratelimiter exceptions show up as legit errors. 
            if (!ex.getMessage().equals("RateLimiter.class blew up!"))
            {
                causedByError = true;
                errorMsg = ex.getMessage();
            }
        } 
        finally
        {
            final StoppedSendingEvent stoppedEvent = new StoppedSendingEvent(this,
                    causedByError, errorMsg, 0, 0, 0);
            Listeners.stream().forEach((tsl) -> 
            {
                tsl.handleStoppedSending(stoppedEvent);
            });
        }
    }
}
