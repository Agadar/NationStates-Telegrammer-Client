package com.github.agadar.telegrammer.client.viewmodel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.client.settings.ClientSettings;
import com.github.agadar.telegrammer.core.Telegrammer;
import com.github.agadar.telegrammer.core.TelegrammerListener;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.event.StartedCompilingRecipientsEvent;
import com.github.agadar.telegrammer.core.event.StartedSendingEvent;
import com.github.agadar.telegrammer.core.event.StoppedCompilingRecipientsEvent;
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
    private int selectedConfiguredRecipientsFilterIndex = -1;
    @Getter
    private String filterParameters = "";
    @Getter
    private String outputText = "";

    private TelegrammerViewModelListener listener = null;
    private Executor compileRecipientsExecutor = Executors.newSingleThreadExecutor();

    public TelegrammerViewModel(@NonNull Telegrammer telegrammer,
            @NonNull ClientSettings clientSettings,
            @NonNull OutputTextCreator outputTextCreator) {

        this.telegrammer = telegrammer;
        this.clientSettings = clientSettings;
        this.outputTextCreator = outputTextCreator;
        coreSettings = telegrammer.getCoreSettings();

        telegrammer.addListeners(this);
        outputText = "updating recipient list...\n";

        compileRecipientsExecutor.execute(() -> {
            var failedFilters = coreSettings.getFilters().refreshFilters();

            if (!failedFilters.isEmpty()) {
                outputText = outputTextCreator.createTimestampedMessage("updated recipients list");
                failedFilters.forEach(
                        (filter, ex) -> outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex));
            } else {
                outputText = outputTextCreator.createExpectedDurationMessage();
            }
            changeStateAndInformListener(TelegrammerState.IDLE);

            if (clientSettings.getStartSendingOnStartup()) {
                this.startSendingTelegrams();
            }
        });
    }

    public void setListener(TelegrammerViewModelListener listener) {
        this.listener = listener;
        listener.refreshEverything();
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
        if (isTelegrammerIdle()) {
            clientSettings.setHideSkippedRecipients(value);
        }
    }

    public boolean getRefreshRecipientsAfterEveryTelegram() {
        return coreSettings.getUpdateAfterEveryTelegram();
    }

    public void setRefreshRecipientsAfterEveryTelegram(boolean value) {
        if (isTelegrammerIdle()) {
            coreSettings.setUpdateAfterEveryTelegram(value);
        }
    }

    public boolean getRunIndefinitely() {
        return coreSettings.getRunIndefinitely();
    }

    public void setRunIndefinitely(boolean value) {
        if (isTelegrammerIdle()) {
            coreSettings.setRunIndefinitely(value);
        }
    }

    public boolean getStartMinimized() {
        return clientSettings.getStartMinimized();
    }

    public void setStartMinimized(boolean value) {
        if (isTelegrammerIdle()) {
            clientSettings.setStartMinimized(value);
        }
    }

    public boolean getStartSendingOnStartup() {
        return clientSettings.getStartSendingOnStartup();
    }

    public void setStartSendingOnStartup(boolean value) {
        if (isTelegrammerIdle()) {
            clientSettings.setStartSendingOnStartup(value);
        }
    }

    public String getClientKey() {
        return coreSettings.getClientKey();
    }

    public void setClientKey(String value) {
        if (isTelegrammerIdle()) {
            coreSettings.setClientKey(value);
        }
    }

    public String getTelegramId() {
        return coreSettings.getTelegramId();
    }

    public void setTelegramId(String value) {
        if (isTelegrammerIdle()) {
            coreSettings.setTelegramId(value);
            outputText = outputTextCreator.createExpectedDurationMessage();
            listener.refreshOutput();
        }
    }

    public String getSecretKey() {
        return coreSettings.getSecretKey();
    }

    public void setSecretKey(String value) {
        if (isTelegrammerIdle()) {
            coreSettings.setSecretKey(value);
        }
    }

    public String[] getAvailableTelegramTypes() {
        return Arrays.stream(TelegramType.values()).map(telegramType -> telegramType.toString()).toArray(String[]::new);
    }

    public int getSelectedTelegramTypeIndex() {
        var telegramType = coreSettings.getTelegramType();
        return Arrays.asList(TelegramType.values()).indexOf(telegramType);
    }

    public void setSelectedTelegramTypeIndex(int value) {
        if (isTelegrammerIdle() && value != getSelectedTelegramTypeIndex()) {
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
        if (isForRegionInputEnabled()) {
            coreSettings.setFromRegion(value);
        }
    }

    public List<String> getConfiguredRecipientsFilters() {
        return coreSettings.getFilters().getFilters().stream()
                .map(filter -> filter.toString())
                .collect(Collectors.toList());
    }

    public void setSelectedConfiguredRecipientsFilter(int index) {
        if (isTelegrammerIdle() && index != selectedConfiguredRecipientsFilterIndex) {
            selectedConfiguredRecipientsFilterIndex = index;
            listener.refreshEverything();
        }
    }

    public void unsetSelectedConfiguredRecipientsFilter() {
        if (isTelegrammerIdle() && selectedConfiguredRecipientsFilterIndex > -1) {
            selectedConfiguredRecipientsFilterIndex = -1;
            listener.refreshEverything();
        }
    }

    public RecipientsFilterAction[] getAvailableFilterActions() {
        return RecipientsFilterAction.values();
    }

    public void setSelectedFilterAction(RecipientsFilterAction filterAction) {
        if (isTelegrammerIdle() && selectedFilterAction != filterAction) {
            selectedFilterAction = filterAction;
        }
    }

    public RecipientsFilterType[] getAvailableFilterTypes() {
        return Arrays.stream(RecipientsFilterType.values())
                .filter(this::filterTypeSupportsSelectedFilterAction)
                .toArray(RecipientsFilterType[]::new);
    }

    public void setSelectedFilterType(RecipientsFilterType filterType) {
        if (isTelegrammerIdle() && filterType != selectedFilterType) {
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
        return isTelegrammerIdle() && selectedConfiguredRecipientsFilterIndex > -1;
    }

    public void startSendingTelegrams() {
        if (!isTelegrammerIdle()) {
            return;
        }

        coreSettings.setFromRegion(coreSettings.getFromRegion().trim());
        coreSettings.setClientKey(removeWhiteSpaces(coreSettings.getClientKey()));
        coreSettings.setTelegramId(removeWhiteSpaces(coreSettings.getTelegramId()));
        coreSettings.setSecretKey(removeWhiteSpaces(coreSettings.getSecretKey()));

        changeStateAndInformListener(TelegrammerState.QUEUING_TELEGRAMS);
        try {
            telegrammer.startSending();

        } catch (Exception ex) {
            if (!(ex instanceof IllegalArgumentException)) {
                log.error("An error occured while starting sending telegrams", ex);
            }
            outputText += outputTextCreator.createTimestampedMessage(ex.getMessage());
            changeStateAndInformListener(TelegrammerState.IDLE);
        }
    }

    public void stopSendingTelegrams() {
        if (isTelegrammerQueuing()) {
            telegrammer.stopSending();
        }
    }

    public void clearOutput() {
        outputText = "";
        listener.refreshOutput();
    }

    public void addNewFilter() {
        if (!isTelegrammerIdle()) {
            return;
        }
        outputText = "updating recipient list...\n";
        changeStateAndInformListener(TelegrammerState.COMPILING_RECIPIENTS);

        var parsedFilterParams = StringFunctions.stringToHashSet(filterParameters);
        var filter = telegrammer.createFilter(selectedFilterType, selectedFilterAction, parsedFilterParams);

        compileRecipientsExecutor.execute(() -> {
            try {
                filter.refreshFilter();
                coreSettings.getFilters().addFilter(filter);
                outputText = outputTextCreator.createExpectedDurationMessage();
            } catch (Exception | OutOfMemoryError ex) {
                log.error("An error occured while refreshing the filters", ex);
                outputText = outputTextCreator.createTimestampedMessage("updated recipients list");
                outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex);
            } finally {
                changeStateAndInformListener(TelegrammerState.IDLE);
            }
        });
    }

    public void removeSelectedFilter() {
        if (isRemoveFilterButtonEnabled()) {
            coreSettings.getFilters().removeFilterAt(selectedConfiguredRecipientsFilterIndex);
            --selectedConfiguredRecipientsFilterIndex;
            outputText = outputTextCreator.createExpectedDurationMessage();
            listener.refreshEverything();
        }
    }

    public boolean isRefreshFiltersButtonEnabled() {
        return isTelegrammerIdle() && coreSettings.getFilters().getFilters().size() > 0;
    }

    public void refreshFilters() {
        if (!isRefreshFiltersButtonEnabled()) {
            return;
        }
        outputText = "updating recipient list...\n";
        changeStateAndInformListener(TelegrammerState.COMPILING_RECIPIENTS);

        compileRecipientsExecutor.execute(() -> {
            var failedFilters = coreSettings.getFilters().refreshFilters();

            if (!failedFilters.isEmpty()) {
                outputText = outputTextCreator.createTimestampedMessage("updated recipients list");
                failedFilters.forEach(
                        (filter, ex) -> outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex));
            } else {
                outputText = outputTextCreator.createExpectedDurationMessage();
            }
            changeStateAndInformListener(TelegrammerState.IDLE);
        });
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
    public void handleStartedCompilingRecipients(StartedCompilingRecipientsEvent event) {
        // TODO Implementation.
    }

    @Override
    public void handleFinishedCompilingRecipients(StoppedCompilingRecipientsEvent event) {
        // TODO Implementation.
    }

    @Override
    public void handleStartedSending(StartedSendingEvent event) {
        // TODO Implementation.
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
    public void handleRecipientsRefreshed(RecipientsRefreshedEvent event) {
        outputText += outputTextCreator.createTimestampedMessage("updated recipients list");
        event.getFailedFilters()
                .forEach((filter, ex) -> outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex));
        listener.refreshOutput();
    }

    @Override
    public void handleStoppedSending(StoppedSendingEvent event) {
        outputText += outputTextCreator.createStoppedSendingMessage(event);
        changeStateAndInformListener(TelegrammerState.IDLE);
    }

    private void changeStateAndInformListener(TelegrammerState newState) {
        telegrammer.setState(newState);
        if (listener != null) {
            listener.refreshEverything();
        }
    }

    private String removeWhiteSpaces(String target) {
        return target.replace(" ", "");
    }

    private boolean filterTypeSupportsSelectedFilterAction(RecipientsFilterType filterType) {
        return Arrays.stream(filterType.getSupportedActions())
                .anyMatch(supportedAction -> supportedAction == selectedFilterAction);
    }

}
