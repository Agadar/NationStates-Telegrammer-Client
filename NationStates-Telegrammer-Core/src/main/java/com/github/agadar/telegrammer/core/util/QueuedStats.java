package com.github.agadar.telegrammer.core.util;

import com.github.agadar.telegrammer.core.enums.SkippedRecipientReason;

import java.util.HashSet;
import java.util.Set;

/**
 * Holder for telegram queued statistics, containing logic to properly keep
 * track of the statistics.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class QueuedStats {

    private final Set<String> queuedSucces = new HashSet<>();
    private final Set<String> recipientDidntExist = new HashSet<>();
    private final Set<String> recipientIsBlocking = new HashSet<>();
    private final Set<String> disconnectOrOtherReason = new HashSet<>();

    public void registerSucces(String recipient) {
        queuedSucces.add(recipient);
        recipientDidntExist.remove(recipient);
        recipientIsBlocking.remove(recipient);
        disconnectOrOtherReason.remove(recipient);
    }

    public void registerFailure(String recipient, SkippedRecipientReason reason) {
        // If the recipient already received the telegram succesfully before, don't register a failure.
        if (queuedSucces.contains(recipient)) {
            return;
        }

        if (reason == null) // Else if no reason given, assume 'DisconnectOrOtherReason'
        {
            disconnectOrOtherReason.add(recipient);
            return;
        }

        switch (reason) // Else if reason given, add to appropriate map.
        {
            case BLOCKING_RECRUITMENT:
            case BLOCKING_CAMPAIGN:
                recipientIsBlocking.add(recipient);
                break;
            case NOT_FOUND:
                recipientDidntExist.add(recipient);
                break;
        }
    }

    public int getQueuedSucces() {
        return queuedSucces.size();
    }

    public int getRecipientDidntExist() {
        return recipientDidntExist.size();
    }

    public int getRecipientIsBlocking() {
        return recipientIsBlocking.size();
    }

    public int getDisconnectOrOtherReason() {
        return disconnectOrOtherReason.size();
    }
}
