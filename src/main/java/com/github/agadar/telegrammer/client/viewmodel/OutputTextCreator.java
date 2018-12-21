package com.github.agadar.telegrammer.client.viewmodel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.github.agadar.telegrammer.core.properties.manager.PropertiesManager;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.telegram.TelegramType;
import com.github.agadar.telegrammer.core.telegram.event.StoppedSendingEvent;

import lombok.NonNull;

/**
 * Provides methods for creating texts meant for the output/log window.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class OutputTextCreator {

    private final static String BORDER = "------------------------------------------";

    private final PropertiesManager<?> propertiesManager;

    public OutputTextCreator(@NonNull PropertiesManager<?> propertiesManager) {
        this.propertiesManager = propertiesManager;
    }

    public String createExpectedDurationMessage() {
        var properties = propertiesManager.getProperties();
        String telegramId = properties.getTelegramId();
        int numberOfRecipients = properties.getRecipientsListBuilder().getRecipients(telegramId).size();
        var telegramType = properties.getLastTelegramType();

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
        return createTimestampedMessage(
                "error while refreshing filter '" + filter.toString() + "' : " + ex.getMessage());
    }

    public String createStoppedSendingMessage(StoppedSendingEvent event) {
        return BORDER + "\nfinished"
                + (event.isCausedByError() ? " with error: " + event.getErrorMsg() + "\n" : " without fatal errors\n")
                + "telegrams queued: " + event.getQueuedSucces() + "\n" + "blocked by category: "
                + event.getRecipientIsBlocking() + "\n" + "recipients not found: " + event.getRecipientDidntExist()
                + "\n" + "failed b/c other reasons: " + event.getDisconnectOrOtherReason() + "\n" + BORDER + "\n";
    }
}
