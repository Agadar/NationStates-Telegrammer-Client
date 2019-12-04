package com.github.agadar.telegrammer.client.viewmodel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.client.event.RefreshEverythingEvent;
import com.github.agadar.telegrammer.client.event.RefreshOutputEvent;
import com.github.agadar.telegrammer.client.settings.ClientSettings;
import com.github.agadar.telegrammer.core.Telegrammer;
import com.github.agadar.telegrammer.core.TelegrammerListener;
import com.github.agadar.telegrammer.core.event.FilterRemovedEvent;
import com.github.agadar.telegrammer.core.event.FinishedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.SettingsUpdatedEvent;
import com.github.agadar.telegrammer.core.event.StartedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StartedSendingEvent;
import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.misc.StringFunctions;
import com.github.agadar.telegrammer.core.misc.TelegramType;
import com.github.agadar.telegrammer.core.misc.TelegrammerState;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.settings.CoreSettingKey;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * The abstract representation of the UI, exposing all information needed to
 * construct an arbitrary framework-specific view that implements the
 * {@link TelegrammerViewModelListener}.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
@Slf4j
public class TelegrammerViewModel implements TelegrammerListener {

    private final Telegrammer telegrammer;
    private final OutputTextCreator outputTextCreator;
    private final ClientSettings clientSettings;

    @Getter
    private volatile String outputText = "";
    @Getter
    private volatile TelegramType telegramType = TelegramType.NORMAL;
    @Getter
    private volatile RecipientsFilterAction selectedFilterAction = RecipientsFilterAction.ADD_TO_RECIPIENTS;
    @Getter
    private volatile RecipientsFilterType selectedFilterType = RecipientsFilterType.ALL_NATIONS;
    @Getter
    private volatile int selectedFilterIndex = -1;
    @Getter
    private volatile String filterParameters = "";
    @Getter
    private volatile List<String> filters = Collections.emptyList();
    @Getter
    private volatile boolean runIndefinitely = false;
    @Getter
    private volatile boolean refreshRecipientsAfterEveryTelegram = false;
    @Getter
    private volatile String telegramId = "";
    @Getter
    private volatile String secretKey = "";
    @Getter
    private volatile String clientKey = "";
    @Getter
    private volatile String forRegion = "";

    private volatile TelegrammerViewModelListener listener = null;
    private volatile int numberOfRecipients = 0;
    private volatile boolean refreshedRecipientsAtLeastOnce = false;
    private volatile TelegrammerState telegrammerState = TelegrammerState.IDLE;

    public TelegrammerViewModel(@NonNull Telegrammer telegrammer,
            @NonNull ClientSettings clientSettings,
            @NonNull OutputTextCreator outputTextCreator) {

        this.telegrammer = telegrammer;
        this.clientSettings = clientSettings;
        this.outputTextCreator = outputTextCreator;
    }

    /**
     * Initialises this viewmodel, starting by registering the given listener.
     * 
     * @param listener The listener to this viewmodel.
     */
    public void initialise(@NonNull TelegrammerViewModelListener listener) {
        this.listener = listener;
        telegrammer.addListeners(this);
        telegrammer.refreshFilters();
    }

    public boolean isTelegrammerIdle() {
        return telegrammerState == TelegrammerState.IDLE;
    }

    public boolean isTelegrammerQueuing() {
        return telegrammerState == TelegrammerState.QUEUING_TELEGRAMS;
    }

    public boolean getHideSkippedRecipients() {
        return clientSettings.getHideSkippedRecipients();
    }

    public void setHideSkippedRecipients(boolean value) {
        clientSettings.setHideSkippedRecipients(value);
    }

    public boolean getStartMinimized() {
        return clientSettings.getStartMinimized();
    }

    public void setStartMinimized(boolean value) {
        clientSettings.setStartMinimized(value);
    }

    public boolean getStartSendingOnStartup() {
        return clientSettings.getStartSendingOnStartup();
    }

    public void setStartSendingOnStartup(boolean value) {
        clientSettings.setStartSendingOnStartup(value);
    }

    public void setRefreshRecipientsAfterEveryTelegram(boolean value) {
        telegrammer.updateSetting(CoreSettingKey.UPDATE_AFTER_EVERY_TELEGRAM, value);
    }

    public void setRunIndefinitely(boolean value) {
        telegrammer.updateSetting(CoreSettingKey.RUN_INDEFINITELY, value);
    }

    public void setClientKey(String value) {
        telegrammer.updateSetting(CoreSettingKey.CLIENT_KEY, value);
    }

    public void setTelegramId(String value) {
        telegrammer.updateSetting(CoreSettingKey.TELEGRAM_ID, value);
    }

    public void setSecretKey(String value) {
        telegrammer.updateSetting(CoreSettingKey.SECRET_KEY, value);
    }

    public void setForRegion(String value) {
        telegrammer.updateSetting(CoreSettingKey.FROM_REGION, value);
    }

    public String[] getAvailableTelegramTypes() {
        return Arrays.stream(TelegramType.values())
                .map(telegramType -> telegramType.toString())
                .toArray(String[]::new);
    }

    public int getSelectedTelegramTypeIndex() {
        return Arrays.asList(TelegramType.values()).indexOf(telegramType);
    }

    public void setSelectedTelegramTypeIndex(int value) {
        if (value != getSelectedTelegramTypeIndex()) {
            var telegramType = TelegramType.values()[value];
            telegrammer.updateSetting(CoreSettingKey.TELEGRAM_TYPE, telegramType);

            if (telegramType != TelegramType.RECRUITMENT && telegramType != TelegramType.CAMPAIGN) {
                telegrammer.updateSetting(CoreSettingKey.FROM_REGION, "");
            }
        }
    }

    public boolean isForRegionInputEnabled() {
        return isTelegrammerIdle()
                && (telegramType == TelegramType.RECRUITMENT || telegramType == TelegramType.CAMPAIGN);
    }

    public void setSelectedConfiguredRecipientsFilter(int index) {
        if (index != selectedFilterIndex) {
            selectedFilterIndex = index;
            listener.onRefreshEverything(new RefreshEverythingEvent(this));
        }
    }

    public void unsetSelectedConfiguredRecipientsFilter() {
        if (selectedFilterIndex > -1) {
            selectedFilterIndex = -1;
            listener.onRefreshEverything(new RefreshEverythingEvent(this));
        }
    }

    public RecipientsFilterAction[] getAvailableFilterActions() {
        return RecipientsFilterAction.values();
    }

    public void setSelectedFilterAction(RecipientsFilterAction filterAction) {
        if (selectedFilterAction != filterAction) {
            selectedFilterAction = filterAction;
            var firstFilterType = RecipientsFilterType.values()[0];

            if (selectedFilterType != firstFilterType) {
                setSelectedFilterType(firstFilterType);
            }
        }
    }

    public RecipientsFilterType[] getAvailableFilterTypes() {
        return Arrays.stream(RecipientsFilterType.values())
                .filter(this::filterTypeSupportsSelectedFilterAction)
                .toArray(RecipientsFilterType[]::new);
    }

    public void setSelectedFilterType(RecipientsFilterType filterType) {
        if (filterType != selectedFilterType) {
            selectedFilterType = filterType;
            filterParameters = "";
            listener.onRefreshEverything(new RefreshEverythingEvent(this));
        }
    }

    public boolean isFilterParametersInputEnabled() {
        return selectedFilterType.isAllowsInput();
    }

    public void setFilterParameters(String value) {
        if (isFilterParametersInputEnabled()) {
            filterParameters = value;
        }
    }

    public String getFilterParametersHint() {
        return selectedFilterType.getInputHint();
    }

    public boolean isRemoveFilterButtonEnabled() {
        return isTelegrammerIdle() && selectedFilterIndex > -1;
    }

    public void startSendingTelegrams() {
        try {
            telegrammer.startSending();

        } catch (Exception ex) {
            if (!(ex instanceof IllegalArgumentException)) {
                log.error("An error occured while starting sending telegrams", ex);
            }
            outputText += outputTextCreator.createTimestampedMessage(ex.getMessage());
        }
    }

    public void stopSendingTelegrams() {
        telegrammer.stopSending();
    }

    public void clearOutput() {
        outputText = "";
        listener.onRefreshOutput(new RefreshOutputEvent(this));
    }

    public void addNewFilter() {
        var parsedFilterParams = StringFunctions.stringToHashSet(filterParameters);
        telegrammer.addFilter(selectedFilterType, selectedFilterAction, parsedFilterParams);
    }

    public void removeSelectedFilter() {
        telegrammer.removeFilterAtIndex(selectedFilterIndex);
    }

    public boolean isRefreshFiltersButtonEnabled() {
        return isTelegrammerIdle() && filters.size() > 0;
    }

    public void refreshFilters() {
        telegrammer.refreshFilters();
    }

    public String getTitle() {
        String version = getClass().getPackage().getImplementationVersion();
        version = version == null ? "[DEVELOPMENT VERSION]" : version;
        return String.format("Agadar's NationStates Telegrammer Client %s", version);
    }

    @Override
    public void handleSettingsUpdated(SettingsUpdatedEvent event) {
        telegramType = event.getTelegramType();
        filters = event.getFilters();
        runIndefinitely = event.isRunIndefinitely();
        refreshRecipientsAfterEveryTelegram = event.isUpdateAfterEveryTelegram();
        telegramId = event.getTelegramId();
        secretKey = event.getSecretKey();
        clientKey = event.getClientKey();
        forRegion = event.getFromRegion();
        numberOfRecipients = event.getNumberOfRecipients();
        outputText = outputTextCreator.createExpectedDurationMessage(numberOfRecipients, telegramType);
        listener.onRefreshEverything(new RefreshEverythingEvent(this));
    }

    @Override
    public void handleStartedRefreshingRecipients(StartedRefreshingRecipientsEvent event) {
        if (event.getTelegrammerState() == TelegrammerState.REFRESHING_RECIPIENTS) {
            outputText = "updating recipient list...\n";
            telegrammerState = event.getTelegrammerState();
            listener.onRefreshEverything(new RefreshEverythingEvent(this));
        }
    }

    @Override
    public void handleFinishedRefreshingRecipients(FinishedRefreshingRecipientsEvent event) {
        numberOfRecipients = event.getNumberOfRecipients();
        filters = event.getFilters();

        if (event.getTelegrammerState() == TelegrammerState.QUEUING_TELEGRAMS) {
            outputText += outputTextCreator.createTimestampedMessage("updated recipients list");
            event.getFailedFilters()
                    .forEach((filter,
                            ex) -> outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex));
            listener.onRefreshOutput(new RefreshOutputEvent(this));

        } else if (event.getTelegrammerState() == TelegrammerState.REFRESHING_RECIPIENTS) {
            if (!event.getFailedFilters().isEmpty()) {
                outputText = outputTextCreator.createTimestampedMessage("updated recipients list");
                event.getFailedFilters().forEach(
                        (filter, ex) -> outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex));
            } else {
                outputText = outputTextCreator.createExpectedDurationMessage(numberOfRecipients, telegramType);
            }
            telegrammerState = TelegrammerState.IDLE;
            listener.onRefreshEverything(new RefreshEverythingEvent(this));

            if (clientSettings.getStartSendingOnStartup() && !refreshedRecipientsAtLeastOnce) {
                this.startSendingTelegrams();
            }
        }
        refreshedRecipientsAtLeastOnce = true;
    }

    @Override
    public void handleFilterRemoved(FilterRemovedEvent event) {
        selectedFilterIndex = -1;
        filters = event.getFilters();
        numberOfRecipients = event.getNumberOfRecipients();
        outputText = outputTextCreator.createExpectedDurationMessage(numberOfRecipients, telegramType);
        listener.onRefreshEverything(new RefreshEverythingEvent(this));
    }

    @Override
    public void handleStartedSending(StartedSendingEvent event) {
        telegrammerState = TelegrammerState.QUEUING_TELEGRAMS;
        listener.onRefreshEverything(new RefreshEverythingEvent(this));
    }

    @Override
    public void handleTelegramSent(TelegramSentEvent event) {
        event.getException().ifPresentOrElse(exception -> {
            outputText += outputTextCreator.createTimestampedMessage(
                    "failed to queue telegram for '" + event.getRecipient() + "': " + exception.getMessage());
        }, () -> {
            outputText += outputTextCreator
                    .createTimestampedMessage("queued telegram for '" + event.getRecipient() + "'");
        });
        listener.onRefreshOutput(new RefreshOutputEvent(this));
    }

    @Override
    public void handleNoRecipientsFound(NoRecipientsFoundEvent event) {
        outputText += outputTextCreator.createTimestampedMessage(
                "no new recipients found, timing out for " + event.getTimeOut() / 1000 + " seconds...");
        listener.onRefreshOutput(new RefreshOutputEvent(this));
    }

    @Override
    public void handleRecipientRemoved(RecipientRemovedEvent event) {
        if (!clientSettings.getHideSkippedRecipients()) {
            outputText += outputTextCreator.createTimestampedMessage(
                    "skipping recipient '" + event.getRecipient() + "': " + event.getReason());
            listener.onRefreshOutput(new RefreshOutputEvent(this));
        }
    }

    @Override
    public void handleStoppedSending(StoppedSendingEvent event) {
        telegrammerState = TelegrammerState.IDLE;
        outputText += outputTextCreator.createStoppedSendingMessage(event);
        listener.onRefreshEverything(new RefreshEverythingEvent(this));
    }

    private boolean filterTypeSupportsSelectedFilterAction(RecipientsFilterType filterType) {
        return filterType.getSupportedActions().stream()
                .anyMatch(supportedAction -> supportedAction == selectedFilterAction);
    }

}
