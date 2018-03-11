package com.github.agadar.telegrammer.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.NationStates;
import com.github.agadar.telegrammer.client.form.NSTelegramForm;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientProperties;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientPropertiesManager;
import com.github.agadar.telegrammer.core.properties.manager.IPropertiesManager;
import com.github.agadar.telegrammer.core.recipients.translator.IRecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.IRecipientsListBuilderTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.IRecipientsProviderTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsListBuilderTranslator;
import com.github.agadar.telegrammer.core.recipients.translator.RecipientsProviderTranslator;
import com.github.agadar.telegrammer.core.regiondumpaccess.IRegionDumpAccess;
import com.github.agadar.telegrammer.core.regiondumpaccess.RegionDumpAccess;
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
	final TelegrammerClientProperties properties = new TelegrammerClientProperties();
	final ITelegramHistory telegramHistory = new TelegramHistory(properties, ".nationstates-telegrammer.history");
	final IRegionDumpAccess regionDumpAccess = new RegionDumpAccess(nationStates);
	final IRecipientsProviderTranslator providerTranslator = new RecipientsProviderTranslator(nationStates,
	        regionDumpAccess);
	final IRecipientsFilterTranslator filterTranslator = new RecipientsFilterTranslator(providerTranslator);
	final IRecipientsListBuilderTranslator recipientsListBuilderTranslator = new RecipientsListBuilderTranslator(
	        telegramHistory, filterTranslator);
	final IPropertiesManager propertiesManager = new TelegrammerClientPropertiesManager(
	        recipientsListBuilderTranslator, ".nationstates-telegrammer.properties");
	final TelegramSender telegramSender = new TelegramSender(nationStates, telegramHistory, properties);

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
