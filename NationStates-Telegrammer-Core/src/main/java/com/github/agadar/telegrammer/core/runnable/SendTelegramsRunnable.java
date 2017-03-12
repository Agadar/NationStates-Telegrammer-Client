package com.github.agadar.telegrammer.core.runnable;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.domain.nation.Nation;
import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.nationstates.event.TelegramSentListener;
import com.github.agadar.nationstates.query.TelegramQuery;
import com.github.agadar.nationstates.shard.NationShard;

import com.github.agadar.telegrammer.core.enums.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.enums.TelegramType;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.manager.PropertiesManager;
import com.github.agadar.telegrammer.core.manager.TelegramManager;
import com.github.agadar.telegrammer.core.util.QueuedStats;
import com.github.agadar.telegrammer.core.util.Tuple;

import java.util.Map;
import java.util.Set;

/**
 * Runnable used by TelegramManager which sends the telegrams to the recipients.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class SendTelegramsRunnable implements Runnable, TelegramSentListener {

    // References to TelegramManager and its private fields that are required
    // for this runnable to do its work.
    private final TelegramManager Tm;
    private final Set<String> Recipients;
    private final Set<TelegramManagerListener> Listeners;
    private final int NoRecipientsFoundTimeOut;
    private final QueuedStats Stats;
    private final Map<Tuple<String, String>, SkippedRecipientReason> History;
    private final PropertiesManager PropsManager;

    public SendTelegramsRunnable(TelegramManager telegramManager, Set<String> recipients,
            Set<TelegramManagerListener> listeners, int noRecipientsFoundTimeOut,
            Map<Tuple<String, String>, SkippedRecipientReason> history, PropertiesManager propsManager) {
        this.Tm = telegramManager;
        this.Recipients = recipients;
        this.Listeners = listeners;
        this.NoRecipientsFoundTimeOut = noRecipientsFoundTimeOut;
        this.Stats = new QueuedStats();
        this.History = history;
        PropsManager = propsManager;
    }

    @Override
    public void run() {
        boolean causedByError = false;
        String errorMsg = null;

        try {
            // Loop until either the thread has been interrupted, or all filters are done.
            while (!Thread.currentThread().isInterrupted()) {
                // If there are recipients available...
                if (Recipients.size() > 0) {
                    final String[] RecipArray = Recipients.toArray(new String[Recipients.size()]);

                    if (PropsManager.lastTelegramType != null) {
                        // According to the telegram type, take proper action...
                        switch (PropsManager.lastTelegramType) {
                            case RECRUITMENT: {
                                boolean skipNext = !canReceiveRecruitmentTelegrams(RecipArray[0]);
                                for (int i = 0; i < RecipArray.length && !Thread.currentThread().isInterrupted(); i++) {
                                    final boolean skipThis = skipNext;

                                    if (i < RecipArray.length - 1) {
                                        final String nextRecipient = RecipArray[i + 1];
                                        skipNext = !canReceiveRecruitmentTelegrams(nextRecipient);
                                    }

                                    if (skipThis) {
                                        continue;
                                    }
                                    sendTelegram(RecipArray[i]);
                                }
                                break;
                            }
                            case CAMPAIGN: {
                                boolean skipNext = !canReceiveCampaignTelegrams(RecipArray[0]);
                                for (int i = 0; i < RecipArray.length && !Thread.currentThread().isInterrupted(); i++) {
                                    final boolean skipThis = skipNext;

                                    if (i < RecipArray.length - 1) {
                                        final String nextRecipient = RecipArray[i + 1];
                                        skipNext = !canReceiveCampaignTelegrams(nextRecipient);
                                    }

                                    if (skipThis) {
                                        continue;
                                    }
                                    sendTelegram(RecipArray[i]);
                                }
                                break;
                            }
                            // If we're sending a normal telegram, just send it.
                            default:
                                sendTelegram(RecipArray);
                                break;
                        }
                    }
                } // Else if the recipients list is empty, sleep for a bit, then continue.
                else {
                    final NoRecipientsFoundEvent event = new NoRecipientsFoundEvent(this, NoRecipientsFoundTimeOut);

                    synchronized (Listeners) {
                        // Publish no recipients found event.
                        Listeners.stream().forEach((tsl)
                                -> {
                            tsl.handleNoRecipientsFound(event);
                        });
                    }
                    Thread.sleep(NoRecipientsFoundTimeOut);
                }

                // If none of the filters can retrieve any new recipients, just end it all.
                if (Tm.cantRetrieveMoreNations() || Thread.currentThread().isInterrupted()) {
                    break;
                }

                // Refresh the filters before going back to the top.
                Tm.refreshAndReapplyFilters();
                final RecipientsRefreshedEvent refrevent = new RecipientsRefreshedEvent(this);

                synchronized (Listeners) {
                    // Publish recipients refreshed event.
                    Listeners.stream().forEach((tsl)
                            -> {
                        tsl.handleRecipientsRefreshed(refrevent);
                    });
                }

            }
        } catch (InterruptedException ex) {
            /* Just fall through to finally. */
        } finally {
            // Reset filters.
            Tm.resetAndReapplyFilters();

            // Send stopped event.
            final StoppedSendingEvent stoppedEvent = new StoppedSendingEvent(this,
                    causedByError, errorMsg, Stats.getQueuedSucces(),
                    Stats.getRecipientDidntExist(), Stats.getRecipientIsBlocking(),
                    Stats.getDisconnectOrOtherReason());
            Listeners.stream().forEach((tsl)
                    -> {
                tsl.handleStoppedSending(stoppedEvent);
            });
        }
    }

    @Override
    public void handleTelegramSent(TelegramSentEvent event) {
        // Update the History. We're assuming removeOldRecipients is always
        // called before this and the Telegram Id didn't change in the meantime,
        // so there is no need to make sure the entry for the current Telegram Id
        // changed.       
        if (event.queued) {
            // Only add it to the history if this wasn't a dry run, i.e. no actual telegram was sent.
            //if (!PropsManager.dryRun) {
            History.put(new Tuple(PropsManager.telegramId, event.recipient), SkippedRecipientReason.PREVIOUS_RECIPIENT);
            //}
            Stats.registerSucces(event.recipient);
        } else {
            Stats.registerFailure(event.recipient, null);
        }
        //System.out.println("--------called----1-----");
        synchronized (Listeners) {
            // Pass telegram sent event through.
            Listeners.stream().forEach((tsl)
                    -> {
                tsl.handleTelegramSent(event);
            });
        }
    }

    /**
     * Sends the telegram to the specified recipient(s).
     *
     * @param recipients
     */
    private void sendTelegram(String... recipients) {
        // Prepare query.
        final TelegramQuery q = NationStates.telegram(PropsManager.clientKey, PropsManager.telegramId, PropsManager.secretKey,
                recipients).addListeners(this);

        // Tag as recruitment telegram if needed.
        if (PropsManager.lastTelegramType == TelegramType.RECRUITMENT) {
            q.isRecruitment();
        }

        // Tag as dry run if needed.
        if (PropsManager.dryRun) {
            q.isDryRun();
        }

        q.execute(null);    // send the telegrams
    }

    /**
     * Returns whether or not the recipient may receive a recruitment telegram.
     * If not, removes it from Recipients and throws a RecipientRemovedEvent. If
     * the server couldn't be reached, always returns true.
     *
     * @param recipient
     * @return
     */
    private boolean canReceiveRecruitmentTelegrams(String recipient) {
        try {
            // Make server call.
            Nation n = NationStates.nation(recipient).shards(NationShard.CAN_RECEIVE_RECRUITMENT_TELEGRAMS)
                    .canReceiveTelegramFromRegion(PropsManager.fromRegion).execute();
            final SkippedRecipientReason reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.canReceiveRecruitmentTelegrams ? SkippedRecipientReason.BLOCKING_RECRUITMENT : null;
            return canReceiveXTelegrams(reason, recipient);
        } catch (Exception ex) {
            // If for any reason the call failed, just take the gamble and say 
            // that the recipient can receive the telegram.
            return true;
        }
    }

    /**
     * Returns whether or not the recipient may receive a campaign telegram. If
     * not, removes it from Recipients and throws a RecipientRemovedEvent. If
     * the server couldn't be reached, always returns true.
     *
     * @param recipient
     * @return
     */
    private boolean canReceiveCampaignTelegrams(String recipient) {
        try {
            // Make server call.
            Nation n = NationStates.nation(recipient).shards(NationShard.CAN_RECEIVE_CAMPAIGN_TELEGRAMS).execute();
            final SkippedRecipientReason reason = (n == null) ? SkippedRecipientReason.NOT_FOUND
                    : !n.canReceiveCampaignTelegrams ? SkippedRecipientReason.BLOCKING_CAMPAIGN : null;
            return canReceiveXTelegrams(reason, recipient);
        } catch (Exception ex) {
            // If for any reason the call failed, just take the gamble and say 
            // that the recipient can receive the telegram.
            return true;
        }
    }

    /**
     * Shared behavior by canReceiveRecruitmentTelegrams(...) and
     * canReceiveCampaignTelegrams(...).
     *
     * @param reason
     * @param recipient
     * @return
     */
    private boolean canReceiveXTelegrams(SkippedRecipientReason reason, String recipient) {
        if (reason != null) {
            Stats.registerFailure(recipient, reason);
            Recipients.remove(recipient);
            History.put(new Tuple(PropsManager.telegramId, recipient), reason);
            final RecipientRemovedEvent event = new RecipientRemovedEvent(this, recipient, reason);

            synchronized (Listeners) {
                // Pass telegram sent event through.
                Listeners.stream().forEach((tsl)
                        -> {
                    tsl.handleRecipientRemoved(event);
                });
            }
            return false;
        }
        return true;
    }
}
