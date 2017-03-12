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

    private final Set<String> QueuedSucces = new HashSet<>();
    private final Set<String> RecipientDidntExist = new HashSet<>();
    private final Set<String> RecipientIsBlocking = new HashSet<>();
    private final Set<String> DisconnectOrOtherReason = new HashSet<>();

    public void registerSucces(String recipient) {
        QueuedSucces.add(recipient);
        RecipientDidntExist.remove(recipient);
        RecipientIsBlocking.remove(recipient);
        DisconnectOrOtherReason.remove(recipient);
    }

    public void registerFailure(String recipient, SkippedRecipientReason reason) {
        // If the recipient already received the telegram succesfully before, don't register a failure.
        if (QueuedSucces.contains(recipient)) {
            return;
        }

        if (reason == null) // Else if no reason given, assume 'DisconnectOrOtherReason'
        {
            DisconnectOrOtherReason.add(recipient);
            return;
        }

        switch (reason) // Else if reason given, add to appropriate map.
        {
            case BLOCKING_RECRUITMENT:
            case BLOCKING_CAMPAIGN:
                RecipientIsBlocking.add(recipient);
                break;
            case NOT_FOUND:
                RecipientDidntExist.add(recipient);
                break;
        }
    }

    public int getQueuedSucces() {
        return QueuedSucces.size();
    }

    public int getRecipientDidntExist() {
        return RecipientDidntExist.size();
    }

    public int getRecipientIsBlocking() {
        return RecipientIsBlocking.size();
    }

    public int getDisconnectOrOtherReason() {
        return DisconnectOrOtherReason.size();
    }
}
