package com.github.agadar.telegrammer.client.viewmodel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientProperties;
import com.github.agadar.telegrammer.core.properties.manager.PropertiesManager;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.telegram.TelegramType;
import com.github.agadar.telegrammer.core.telegram.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.telegram.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.telegram.sender.TelegramSender;
import com.github.agadar.telegrammer.core.util.StringFunctions;

import lombok.Getter;

public class TelegrammerViewModel implements TelegramManagerListener {

    private final static String BORDER = "------------------------------------------";

    @Getter private final String title = "Agadar's NationStates Telegrammer Client 2.0.0";

    private final RecipientsFilterTranslator filterTranslator;
    private final TelegrammerClientProperties properties;
    private final PropertiesManager<TelegrammerClientProperties> propertiesManager;
    private final TelegramSender telegramSender;

    @Getter private int selectedFilterTypeIndex = 0;
    @Getter private int selectedProviderTypeIndex = 0;
    @Getter private int selectedConfiguredRecipientsFilterIndex = -1;
    @Getter private String filterParameters = "";
    @Getter private String outputText = "";

    private TelegrammerViewModelListener listener = null;
    private Thread compileRecipientsWorker = null;
    private TelegrammerState state = TelegrammerState.Idle;

    public TelegrammerViewModel(TelegramSender telegramSender,
            PropertiesManager<TelegrammerClientProperties> propertiesManager, TelegrammerClientProperties properties,
            RecipientsFilterTranslator filterTranslator) {

        this.telegramSender = telegramSender;
        this.propertiesManager = propertiesManager;
        this.filterTranslator = filterTranslator;
        this.properties = properties;

        outputText = getDurationText();
        telegramSender.addListeners(this);

        if (properties.isStartSendingOnStartup()) {
            this.startSendingTelegrams();
        }
    }

    public void setListener(TelegrammerViewModelListener listener) {
        this.listener = listener;
        listener.refreshEverything();
    }

    public boolean isOptionsMenuEnabled() {
        return state == TelegrammerState.Idle;
    }

    public boolean getHideSkippedRecipients() {
        return properties.isHideSkippedRecipients();
    }

    public void setHideSkippedRecipients(boolean value) {
        if (isOptionsMenuEnabled()) {
            properties.setHideSkippedRecipients(value);
        }
    }

    public boolean getRefreshRecipientsAfterEveryTelegram() {
        return properties.isUpdateRecipientsAfterEveryTelegram();
    }

    public void setRefreshRecipientsAfterEveryTelegram(boolean value) {
        if (isOptionsMenuEnabled()) {
            properties.setUpdateRecipientsAfterEveryTelegram(value);
        }
    }

    public boolean getRunIndefinitely() {
        return properties.isRunIndefinitely();
    }

    public void setRunIndefinitely(boolean value) {
        if (isOptionsMenuEnabled()) {
            properties.setRunIndefinitely(value);
        }
    }

    public boolean getStartMinimized() {
        return properties.isStartMinimized();
    }

    public void setStartMinimized(boolean value) {
        if (isOptionsMenuEnabled()) {
            properties.setStartMinimized(value);
        }
    }

    public boolean getStartSendingOnStartup() {
        return properties.isStartSendingOnStartup();
    }

    public void setStartSendingOnStartup(boolean value) {
        if (isOptionsMenuEnabled()) {
            properties.setStartSendingOnStartup(value);
        }
    }

    public boolean isClientKeyInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String getClientKey() {
        return properties.getClientKey();
    }

    public void setClientKey(String value) {
        if (isClientKeyInputEnabled()) {
            properties.setClientKey(value);
        }
    }

    public boolean isTelegramIdInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String getTelegramId() {
        return properties.getTelegramId();
    }

    public void setTelegramId(String value) {
        if (isTelegramIdInputEnabled()) {
            properties.setTelegramId(value);
            outputText = getDurationText();
            listener.refreshOutput();
        }
    }

    public boolean isSecretKeyInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String getSecretKey() {
        return properties.getSecretKey();
    }

    public void setSecretKey(String value) {
        if (isSecretKeyInputEnabled()) {
            properties.setSecretKey(value);
        }
    }

    public boolean isAvailableTelegramTypesEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String[] getAvailableTelegramTypes() {
        return Arrays.stream(TelegramType.values()).map(telegramType -> telegramType.toString()).toArray(String[]::new);
    }

    public int getSelectedTelegramTypeIndex() {
        return Arrays.asList(TelegramType.values()).indexOf(properties.getLastTelegramType());
    }

    public void setSelectedTelegramTypeIndex(int value) {
        if (isAvailableTelegramTypesEnabled()) {
            var telegramType = TelegramType.values()[value];
            properties.setLastTelegramType(telegramType);
            outputText = getDurationText();

            if (telegramType != TelegramType.RECRUITMENT && telegramType != TelegramType.CAMPAIGN) {
                properties.setFromRegion("");
            }
            listener.refreshEverything();
        }
    }

    public boolean isForRegionInputEnabled() {
        return state == TelegrammerState.Idle && (properties.getLastTelegramType() == TelegramType.RECRUITMENT
                || properties.getLastTelegramType() == TelegramType.CAMPAIGN);
    }

    public String getForRegion() {
        return properties.getFromRegion();
    }

    public void setForRegion(String value) {
        if (isForRegionInputEnabled()) {
            properties.setFromRegion(value);
        }
    }

    public boolean isConfiguredRecipientsFiltersEnabled() {
        return state == TelegrammerState.Idle;
    }

    public List<String> getConfiguredRecipientsFilters() {
        return properties.getRecipientsListBuilder().getFilters().stream().map(filter -> filter.toString())
                .collect(Collectors.toList());
    }

    public void setSelectedConfiguredRecipientsFilter(int index) {
        if (isConfiguredRecipientsFiltersEnabled()) {

            int oldIndex = selectedConfiguredRecipientsFilterIndex;
            selectedConfiguredRecipientsFilterIndex = index;

            if (oldIndex != index) {
                listener.refreshEverything();
            }
        }
    }

    public void unsetSelectedConfiguredRecipientsFilter() {
        if (isConfiguredRecipientsFiltersEnabled() && selectedConfiguredRecipientsFilterIndex > -1) {
            selectedConfiguredRecipientsFilterIndex = -1;
            listener.refreshEverything();
        }
    }

    public boolean isAvailableFilterTypesInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String[] getAvailableFilterTypes() {
        return Arrays.stream(RecipientsFilterType.values()).map(type -> type.toString()).toArray(String[]::new);
    }

    public void setSelectedFilterTypeIndex(int value) {
        if (isAvailableFilterTypesInputEnabled()) {
            selectedFilterTypeIndex = value;
        }
    }

    public boolean isAvailableProviderTypesInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String[] getAvailableProviderTypes() {
        return Arrays.stream(RecipientsProviderType.values()).map(type -> type.toString()).toArray(String[]::new);
    }

    public void setSelectedProviderTypeIndex(int value) {
        if (isAvailableProviderTypesInputEnabled()) {
            selectedProviderTypeIndex = value;
            filterParameters = "";
            listener.refreshEverything();
        }
    }

    public boolean isFilterParametersInputEnabled() {
        switch (RecipientsProviderType.values()[selectedProviderTypeIndex]) {
        case NATIONS_IN_EMBASSY_REGIONS:
        case NATIONS:
        case NATIONS_IN_REGIONS:
        case NATIONS_IN_REGIONS_WITH_TAGS:
        case NATIONS_IN_REGIONS_WITHOUT_TAGS:
            return true;
        default:
            return false;
        }
    }

    public void setFilterParameters(String value) {
        if (isFilterParametersInputEnabled()) {
            filterParameters = value;
        }
    }

    public String getFilterParametersHint() {
        switch (RecipientsProviderType.values()[selectedProviderTypeIndex]) {
        case NATIONS_IN_EMBASSY_REGIONS:
        case NATIONS_IN_REGIONS:
            return "Insert region names, e.g. 'region1, region2'.";
        case NATIONS:
            return "Insert nation names, e.g. 'nation1, nation2'.";
        case NATIONS_IN_REGIONS_WITH_TAGS:
        case NATIONS_IN_REGIONS_WITHOUT_TAGS:
            return "Insert region tags, e.g. 'tag1, tag2'.";
        default:
            return "";
        }
    }

    public boolean isRemoveFilterButtonEnabled() {
        return state == TelegrammerState.Idle && selectedConfiguredRecipientsFilterIndex > -1;
    }

    public boolean isAddFilterButtonEnabled() {
        return state == TelegrammerState.Idle;
    }

    public boolean isStartSendingButtonEnabled() {
        return state == TelegrammerState.Idle;
    }

    public boolean isStopSendingButtonEnabled() {
        return state == TelegrammerState.SendingTelegrams;
    }

    public void startSendingTelegrams() {
        if (!isStartSendingButtonEnabled()) {
            return;
        }
        properties.setFromRegion(properties.getFromRegion().trim());
        properties.setClientKey(removeWhiteSpaces(properties.getClientKey()));
        properties.setTelegramId(removeWhiteSpaces(properties.getTelegramId()));
        properties.setSecretKey(removeWhiteSpaces(properties.getSecretKey()));
        outputText = getDurationText();
        changeStateAndInformListener(TelegrammerState.SendingTelegrams);

        try {
            telegramSender.startSending(properties.getRecipientsListBuilder());
        } catch (Exception ex) {
            outputText = ex.getMessage() + "\n";
            changeStateAndInformListener(TelegrammerState.Idle);
        }
    }

    public void stopSendingTelegrams() {
        if (isStopSendingButtonEnabled()) {
            telegramSender.stopSending();
        }
    }

    public void clearOutput() {
        outputText = "";
        listener.refreshOutput();
    }

    public void addNewFilter() {
        if (!isAddFilterButtonEnabled()) {
            return;
        }
        outputText = "updating recipient list...\n";
        changeStateAndInformListener(TelegrammerState.CompilingRecipients);

        var parsedFilterParams = StringFunctions.stringToHashSet(filterParameters);
        var selectedFilterType = RecipientsFilterType.values()[selectedFilterTypeIndex];
        var selectedProviderType = RecipientsProviderType.values()[selectedProviderTypeIndex];
        var filter = filterTranslator.toFilter(selectedFilterType, selectedProviderType, parsedFilterParams);

        compileRecipientsWorker = new Thread(() -> {
            try {
                filter.refreshFilter();
                properties.getRecipientsListBuilder().addFilter(filter);
                outputText = getDurationText();
            } catch (Exception | OutOfMemoryError ex) {
                outputText = createFailedFilterRefreshMessage(filter, ex);
            } finally {
                changeStateAndInformListener(TelegrammerState.Idle);
            }
        });
        compileRecipientsWorker.start();
    }

    public void removeSelectedFilter() {
        if (isRemoveFilterButtonEnabled()) {
            properties.getRecipientsListBuilder().removeFilterAt(selectedConfiguredRecipientsFilterIndex);
            selectedConfiguredRecipientsFilterIndex = Math.max(0, --selectedConfiguredRecipientsFilterIndex);
            outputText = getDurationText();
            listener.refreshEverything();
        }
    }

    public void performPreCloseActions() {
        propertiesManager.saveProperties(properties);
    }

    @Override
    public void handleNoRecipientsFound(NoRecipientsFoundEvent event) {
        outputText += createTimestampedMessage(
                "no new recipients found, timing out for " + event.getTimeOut() / 1000 + " seconds...");
        listener.refreshOutput();
    }

    @Override
    public void handleRecipientRemoved(RecipientRemovedEvent event) {
        if (!properties.isHideSkippedRecipients()) {
            outputText += createTimestampedMessage(
                    "skipping recipient '" + event.getRecipient() + "': " + event.getReason());
            listener.refreshOutput();
        }
    }

    @Override
    public void handleRecipientsRefreshed(RecipientsRefreshedEvent event) {
        outputText += createTimestampedMessage("updated recipients list");
        event.getFailedFilters().forEach((filter, ex) -> outputText += createFailedFilterRefreshMessage(filter, ex));
        listener.refreshOutput();
    }

    @Override
    public void handleStoppedSending(StoppedSendingEvent event) {
        outputText += BORDER + "\nfinished"
                + (event.isCausedByError() ? " with error: " + event.getErrorMsg() + "\n" : " without fatal errors\n")
                + "telegrams queued: " + event.getQueuedSucces() + "\n" + "blocked by category: "
                + event.getRecipientIsBlocking() + "\n" + "recipients not found: " + event.getRecipientDidntExist()
                + "\n" + "failed b/c other reasons: " + event.getDisconnectOrOtherReason() + "\n" + BORDER + "\n";
        changeStateAndInformListener(TelegrammerState.Idle);
    }

    @Override
    public void handleTelegramSent(TelegramSentEvent event) {
        event.getException().ifPresentOrElse(exception -> {
            outputText += createTimestampedMessage(
                    "failed to queue telegram for '" + event.getRecipient() + "': " + exception.getMessage());
        }, () -> {
            outputText += createTimestampedMessage("queued telegram for '" + event.getRecipient() + "'");
        });
        listener.refreshOutput();
    }

    private void changeStateAndInformListener(TelegrammerState newState) {
        state = newState;
        if (listener != null) {
            listener.refreshEverything();
        }
    }

    private String getDurationText() {
        var recipients = properties.getRecipientsListBuilder().getRecipients();
        int timePerTelegram = properties.getLastTelegramType() == TelegramType.RECRUITMENT ? 180050 : 30050;
        int estimatedDuration = Math.max(recipients.size() - 1, 0) * (timePerTelegram / 1000);
        int hours = estimatedDuration / 3600;
        int minutes = estimatedDuration % 3600 / 60;
        int seconds = estimatedDuration % 3600 % 60;
        return String.format(BORDER + "%naddressees selected: %s%nestimated duration: "
                + "%s hours, %s minutes, %s seconds%n" + BORDER + "%n", recipients.size(), hours, minutes, seconds);
    }

    private String removeWhiteSpaces(String target) {
        return target.replace(" ", "");
    }

    private String createTimestampedMessage(String msg) {
        return "[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + msg + "\n";
    }

    private String createFailedFilterRefreshMessage(RecipientsFilter filter, Throwable ex) {
        return createTimestampedMessage(
                "error while refreshing filter '" + filter.toString() + "' : " + ex.getMessage());
    }
}
