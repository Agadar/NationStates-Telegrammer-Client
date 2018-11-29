package com.github.agadar.telegrammer.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.telegrammer.client.form.NSTelegramForm;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientProperties;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientPropertiesManager;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsProviderTranslator;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccess;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistory;
import com.github.agadar.telegrammer.core.telegram.sender.TelegramSender;

/**
 * Main entry for this application.
 *
 * @author Agadar (https://github.com/Agadar/)n
 */
public class Main {

    public static void main(String args[]) {

        // Context root.
        var nationStates = new NationStates(
                "Agadar's Telegrammer Client (https://github.com/Agadar/NationStates-Telegrammer-Client)");
        var properties = new TelegrammerClientProperties();
        var telegramHistory = new TelegramHistory(properties, ".nationstates-telegrammer.history");
        var regionDumpAccess = new RegionDumpAccess(nationStates);
        var providerTranslator = new RecipientsProviderTranslator(nationStates, regionDumpAccess);
        var filterTranslator = new RecipientsFilterTranslator(providerTranslator);
        var recipientsListBuilderTranslator = new RecipientsListBuilderTranslator(telegramHistory, filterTranslator);
        var propertiesManager = new TelegrammerClientPropertiesManager(recipientsListBuilderTranslator,
                ".nationstates-telegrammer.properties");
        var telegramSender = new TelegramSender(nationStates, telegramHistory, properties);

        // Retrieve properties and history.
        propertiesManager.loadProperties(properties);
        telegramHistory.loadHistory();
        properties.recipientsListBuilder.refreshFilters();

        // Set-up graphical form.
        try {
            // Set cross-platform look&feel.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            java.awt.EventQueue.invokeLater(() -> {
                final NSTelegramForm form = new NSTelegramForm(telegramSender, propertiesManager, properties,
                        filterTranslator);
                telegramSender.addListeners(form); // subscribe form to TelegramManager.
                form.setLocationRelativeTo(null);
                form.setVisible(true);
            });
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(NSTelegramForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
