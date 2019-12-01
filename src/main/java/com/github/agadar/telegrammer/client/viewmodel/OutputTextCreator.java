package com.github.agadar.telegrammer.client.viewmodel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.misc.TelegramType;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.settings.CoreSettings;

import lombok.NonNull;

/**
 * Provides methods for creating texts meant for the output/log window.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class OutputTextCreator {

    private final static String BORDER = "------------------------------------------";

    private final CoreSettings coreSettings;

    public OutputTextCreator(@NonNull CoreSettings coreSettings) {
        this.coreSettings = coreSettings;
    }

    public String createExpectedDurationMessage() {

        String telegramId = coreSettings.getTelegramId();
        int numberOfRecipients = coreSettings.getFilters().getRecipients(telegramId).size();
        var telegramType = coreSettings.getTelegramType();

        int timePerTelegram = telegramType == TelegramType.RECRUITMENT ? 180050 : 30050;
        int estimatedDuration = Math.max(numberOfRecipients - 1, 0) * (timePerTelegram / 1000);
        int hours = estimatedDuration / 3600;
        int minutes = estimatedDuration % 3600 / 60;
        int seconds = estimatedDuration % 3600 % 60;

        return String.format(BORDER + "%naddressees selected: %s%nestimated duration: "
                + "%s hours, %s minutes, %s seconds%n" + BORDER + "%n", numberOfRecipients, hours, minutes, seconds);
    }

    public String createTimestampedMessage(String msg) {
        return "[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + msg + "\n";
    }

    public String createFailedFilterRefreshMessage(RecipientsFilter filter, Throwable ex) {
        return "error while refreshing filter '" + filter.toString() + "' : " + ex.getMessage() + "\n";
    }

    public String createStoppedSendingMessage(StoppedSendingEvent event) {
        return BORDER + "\nfinished queueing telegrams\n\n"
                + "telegrams queued: " + event.getQueuedSucces() + "\n" + "blocked by category: "
                + event.getRecipientIsBlocking() + "\n" + "recipients not found: " + event.getRecipientDidntExist()
                + "\n" + "failed b/c other reasons: " + event.getDisconnectOrOtherReason() + "\n" + BORDER + "\n";
    }
}
