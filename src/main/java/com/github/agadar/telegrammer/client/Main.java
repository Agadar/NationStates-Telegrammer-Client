package com.github.agadar.telegrammer.client;

import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.HIDE_SKIPPED_RECIPIENTS;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_ALGORITHM;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_KEYSTORE_FILE;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_KEYSTORE_PASSPHRASE;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_KEYSTORE_TYPE;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_PROTOCOL;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_SERVER_ADDRESS;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_SERVER_PORT;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.START_MINIMIZED;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.START_SENDING_ON_STARTUP;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.github.agadar.javacommander.JavaCommander;
import com.github.agadar.telegrammer.client.telegrammer.SocketClient;
import com.github.agadar.telegrammer.client.telegrammer.SocketClientTelegrammerImpl;
import com.github.agadar.telegrammer.client.view.TelegrammerView;
import com.github.agadar.telegrammer.client.viewmodel.OutputTextCreator;
import com.github.agadar.telegrammer.client.viewmodel.TelegrammerViewModel;
import com.github.agadar.telegrammer.core.DefaultTelegrammerImpl;
import com.github.agadar.telegrammer.core.Telegrammer;
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
            var settings = new Settings(".nationstates-telegrammer.properties");
            addClientSettings(settings);
            Telegrammer telegrammer;

            if (args.length == 0 || !args[0].equals("remote")) {
                String userAgent = "Agadar's Telegrammer Client (https://github.com/Agadar/NationStates-Telegrammer-Client)";
                telegrammer = new DefaultTelegrammerImpl(userAgent, settings);
                settings.loadPropertiesFile();

            } else {
                var socketClient = new SocketClient();
                var javaCommander = new JavaCommander();
                telegrammer = new SocketClientTelegrammerImpl(socketClient, javaCommander);
                settings.loadPropertiesFile();

                if (!socketClient.initialise(settings)) {
                    return;
                }
            }

            var outputTextCreator = new OutputTextCreator();
            var viewModel = new TelegrammerViewModel(telegrammer, settings, outputTextCreator);
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Create and display the form.
            SwingUtilities.invokeLater(() -> {
                var view = new TelegrammerView(viewModel);
                view.setLocationRelativeTo(null);
                view.setVisible(true);
            });
        } catch (Exception ex) {
            log.error("Something went wrong during startup", ex);
        }
    }

    private static void addClientSettings(Settings settings) {
        settings.addBooleanSetting(START_SENDING_ON_STARTUP.getKey(), false);
        settings.addBooleanSetting(HIDE_SKIPPED_RECIPIENTS.getKey(), false);
        settings.addBooleanSetting(START_MINIMIZED.getKey(), false);
        settings.addStringSetting(SOCKET_PROTOCOL.getKey(), "TLS");
        settings.addStringSetting(SOCKET_ALGORITHM.getKey(), "SunX509");
        settings.addStringSetting(SOCKET_KEYSTORE_TYPE.getKey(), "JKS");
        settings.addStringSetting(SOCKET_KEYSTORE_PASSPHRASE.getKey(), "default");
        settings.addStringSetting(SOCKET_KEYSTORE_FILE.getKey(), "default-keystore.jks");
        settings.addStringSetting(SOCKET_SERVER_ADDRESS.getKey(), "localhost");
        settings.addIntegerSetting(SOCKET_SERVER_PORT.getKey(), 420);
    }
}
