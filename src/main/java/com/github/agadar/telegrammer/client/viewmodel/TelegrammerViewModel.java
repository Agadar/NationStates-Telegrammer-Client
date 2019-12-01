package com.github.agadar.telegrammer.client.viewmodel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.client.settings.ClientSettings;
import com.github.agadar.telegrammer.core.Telegrammer;
import com.github.agadar.telegrammer.core.TelegrammerListener;
import com.github.agadar.telegrammer.core.event.FilterRemovedEvent;
import com.github.agadar.telegrammer.core.event.FinishedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.StartedRefreshingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StartedSendingEvent;
import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.misc.StringFunctions;
import com.github.agadar.telegrammer.core.misc.TelegramType;
import com.github.agadar.telegrammer.core.misc.TelegrammerState;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.settings.CoreSettings;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * The abstract representation of the GUI, exposing all information needed to
 * construct an arbitrary framework-specific view that implements the
 * TelegrammerViewModelListener.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
@Slf4j
public class TelegrammerViewModel implements TelegrammerListener {

    private final Telegrammer telegrammer;
    private final OutputTextCreator outputTextCreator;
    private final ClientSettings clientSettings;
    private final CoreSettings coreSettings;

    @Getter
    private RecipientsFilterAction selectedFilterAction = RecipientsFilterAction.ADD_TO_RECIPIENTS;
    @Getter
    private RecipientsFilterType selectedFilterType = RecipientsFilterType.ALL_NATIONS;
    @Getter
    private int selectedFilterIndex = -1;
    @Getter
    private String filterParameters = "";
    @Getter
    private volatile String outputText = "";
    private volatile boolean refreshedRecipientsAtLeastOnce = false;

    private volatile TelegrammerViewModelListener listener = null;

    public TelegrammerViewModel(@NonNull Telegrammer telegrammer,
            @NonNull ClientSettings clientSettings,
            @NonNull OutputTextCreator outputTextCreator) {

        this.telegrammer = telegrammer;
        this.clientSettings = clientSettings;
        this.outputTextCreator = outputTextCreator;
        coreSettings = telegrammer.getCoreSettings();
        telegrammer.addListeners(this);
    }

    /**
     * Initialises this viewmodel, starting by registering the given listener.
     * 
     * @param listener The listener to this viewmodel.
     */
    public void initialise(@NonNull TelegrammerViewModelListener listener) {
        this.listener = listener;
        telegrammer.refreshFilters();
    }

    public boolean isTelegrammerIdle() {
        return telegrammer.getState() == TelegrammerState.IDLE;
    }

    public boolean isTelegrammerQueuing() {
        return telegrammer.getState() == TelegrammerState.QUEUING_TELEGRAMS;
    }

    public boolean getHideSkippedRecipients() {
        return clientSettings.getHideSkippedRecipients();
    }

    public void setHideSkippedRecipients(boolean value) {
        clientSettings.setHideSkippedRecipients(value);
    }

    public boolean getRefreshRecipientsAfterEveryTelegram() {
        return coreSettings.getUpdateAfterEveryTelegram();
    }

    public void setRefreshRecipientsAfterEveryTelegram(boolean value) {
        coreSettings.setUpdateAfterEveryTelegram(value);
    }

    public boolean getRunIndefinitely() {
        return coreSettings.getRunIndefinitely();
    }

    public void setRunIndefinitely(boolean value) {
        coreSettings.setRunIndefinitely(value);
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

    public String getClientKey() {
        return coreSettings.getClientKey();
    }

    public void setClientKey(String value) {
        coreSettings.setClientKey(value);
    }

    public String getTelegramId() {
        return coreSettings.getTelegramId();
    }

    public void setTelegramId(String value) {
        coreSettings.setTelegramId(value);
        outputText = outputTextCreator.createExpectedDurationMessage();
        listener.refreshOutput();
    }

    public String getSecretKey() {
        return coreSettings.getSecretKey();
    }

    public void setSecretKey(String value) {
        coreSettings.setSecretKey(value);
    }

    public String[] getAvailableTelegramTypes() {
        return Arrays.stream(TelegramType.values())
                .map(telegramType -> telegramType.toString())
                .toArray(String[]::new);
    }

    public int getSelectedTelegramTypeIndex() {
        var telegramType = coreSettings.getTelegramType();
        return Arrays.asList(TelegramType.values()).indexOf(telegramType);
    }

    public void setSelectedTelegramTypeIndex(int value) {
        if (value != getSelectedTelegramTypeIndex()) {
            var telegramType = TelegramType.values()[value];
            coreSettings.setTelegramType(telegramType);
            outputText = outputTextCreator.createExpectedDurationMessage();

            if (telegramType != TelegramType.RECRUITMENT && telegramType != TelegramType.CAMPAIGN) {
                coreSettings.setFromRegion("");
            }
            listener.refreshEverything();
        }
    }

    public boolean isForRegionInputEnabled() {
        var telegramType = coreSettings.getTelegramType();
        return isTelegrammerIdle()
                && (telegramType == TelegramType.RECRUITMENT || telegramType == TelegramType.CAMPAIGN);
    }

    public String getForRegion() {
        return coreSettings.getFromRegion();
    }

    public void setForRegion(String value) {
        coreSettings.setFromRegion(value);
    }

    public List<String> getConfiguredFilters() {
        return coreSettings.getFilters().getFilters().stream()
                .map(filter -> filter.toString())
                .collect(Collectors.toList());
    }

    public void setSelectedConfiguredRecipientsFilter(int index) {
        if (index != selectedFilterIndex) {
            selectedFilterIndex = index;
            listener.refreshEverything();
        }
    }

    public void unsetSelectedConfiguredRecipientsFilter() {
        if (selectedFilterIndex > -1) {
            selectedFilterIndex = -1;
            listener.refreshEverything();
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
            listener.refreshEverything();
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
        listener.refreshOutput();
    }

    public void addNewFilter() {
        var parsedFilterParams = StringFunctions.stringToHashSet(filterParameters);
        telegrammer.addFilter(selectedFilterType, selectedFilterAction, parsedFilterParams);
    }

    public void removeSelectedFilter() {
        telegrammer.removeFilterAtIndex(selectedFilterIndex);
    }

    public boolean isRefreshFiltersButtonEnabled() {
        return isTelegrammerIdle() && coreSettings.getFilters().getFilters().size() > 0;
    }

    public void refreshFilters() {
        telegrammer.refreshFilters();
    }

    public void performPreCloseActions() {
        coreSettings.savePropertiesFile();
    }

    public String getTitle() {
        String version = getClass().getPackage().getImplementationVersion();
        version = version == null ? "[DEVELOPMENT VERSION]" : version;
        return String.format("Agadar's NationStates Telegrammer Client %s", version);
    }

    @Override
    public void handleStartedRefreshingRecipients(StartedRefreshingRecipientsEvent event) {
        if (event.getTelegrammerState() == TelegrammerState.REFRESHING_RECIPIENTS) {
            outputText = "updating recipient list...\n";
            listener.refreshEverything();
        }
    }

    @Override
    public void handleFinishedRefreshingRecipients(FinishedRefreshingRecipientsEvent event) {
        if (event.getTelegrammerState() == TelegrammerState.QUEUING_TELEGRAMS) {
            outputText += outputTextCreator.createTimestampedMessage("updated recipients list");
            event.getFailedFilters()
                    .forEach((filter,
                            ex) -> outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex));
            listener.refreshOutput();

        } else if (event.getTelegrammerState() == TelegrammerState.REFRESHING_RECIPIENTS) {
            if (!event.getFailedFilters().isEmpty()) {
                outputText = outputTextCreator.createTimestampedMessage("updated recipients list");
                event.getFailedFilters().forEach(
                        (filter, ex) -> outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex));
            } else {
                outputText = outputTextCreator.createExpectedDurationMessage();
            }
            listener.refreshEverything();

            if (clientSettings.getStartSendingOnStartup() && !refreshedRecipientsAtLeastOnce) {
                this.startSendingTelegrams();
            }
        }
        refreshedRecipientsAtLeastOnce = true;
    }

    @Override
    public void handleFilterRemoved(FilterRemovedEvent event) {
        selectedFilterIndex = Math.max(-1, selectedFilterIndex - 1);
        outputText = outputTextCreator.createExpectedDurationMessage();
        listener.refreshEverything();
    }

    @Override
    public void handleStartedSending(StartedSendingEvent event) {
        listener.refreshEverything();
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
        listener.refreshOutput();
    }

    @Override
    public void handleNoRecipientsFound(NoRecipientsFoundEvent event) {
        outputText += outputTextCreator.createTimestampedMessage(
                "no new recipients found, timing out for " + event.getTimeOut() / 1000 + " seconds...");
        listener.refreshOutput();
    }

    @Override
    public void handleRecipientRemoved(RecipientRemovedEvent event) {
        if (!clientSettings.getHideSkippedRecipients()) {
            outputText += outputTextCreator.createTimestampedMessage(
                    "skipping recipient '" + event.getRecipient() + "': " + event.getReason());
            listener.refreshOutput();
        }
    }

    @Override
    public void handleStoppedSending(StoppedSendingEvent event) {
        outputText += outputTextCreator.createStoppedSendingMessage(event);
        listener.refreshEverything();
    }

    private boolean filterTypeSupportsSelectedFilterAction(RecipientsFilterType filterType) {
        return filterType.getSupportedActions().stream()
                .anyMatch(supportedAction -> supportedAction == selectedFilterAction);
    }

}
