package com.github.agadar.telegrammer.client.viewmodel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientProperties;
import com.github.agadar.telegrammer.core.properties.manager.PropertiesManager;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
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

/**
 * The abstract representation of the GUI, exposing all information needed to
 * construct an arbitrary framework-specific view that implements the
 * ITelegrammerViewModelListener.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
public class TelegrammerViewModel implements TelegramManagerListener {

    @Getter private final String title = "Agadar's NationStates Telegrammer Client 2.0.0";

    private final RecipientsFilterTranslator filterTranslator;
    private final TelegrammerClientProperties properties;
    private final PropertiesManager<TelegrammerClientProperties> propertiesManager;
    private final TelegramSender telegramSender;
    private final OutputTextCreator outputTextCreator;

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
            RecipientsFilterTranslator filterTranslator, OutputTextCreator outputTextCreator) {

        this.telegramSender = telegramSender;
        this.propertiesManager = propertiesManager;
        this.filterTranslator = filterTranslator;
        this.properties = properties;
        this.outputTextCreator = outputTextCreator;

        outputText = outputTextCreator.createExpectedDurationMessage();
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
            outputText = outputTextCreator.createExpectedDurationMessage();
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
            outputText = outputTextCreator.createExpectedDurationMessage();

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
        outputText = outputTextCreator.createExpectedDurationMessage();
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
                outputText = outputTextCreator.createExpectedDurationMessage();
            } catch (Exception | OutOfMemoryError ex) {
                outputText = outputTextCreator.createFailedFilterRefreshMessage(filter, ex);
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
            outputText = outputTextCreator.createExpectedDurationMessage();
            listener.refreshEverything();
        }
    }

    public void performPreCloseActions() {
        propertiesManager.saveProperties(properties);
    }

    @Override
    public void handleNoRecipientsFound(NoRecipientsFoundEvent event) {
        outputText += outputTextCreator.createTimestampedMessage(
                "no new recipients found, timing out for " + event.getTimeOut() / 1000 + " seconds...");
        listener.refreshOutput();
    }

    @Override
    public void handleRecipientRemoved(RecipientRemovedEvent event) {
        if (!properties.isHideSkippedRecipients()) {
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
}
