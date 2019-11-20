package com.github.agadar.telegrammer.client;

import java.awt.EventQueue;

import javax.swing.UIManager;

import com.github.agadar.nationstates.DefaultNationStatesImpl;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientPropertiesManager;
import com.github.agadar.telegrammer.client.view.TelegrammerView;
import com.github.agadar.telegrammer.client.viewmodel.OutputTextCreator;
import com.github.agadar.telegrammer.client.viewmodel.TelegrammerViewModel;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsProviderTranslatorImpl;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccessImpl;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistoryImpl;
import com.github.agadar.telegrammer.core.telegram.sender.TelegramSenderImpl;

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
            var propertiesManager = new TelegrammerClientPropertiesManager(recipientsListBuilderTranslator,
                    ".nationstates-telegrammer.properties");
            var telegramSender = new TelegramSenderImpl(nationStates, telegramHistory, propertiesManager);
            var outputTextCreator = new OutputTextCreator(propertiesManager);

            // Retrieve properties and history.
            propertiesManager.loadPropertiesFromFileSystem();
            telegramHistory.loadHistory();

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            EventQueue.invokeLater(() -> {
                var viewModel = new TelegrammerViewModel(telegramSender, propertiesManager, filterTranslator,
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
