package com.github.agadar.telegrammer.client;

import java.awt.EventQueue;

import javax.swing.UIManager;

import com.github.agadar.nationstates.DefaultNationStatesImpl;
import com.github.agadar.telegrammer.client.settings.TelegrammerClientSettings;
import com.github.agadar.telegrammer.client.view.TelegrammerView;
import com.github.agadar.telegrammer.client.viewmodel.OutputTextCreator;
import com.github.agadar.telegrammer.client.viewmodel.TelegrammerViewModel;
import com.github.agadar.telegrammer.core.history.TelegramHistoryImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsProviderTranslatorImpl;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccessImpl;
import com.github.agadar.telegrammer.core.sender.TelegramSenderImpl;
import com.github.agadar.telegrammer.core.settings.Settings;
import com.github.agadar.telegrammer.core.settings.TelegrammerCoreSettings;

import lombok.extern.slf4j.Slf4j;

/**
 * Main entry for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
@Slf4j
public class Main {

    public static void main(String args[]) {

        try {
            // Context root.
            var nationStates = new DefaultNationStatesImpl(
                    "Agadar's Telegrammer Client (https://github.com/Agadar/NationStates-Telegrammer-Client)");
            var telegramHistory = new TelegramHistoryImpl(".nationstates-telegrammer.history");
            var regionDumpAccess = new RegionDumpAccessImpl(nationStates);
            var providerTranslator = new RecipientsProviderTranslatorImpl(nationStates, regionDumpAccess);
            var filterTranslator = new RecipientsFilterTranslatorImpl(providerTranslator);
            var recipientsListBuilderTranslator = new RecipientsListBuilderTranslatorImpl(telegramHistory,
                    filterTranslator);
            var settings = new Settings(".nationstates-telegrammer.properties");
            var coreSettings = new TelegrammerCoreSettings(settings, recipientsListBuilderTranslator);
            var clientSettings = new TelegrammerClientSettings(settings);
            var telegramSender = new TelegramSenderImpl(nationStates, telegramHistory);
            var outputTextCreator = new OutputTextCreator(coreSettings);

            // Retrieve properties and history.
            settings.loadPropertiesFile();
            telegramHistory.loadHistory();

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            EventQueue.invokeLater(() -> {
                var viewModel = new TelegrammerViewModel(telegramSender, coreSettings, clientSettings, filterTranslator,
                        outputTextCreator);
                var view = new TelegrammerView(viewModel);
                view.setLocationRelativeTo(null);
                view.setVisible(true);
            });
        } catch (Exception ex) {
            log.error("Something went wrong during startup", ex);
        }
    }
}
