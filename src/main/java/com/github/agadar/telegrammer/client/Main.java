package com.github.agadar.telegrammer.client;

import com.github.agadar.telegrammer.client.form.NSTelegramForm;
import com.github.agadar.nationstates.NationStates;
import com.github.agadar.telegrammer.core.nationdumpaccess.NationDumpAccess;
import com.github.agadar.telegrammer.core.propertiesmanager.PropertiesManager;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsProviderTranslator;
import com.github.agadar.telegrammer.core.telegram.history.TelegramHistory;
import com.github.agadar.telegrammer.core.telegram.sender.TelegramSender;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main entry for this application.
 *
 * @author Agadar (https://github.com/Agadar/)n
 */
public class Main {

    public static void main(String args[]) {

        // Create context root.
        final PropertiesManager propertiesManager = new PropertiesManager();
        final NationStates nationStates = new NationStates("Agadar's Telegrammer Client (https://github.com/Agadar/NationStates-Telegrammer-Client)");
        final TelegramHistory telegramHistory = new TelegramHistory(propertiesManager);
        final NationDumpAccess nationDumpAccess = new NationDumpAccess(nationStates);
        final TelegramSender telegramSender = new TelegramSender(nationStates, telegramHistory, propertiesManager);
        final RecipientsListBuilder recipientsListBuilder = new RecipientsListBuilder(telegramHistory);
        final RecipientsProviderTranslator providerTranslator = new RecipientsProviderTranslator(nationStates, nationDumpAccess);
        final RecipientsFilterTranslator filterTranslator = new RecipientsFilterTranslator(providerTranslator);

        // Retrieve properties and history.
        propertiesManager.loadProperties();
        telegramHistory.loadHistory();

        // Set-up graphical form.      
        try {
            // Set cross-platform look&feel.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            java.awt.EventQueue.invokeLater(()
                    -> {
                final NSTelegramForm form = new NSTelegramForm(telegramSender, propertiesManager, recipientsListBuilder, filterTranslator);
                telegramSender.addListeners(form);   // subscribe form to TelegramManager.
                form.setLocationRelativeTo(null);
                form.setVisible(true);
            });
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(NSTelegramForm.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
}
