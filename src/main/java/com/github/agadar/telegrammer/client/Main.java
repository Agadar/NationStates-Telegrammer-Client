package com.github.agadar.telegrammer.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.agadar.nationstates.DefaultNationStatesImpl;
import com.github.agadar.telegrammer.client.form.NSTelegramForm;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientProperties;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientPropertiesManager;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslatorImpl;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsProviderTranslatorImpl;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccessImpl;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistoryImpl;
import com.github.agadar.telegrammer.core.telegram.sender.TelegramSenderImpl;

/**
 * Main entry for this application.
 *
 * @author Agadar (https://github.com/Agadar/)n
 */
public class Main {

    public static void main(String args[]) {

        // Context root.
        var nationStates = new DefaultNationStatesImpl(
                "Agadar's Telegrammer Client (https://github.com/Agadar/NationStates-Telegrammer-Client)");
        var properties = new TelegrammerClientProperties();
        var telegramHistory = new TelegramHistoryImpl(properties, ".nationstates-telegrammer.history");
        var regionDumpAccess = new RegionDumpAccessImpl(nationStates);
        var providerTranslator = new RecipientsProviderTranslatorImpl(nationStates, regionDumpAccess);
        var filterTranslator = new RecipientsFilterTranslatorImpl(providerTranslator);
        var recipientsListBuilderTranslator = new RecipientsListBuilderTranslatorImpl(telegramHistory,
                filterTranslator);
        var propertiesManager = new TelegrammerClientPropertiesManager(recipientsListBuilderTranslator,
                ".nationstates-telegrammer.properties");
        var telegramSender = new TelegramSenderImpl(nationStates, telegramHistory, properties);

        // Retrieve properties and history.
        propertiesManager.loadProperties(properties);
        telegramHistory.loadHistory();
        properties.getRecipientsListBuilder().refreshFilters();

        // Set-up graphical form.
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            java.awt.EventQueue.invokeLater(() -> {
                var form = new NSTelegramForm(telegramSender, propertiesManager, properties, filterTranslator);
                telegramSender.addListeners(form);
                form.setLocationRelativeTo(null);
                form.setVisible(true);
            });
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(NSTelegramForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
