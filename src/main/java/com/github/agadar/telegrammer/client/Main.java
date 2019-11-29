package com.github.agadar.telegrammer.client;

import java.awt.EventQueue;

import javax.swing.UIManager;

import com.github.agadar.telegrammer.client.settings.TelegrammerClientSettings;
import com.github.agadar.telegrammer.client.view.TelegrammerView;
import com.github.agadar.telegrammer.client.viewmodel.OutputTextCreator;
import com.github.agadar.telegrammer.client.viewmodel.TelegrammerViewModel;
import com.github.agadar.telegrammer.core.DefaultTelegrammerImpl;

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
            var telegrammer = new DefaultTelegrammerImpl();
            var clientSettings = new TelegrammerClientSettings(telegrammer.getSettings());
            telegrammer.initialise(
                    "Agadar's Telegrammer Client (https://github.com/Agadar/NationStates-Telegrammer-Client)");
            var outputTextCreator = new OutputTextCreator(telegrammer.getTelegrammerCoreSettings());

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            EventQueue.invokeLater(() -> {
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
