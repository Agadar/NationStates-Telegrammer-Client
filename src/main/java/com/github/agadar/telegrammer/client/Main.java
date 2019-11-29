package com.github.agadar.telegrammer.client;

import java.awt.EventQueue;

import javax.swing.UIManager;

import com.github.agadar.telegrammer.client.settings.ClientSettings;
import com.github.agadar.telegrammer.client.view.TelegrammerView;
import com.github.agadar.telegrammer.client.viewmodel.OutputTextCreator;
import com.github.agadar.telegrammer.client.viewmodel.TelegrammerViewModel;
import com.github.agadar.telegrammer.core.DefaultTelegrammerImpl;
import com.github.agadar.telegrammer.core.settings.Settings;

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
            String userAgent = "Agadar's Telegrammer Client (https://github.com/Agadar/NationStates-Telegrammer-Client)";
            var settings = new Settings(".nationstates-telegrammer.properties");
            var telegrammer = new DefaultTelegrammerImpl(userAgent, settings);
            var clientSettings = new ClientSettings(settings);

            settings.loadPropertiesFile();
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            EventQueue.invokeLater(() -> {
                var outputTextCreator = new OutputTextCreator(telegrammer.getTelegrammerCoreSettings());
                var viewModel = new TelegrammerViewModel(telegrammer, clientSettings, outputTextCreator);
                var view = new TelegrammerView(viewModel);
                view.setLocationRelativeTo(null);
                view.setVisible(true);
            });
        } catch (Exception ex) {
            log.error("Something went wrong during startup", ex);
        }
    }
}
