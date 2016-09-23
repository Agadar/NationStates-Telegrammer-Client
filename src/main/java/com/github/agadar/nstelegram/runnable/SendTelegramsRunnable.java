package com.github.agadar.nstelegram.runnable;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.nation.Nation;
import com.github.agadar.nsapi.enums.shard.NationShard;
import com.github.agadar.nsapi.event.TelegramSentEvent;
import com.github.agadar.nsapi.event.TelegramSentListener;
import com.github.agadar.nsapi.query.TelegramQuery;
import com.github.agadar.nstelegram.enums.SkippedRecipientReason;
import com.github.agadar.nstelegram.enums.TelegramType;
import com.github.agadar.nstelegram.event.NoRecipientsFoundEvent;
import com.github.agadar.nstelegram.event.RecipientRemovedEvent;
import com.github.agadar.nstelegram.event.RecipientsRefreshedEvent;
import com.github.agadar.nstelegram.event.StoppedSendingEvent;
import com.github.agadar.nstelegram.event.TelegramManagerListener;
import com.github.agadar.nstelegram.manager.TelegramManager;
import com.github.agadar.nstelegram.util.QueuedStats;
import com.github.agadar.nstelegram.util.Tuple;
import java.util.Map;
import java.util.Set;

/**
 * Runnable used by TelegramManager which sends the telegrams to the recipients.
 *
 * @author Agadar
 */
public class SendTelegramsRunnable implements Runnable, TelegramSentListener
{
    // References to TelegramManager and its private fields that are required
    // for this runnable to do its work.
    private final TelegramManager Tm;
    private final Set<String> Recipients;
    private final Set<TelegramManagerListener> Listeners;
    private final int NoRecipientsFoundTimeOut;
    private final QueuedStats Stats;
    private final Map<Tuple<String, String>, SkippedRecipientReason> History;
    
    public SendTelegramsRunnable(TelegramManager telegramManager, Set<String> recipients,
            Set<TelegramManagerListener> listeners, int noRecipientsFoundTimeOut, 
            Map<Tuple<String, String>, SkippedRecipientReason> history)
    {
        this.Tm = telegramManager;
        this.Recipients = recipients;
        this.Listeners = listeners;
        this.NoRecipientsFoundTimeOut = noRecipientsFoundTimeOut;
        this.Stats = new QueuedStats(Recipients);
        this.History = history;
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
                if (Recipients.size() > 0)
                {
                    final String[] RecipArray = Recipients.toArray(new String[Recipients.size()]);
                    
                    if (Tm.LastTelegramType != null)
                    switch (Tm.LastTelegramType)
                    {
                        case RECRUITMENT:
                        {
                            boolean skipNext = !canReceiveRecruitmentTelegrams(RecipArray[0]);
                            for (int i = 0; i < RecipArray.length; i++)
                            {
                                final boolean skipThis = skipNext;
                                
                                if (i < RecipArray.length - 1)
                                {
                                    final String nextRecipient = RecipArray[i + 1];
                                    skipNext = !canReceiveRecruitmentTelegrams(nextRecipient);
                                }
                                
                                if (skipThis)
                                    continue;
                                
                                sendTelegram(RecipArray[i]);
                            }       
                            break;
                        }
                        case CAMPAIGN:
                        {
                            boolean skipNext = !canReceiveCampaignTelegrams(RecipArray[0]);
                            for (int i = 0; i < RecipArray.length; i++)
                            {
                                final boolean skipThis = skipNext;
                                
                                if (i < RecipArray.length - 1)
                                {
                                    final String nextRecipient = RecipArray[i + 1];
                                    skipNext = !canReceiveCampaignTelegrams(nextRecipient);
                                }
                                
                                if (skipThis)
                                    continue;
                                
                                sendTelegram(RecipArray[i]);
                            }       
                            break;
                        }
                        default:
                            sendTelegram(RecipArray);
                            break;
                    }
                }
                
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
                    Tm.removeOldRecipients(true);

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
                        Tm.removeOldRecipients(true);
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
                    causedByError, errorMsg, Stats.getQueuedSucces(), 
                    Stats.getQueuedFailed(), Stats.getQueuedNot());
            Listeners.stream().forEach((tsl) -> 
            {
                tsl.handleStoppedSending(stoppedEvent);
            });
        }
    }
    
    @Override
    public void handleTelegramSent(TelegramSentEvent event)
    {
        // Update the History. We're assuming removeOldRecipients is always
        // called before this and the Telegram Id didn't change in the meantime,
        // so there is no need to make sure the entry for the current Telegram Id
        // changed.       
        if (event.Queued)
        {
            History.put(new Tuple(Tm.TelegramId, event.Addressee), SkippedRecipientReason.PREVIOUS_RECIPIENT);
            Stats.registerSucces(event.Addressee);
        }
        else
        {
            Stats.registerFailure(event.Addressee);
        }
        
        synchronized(Listeners)
        {
            // Pass telegram sent event through.
            Listeners.stream().forEach((tsl) ->
            {
                tsl.handleTelegramSent(event);
            });
        }
    }
    
    /**
     * Sends the telegram to the specified recipient(s).
     * 
     * @param recipients 
     */
    private void sendTelegram(String... recipients)
    {
        // Prepare query.
        final TelegramQuery q = NSAPI.telegram(Tm.ClientKey, Tm.TelegramId, Tm.SecretKey,
                recipients).addListeners(this);

        // Tag as recruitment telegram if needed.
        if (Tm.LastTelegramType == TelegramType.RECRUITMENT)
            q.isRecruitment();

        q.execute(null);    // send the telegrams
    }
    
    /**
     * Returns whether or not the recipient may receive a recruitment telegram.
     * If not, removes it from Recipients and throws a RecipientRemovedEvent.
     * 
     * @param recipient
     * @return 
     */
    private boolean canReceiveRecruitmentTelegrams(String recipient)
    {
        // Make server call.
        Nation n = NSAPI.nation(recipient).shards(NationShard.CanReceiveRecruitmentTelegrams)
                            .canReceiveTelegramFromRegion(Tm.FromRegion).execute();
        final SkippedRecipientReason reason = (n == null) ? SkippedRecipientReason.NOT_FOUND : 
                !n.CanReceiveRecruitmentTelegrams ? SkippedRecipientReason.BLOCKING_RECRUITMENT : null;              
        return canReceiveXTelegrams(reason, recipient);
    }
    
    /**
     * Returns whether or not the recipient may receive a campaign telegram.
     * If not, removes it from Recipients and throws a RecipientRemovedEvent.
     * 
     * @param recipient
     * @return 
     */
    private boolean canReceiveCampaignTelegrams(String recipient)
    {
        // Make server call.
        Nation n = NSAPI.nation(recipient).shards(NationShard.CanReceiveCampaignTelegrams).execute();
        final SkippedRecipientReason reason = (n == null) ? SkippedRecipientReason.NOT_FOUND : 
                !n.CanReceiveCampaignTelegrams ? SkippedRecipientReason.BLOCKING_CAMPAIGN : null;              
        return canReceiveXTelegrams(reason, recipient);
    }
    
    /**
     * Shared behavior by canReceiveRecruitmentTelegrams(...) and canReceiveCampaignTelegrams(...).
     * 
     * @param reason
     * @param recipient
     * @return 
     */
    private boolean canReceiveXTelegrams(SkippedRecipientReason reason, String recipient)
    {
        if (reason != null)
        {
            Recipients.remove(recipient);
            History.put(new Tuple(Tm.TelegramId, recipient), reason);        
            final RecipientRemovedEvent event = new RecipientRemovedEvent(this, recipient, reason);

            synchronized(Listeners)
            {
                // Pass telegram sent event through.
                Listeners.stream().forEach((tsl) ->
                {
                    tsl.handleRecipientRemoved(event);
                });
            }
            return false;
        }
        return true;
    }
}
