package com.github.agadar.telegrammer.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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

/**
 * Main entry for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class Main {

    public static void main(String args[]) {

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

        // Set-up graphical form.
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            java.awt.EventQueue.invokeLater(() -> {
                var viewModel = new TelegrammerViewModel(telegramSender, propertiesManager, filterTranslator,
                        outputTextCreator);
                var view = new TelegrammerView(viewModel);
                view.setLocationRelativeTo(null);
                view.setVisible(true);
            });
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
