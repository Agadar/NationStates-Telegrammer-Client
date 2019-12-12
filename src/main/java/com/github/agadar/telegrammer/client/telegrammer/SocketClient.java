package com.github.agadar.telegrammer.client.telegrammer;

import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_ALGORITHM;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_KEYSTORE_FILE;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_KEYSTORE_PASSPHRASE;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_KEYSTORE_TYPE;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_PROTOCOL;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_SERVER_ADDRESS;
import static com.github.agadar.telegrammer.client.settings.ClientSettingKey.SOCKET_SERVER_PORT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.github.agadar.telegrammer.core.settings.Settings;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketClient {

    private final Collection<SocketClientListener> listeners = new HashSet<>();
    private final Executor socketReaderExecutor = Executors.newSingleThreadExecutor();

    private Socket socket;
    private Writer socketWriter;
    private BufferedReader socketReader;

    public void addListener(@NonNull SocketClientListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public boolean initialise(@NonNull Settings settings) {

        String protocol = settings.getValue(SOCKET_PROTOCOL.getKey(), String.class);
        String algorithm = settings.getValue(SOCKET_ALGORITHM.getKey(), String.class);
        String keyStoreType = settings.getValue(SOCKET_KEYSTORE_TYPE.getKey(), String.class);
        String passPhrase = settings.getValue(SOCKET_KEYSTORE_PASSPHRASE.getKey(), String.class);
        String keyStoreFile = settings.getValue(SOCKET_KEYSTORE_FILE.getKey(), String.class);
        String serverAddress = settings.getValue(SOCKET_SERVER_ADDRESS.getKey(), String.class);
        int serverPort = settings.getValue(SOCKET_SERVER_PORT.getKey(), Integer.class);

        try {
            var sslContext = SSLContext.getInstance(protocol);
            var trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
            var keyStore = KeyStore.getInstance(keyStoreType);
            char[] passPhraseChars = passPhrase.toCharArray();

            keyStore.load(new FileInputStream(keyStoreFile), passPhraseChars);
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            var socketFactory = sslContext.getSocketFactory();
            socket = socketFactory.createSocket(serverAddress, serverPort);
            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);
            socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            socketReaderExecutor.execute(this::readSocketIndefinitely);
            log.info("Initialised socket client");
            return true;

        } catch (Exception ex) {
            log.error("Failed to initialise socket client", ex);
            return false;
        }
    }

    public void write(@NonNull String data) {
        log.info("Sending data through the socket: '{}'", data);
        synchronized (socketWriter) {
            try {
                socketWriter.write(data);
                socketWriter.flush();
            } catch (IOException ex) {
                log.error("An error occured while sending data through the socket", ex);
            }
        }
    }

    private void readSocketIndefinitely() {
        try {
            String data = null;
            while ((data = socketReader.readLine()) != null) {
                log.info("Received data through the socket: '{}'", data);
                synchronized (listeners) {
                    final String finalData = data;
                    listeners.forEach(listener -> listener.onDataReceived(finalData));
                }
            }
        } catch (IOException ex) {
            log.error("An error occured while receiving data through the socket", ex);
            socketReaderExecutor.execute(this::readSocketIndefinitely);
        }
    }
}
