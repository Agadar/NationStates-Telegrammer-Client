package com.github.agadar.telegrammer.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.NationStates;
import com.github.agadar.telegrammer.client.form.NSTelegramForm;
import com.github.agadar.telegrammer.core.nationdumpaccess.INationDumpAccess;
import com.github.agadar.telegrammer.core.nationdumpaccess.NationDumpAccess;
import com.github.agadar.telegrammer.core.properties.ApplicationProperties;
import com.github.agadar.telegrammer.core.properties.manager.BasicPropertiesManager;
import com.github.agadar.telegrammer.core.properties.manager.IPropertiesManager;
import com.github.agadar.telegrammer.core.recipients.translator.IRecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.IRecipientsListBuilderTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.IRecipientsProviderTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsProviderTranslator;
import com.github.agadar.telegrammer.core.telegram.history.ITelegramHistory;
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
	final INationStates nationStates = new NationStates(
		"Agadar's Telegrammer Client (https://github.com/Agadar/NationStates-Telegrammer-Client)");
	final ApplicationProperties applicationProperties = new ApplicationProperties();
	final ITelegramHistory telegramHistory = new TelegramHistory(applicationProperties,
		".nationstates-telegrammer.history");
	final INationDumpAccess nationDumpAccess = new NationDumpAccess(nationStates);
	final IRecipientsProviderTranslator providerTranslator = new RecipientsProviderTranslator(nationStates,
		nationDumpAccess);
	final IRecipientsFilterTranslator filterTranslator = new RecipientsFilterTranslator(providerTranslator);
	final IRecipientsListBuilderTranslator recipientsListBuilderTranslator = new RecipientsListBuilderTranslator(
		telegramHistory, filterTranslator);
	final IPropertiesManager propertiesManager = new BasicPropertiesManager(recipientsListBuilderTranslator,
		".nationstates-telegrammer.properties");
	final TelegramSender telegramSender = new TelegramSender(nationStates, telegramHistory, applicationProperties);

	// Retrieve properties and history.
	propertiesManager.loadProperties(applicationProperties);
	telegramHistory.loadHistory();
	applicationProperties.recipientsListBuilder.refreshFilters();

	// Set-up graphical form.
	try {
	    // Set cross-platform look&feel.
	    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

	    // Create and display the form.
	    java.awt.EventQueue.invokeLater(() -> {
		final NSTelegramForm form = new NSTelegramForm(telegramSender, propertiesManager, applicationProperties,
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
