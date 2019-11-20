package com.github.agadar.telegrammer.client.viewmodel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientProperties;
import com.github.agadar.telegrammer.core.properties.manager.PropertiesManager;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
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
    private final PropertiesManager<TelegrammerClientProperties> propertiesManager;
    private final TelegramSender telegramSender;
    private final OutputTextCreator outputTextCreator;

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

    public TelegrammerViewModel(TelegramSender telegramSender,
            PropertiesManager<TelegrammerClientProperties> propertiesManager,
            RecipientsFilterTranslator filterTranslator, OutputTextCreator outputTextCreator) {

        this.telegramSender = telegramSender;
        this.propertiesManager = propertiesManager;
        this.filterTranslator = filterTranslator;
        this.outputTextCreator = outputTextCreator;

        telegramSender.addListeners(this);
        outputText = "updating recipient list...\n";

        compileRecipientsExecutor.execute(() -> {
            var properties = propertiesManager.getProperties();
            var failedFilters = properties.getRecipientsListBuilder().refreshFilters();

            if (!failedFilters.isEmpty()) {
                outputText = outputTextCreator.createTimestampedMessage("updated recipients list");
                failedFilters.forEach(
                        (filter, ex) -> outputText += outputTextCreator.createFailedFilterRefreshMessage(filter, ex));
            } else {
                outputText = outputTextCreator.createExpectedDurationMessage();
            }
            changeStateAndInformListener(TelegrammerState.Idle);

            if (properties.isStartSendingOnStartup()) {
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
        return propertiesManager.getProperties().isHideSkippedRecipients();
    }

    public void setHideSkippedRecipients(boolean value) {
        if (isOptionsMenuEnabled()) {
            propertiesManager.getProperties().setHideSkippedRecipients(value);
        }
    }

    public boolean getRefreshRecipientsAfterEveryTelegram() {
        return propertiesManager.getProperties().isUpdateRecipientsAfterEveryTelegram();
    }

    public void setRefreshRecipientsAfterEveryTelegram(boolean value) {
        if (isOptionsMenuEnabled()) {
            propertiesManager.getProperties().setUpdateRecipientsAfterEveryTelegram(value);
        }
    }

    public boolean getRunIndefinitely() {
        return propertiesManager.getProperties().isRunIndefinitely();
    }

    public void setRunIndefinitely(boolean value) {
        if (isOptionsMenuEnabled()) {
            propertiesManager.getProperties().setRunIndefinitely(value);
        }
    }

    public boolean getStartMinimized() {
        return propertiesManager.getProperties().isStartMinimized();
    }

    public void setStartMinimized(boolean value) {
        if (isOptionsMenuEnabled()) {
            propertiesManager.getProperties().setStartMinimized(value);
        }
    }

    public boolean getStartSendingOnStartup() {
        return propertiesManager.getProperties().isStartSendingOnStartup();
    }

    public void setStartSendingOnStartup(boolean value) {
        if (isOptionsMenuEnabled()) {
            propertiesManager.getProperties().setStartSendingOnStartup(value);
        }
    }

    public boolean isClientKeyInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String getClientKey() {
        return propertiesManager.getProperties().getClientKey();
    }

    public void setClientKey(String value) {
        if (isClientKeyInputEnabled()) {
            propertiesManager.getProperties().setClientKey(value);
        }
    }

    public boolean isTelegramIdInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String getTelegramId() {
        return propertiesManager.getProperties().getTelegramId();
    }

    public void setTelegramId(String value) {
        if (isTelegramIdInputEnabled()) {
            propertiesManager.getProperties().setTelegramId(value);
            outputText = outputTextCreator.createExpectedDurationMessage();
            listener.refreshOutput();
        }
    }

    public boolean isSecretKeyInputEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String getSecretKey() {
        return propertiesManager.getProperties().getSecretKey();
    }

    public void setSecretKey(String value) {
        if (isSecretKeyInputEnabled()) {
            propertiesManager.getProperties().setSecretKey(value);
        }
    }

    public boolean isAvailableTelegramTypesEnabled() {
        return state == TelegrammerState.Idle;
    }

    public String[] getAvailableTelegramTypes() {
        return Arrays.stream(TelegramType.values()).map(telegramType -> telegramType.toString()).toArray(String[]::new);
    }

    public int getSelectedTelegramTypeIndex() {
        var telegramType = propertiesManager.getProperties().getLastTelegramType();
        return Arrays.asList(TelegramType.values()).indexOf(telegramType);
    }

    public void setSelectedTelegramTypeIndex(int value) {
        if (isAvailableTelegramTypesEnabled() && value != getSelectedTelegramTypeIndex()) {
            var telegramType = TelegramType.values()[value];
            propertiesManager.getProperties().setLastTelegramType(telegramType);
            outputText = outputTextCreator.createExpectedDurationMessage();

            if (telegramType != TelegramType.RECRUITMENT && telegramType != TelegramType.CAMPAIGN) {
                propertiesManager.getProperties().setFromRegion("");
            }
            listener.refreshEverything();
        }
    }

    public boolean isForRegionInputEnabled() {
        var telegramType = propertiesManager.getProperties().getLastTelegramType();
        return state == TelegrammerState.Idle
                && (telegramType == TelegramType.RECRUITMENT || telegramType == TelegramType.CAMPAIGN);
    }

    public String getForRegion() {
        return propertiesManager.getProperties().getFromRegion();
    }

    public void setForRegion(String value) {
        if (isForRegionInputEnabled()) {
            propertiesManager.getProperties().setFromRegion(value);
        }
    }

    public boolean isConfiguredRecipientsFiltersEnabled() {
        return state == TelegrammerState.Idle;
    }

    public List<String> getConfiguredRecipientsFilters() {
        return propertiesManager.getProperties().getRecipientsListBuilder().getFilters().stream()
                .map(filter -> filter.toString()).collect(Collectors.toList());
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

        var properties = propertiesManager.getProperties();
        properties.setFromRegion(properties.getFromRegion().trim());
        properties.setClientKey(removeWhiteSpaces(properties.getClientKey()));
        properties.setTelegramId(removeWhiteSpaces(properties.getTelegramId()));
        properties.setSecretKey(removeWhiteSpaces(properties.getSecretKey()));

        changeStateAndInformListener(TelegrammerState.SendingTelegrams);
        try {
            telegramSender.startSending(properties.getRecipientsListBuilder());

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
                propertiesManager.getProperties().getRecipientsListBuilder().addFilter(filter);
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
            propertiesManager.getProperties().getRecipientsListBuilder()
                    .removeFilterAt(selectedConfiguredRecipientsFilterIndex);
            --selectedConfiguredRecipientsFilterIndex;
            outputText = outputTextCreator.createExpectedDurationMessage();
            listener.refreshEverything();
        }
    }

    public boolean isRefreshFiltersButtonEnabled() {
        return state == TelegrammerState.Idle
                && propertiesManager.getProperties().getRecipientsListBuilder().getFilters().size() > 0;
    }

    public void refreshFilters() {
        if (!isRefreshFiltersButtonEnabled()) {
            return;
        }
        outputText = "updating recipient list...\n";
        changeStateAndInformListener(TelegrammerState.CompilingRecipients);

        compileRecipientsExecutor.execute(() -> {
            var failedFilters = propertiesManager.getProperties().getRecipientsListBuilder().refreshFilters();

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
        propertiesManager.persistPropertiesToFileSystem();
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
        if (!propertiesManager.getProperties().isHideSkippedRecipients()) {
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
