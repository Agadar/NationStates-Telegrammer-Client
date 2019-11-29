package com.github.agadar.telegrammer.client.viewmodel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.client.settings.TelegrammerClientSettings;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.misc.StringFunctions;
import com.github.agadar.telegrammer.core.misc.TelegramType;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.sender.TelegramSender;
import com.github.agadar.telegrammer.core.settings.TelegrammerCoreSettings;

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
public class TelegrammerViewModel implements TelegramManagerListener {

    private final RecipientsFilterTranslator filterTranslator;
    private final TelegramSender telegramSender;
    private final OutputTextCreator outputTextCreator;
    private final TelegrammerCoreSettings coreSettings;
    private final TelegrammerClientSettings clientSettings;

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
    private TelegrammerState state = TelegrammerState.CompilingRecipients;

    public TelegrammerViewModel(@NonNull TelegramSender telegramSender,
            @NonNull TelegrammerCoreSettings coreSettings,
            @NonNull TelegrammerClientSettings clientSettings,
            @NonNull RecipientsFilterTranslator filterTranslator,
            @NonNull OutputTextCreator outputTextCreator) {

        this.telegramSender = telegramSender;
        this.coreSettings = coreSettings;
        this.clientSettings = clientSettings;
        this.filterTranslator = filterTranslator;
        this.outputTextCreator = outputTextCreator;

        telegramSender.addListeners(this);
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
            changeStateAndInformListener(TelegrammerState.Idle);

            if (clientSettings.getStartSendingOnStartup()) {
                this.startSendingTelegrams();
            }
        });
    }

    public void setListener(TelegrammerViewModelListener listener) {
        this.listener = listener;
        listener.refreshEverything();
    }

    public boolean isOptionsMenuEnabled() {
        return state == TelegrammerState.Idle;
    }

    public boolean getHideSkippedRecipients() {
        return clientSettings.getHideSkippedRecipients();
    }

    public void setHideSkippedRecipients(boolean value) {
        if (isOptionsMenuEnabled()) {
            clientSettings.setHideSkippedRecipients(value);
        }
    }

    public boolean getRefreshRecipientsAfterEveryTelegram() {
        return coreSettings.getUpdateAfterEveryTelegram();
    }

    public void setRefreshRecipientsAfterEveryTelegram(boolean value) {
        if (isOptionsMenuEnabled()) {
            coreSettings.setUpdateAfterEveryTelegram(value);
        }
    }

    public boolean getRunIndefinitely() {
        return coreSettings.getRunIndefinitely();
    }

    public void setRunIndefinitely(boolean value) {
        if (isOptionsMenuEnabled()) {
            coreSettings.setRunIndefinitely(value);
        }
    }

    public boolean getStartMinimized() {
        return clientSettings.getStartMinimized();
    }

    public void setStartMinimized(boolean value) {
        if (isOptionsMenuEnabled()) {
            clientSettings.setStartMinimized(value);
        }
    }

    public boolean getStartSendingOnStartup() {
        return clientSettings.getStartSendingOnStartup();
    }

    public void setStartSendingOnStartup(boolean value) {
        if (isOptionsMenuEnabled()) {
            clientSettings.setStartSendingOnStartup(value);
        }
    }

    public boolean isClientKeyInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String getClientKey() {
        return coreSettings.getClientKey();
    }

    public void setClientKey(String value) {
        if (isClientKeyInputEnabled()) {
            coreSettings.setClientKey(value);
        }
    }

    public boolean isTelegramIdInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String getTelegramId() {
        return coreSettings.getTelegramId();
    }

    public void setTelegramId(String value) {
        if (isTelegramIdInputEnabled()) {
            coreSettings.setTelegramId(value);
            outputText = outputTextCreator.createExpectedDurationMessage();
            listener.refreshOutput();
        }
    }

    public boolean isSecretKeyInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String getSecretKey() {
        return coreSettings.getSecretKey();
    }

    public void setSecretKey(String value) {
        if (isSecretKeyInputEnabled()) {
            coreSettings.setSecretKey(value);
        }
    }

    public boolean isAvailableTelegramTypesEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String[] getAvailableTelegramTypes() {
        return Arrays.stream(TelegramType.values()).map(telegramType -> telegramType.toString()).toArray(String[]::new);
    }

    public int getSelectedTelegramTypeIndex() {
        var telegramType = coreSettings.getTelegramType();
        return Arrays.asList(TelegramType.values()).indexOf(telegramType);
    }

    public void setSelectedTelegramTypeIndex(int value) {
        if (isAvailableTelegramTypesEnabled() && value != getSelectedTelegramTypeIndex()) {
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
        return state == TelegrammerState.Idle
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

    public boolean isConfiguredRecipientsFiltersEnabled() {
        return state == TelegrammerState.Idle;
    }

    public List<String> getConfiguredRecipientsFilters() {
        return coreSettings.getFilters().getFilters().stream()
                .map(filter -> filter.toString())
                .collect(Collectors.toList());
    }

    public void setSelectedConfiguredRecipientsFilter(int index) {
        if (isConfiguredRecipientsFiltersEnabled() && index != selectedConfiguredRecipientsFilterIndex) {
            selectedConfiguredRecipientsFilterIndex = index;
            listener.refreshEverything();
        }
    }

    public void unsetSelectedConfiguredRecipientsFilter() {
        if (isConfiguredRecipientsFiltersEnabled() && selectedConfiguredRecipientsFilterIndex > -1) {
            selectedConfiguredRecipientsFilterIndex = -1;
            listener.refreshEverything();
        }
    }

    public boolean isAvailableFilterActionsInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public RecipientsFilterAction[] getAvailableFilterActions() {
        return RecipientsFilterAction.values();
    }

    public void setSelectedFilterAction(RecipientsFilterAction filterAction) {
        if (isAvailableFilterActionsInputEnabled() && selectedFilterAction != filterAction) {
            selectedFilterAction = filterAction;
        }
    }

    public boolean isAvailableFilterTypesInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public RecipientsFilterType[] getAvailableFilterTypes() {
        return Arrays.stream(RecipientsFilterType.values())
                .filter(this::filterTypeSupportsSelectedFilterAction)
                .toArray(RecipientsFilterType[]::new);
    }

    public void setSelectedFilterType(RecipientsFilterType filterType) {
        if (isAvailableFilterTypesInputEnabled() && filterType != selectedFilterType) {
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

        coreSettings.setFromRegion(coreSettings.getFromRegion().trim());
        coreSettings.setClientKey(removeWhiteSpaces(coreSettings.getClientKey()));
        coreSettings.setTelegramId(removeWhiteSpaces(coreSettings.getTelegramId()));
        coreSettings.setSecretKey(removeWhiteSpaces(coreSettings.getSecretKey()));

        changeStateAndInformListener(TelegrammerState.SendingTelegrams);
        try {
            telegramSender.startSending(coreSettings);

        } catch (Exception ex) {
            if (!(ex instanceof IllegalArgumentException)) {
                log.error("An error occured while starting sending telegrams", ex);
            }
            outputText += outputTextCreator.createTimestampedMessage(ex.getMessage());
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
        var filter = filterTranslator.toFilter(selectedFilterType, selectedFilterAction, parsedFilterParams);

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
                changeStateAndInformListener(TelegrammerState.Idle);
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
        return state == TelegrammerState.Idle && coreSettings.getFilters().getFilters().size() > 0;
    }

    public void refreshFilters() {
        if (!isRefreshFiltersButtonEnabled()) {
            return;
        }
        outputText = "updating recipient list...\n";
        changeStateAndInformListener(TelegrammerState.CompilingRecipients);

        compileRecipientsExecutor.execute(() -> {
            var failedFilters = coreSettings.getFilters().refreshFilters();

            if (!failedFilters.isEmpty()) {
                outputText = outputTextCreator.createTimestampedMessage("updated recipients list");
                failedFilters.forEach(
                        (filter, ex) -> outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex));
            } else {
                outputText = outputTextCreator.createExpectedDurationMessage();
            }
            changeStateAndInformListener(TelegrammerState.Idle);
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
        changeStateAndInformListener(TelegrammerState.Idle);
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

    private void changeStateAndInformListener(TelegrammerState newState) {
        state = newState;
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
