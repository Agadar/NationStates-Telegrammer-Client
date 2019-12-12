package com.github.agadar.telegrammer.client.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The keys of the telegrammer client specific settings.
 * 
 * @author Agadar (https://github.com/Agadar/)
 *
 */
@RequiredArgsConstructor
@Getter
public enum ClientSettingKey {

    START_SENDING_ON_STARTUP("startSendingOnStartup"),
    HIDE_SKIPPED_RECIPIENTS("hideSkippedRecipients"),
    START_MINIMIZED("startMinimized"),
    SOCKET_PROTOCOL("socket.protocol"),
    SOCKET_ALGORITHM("socket.algorithm"),
    SOCKET_KEYSTORE_TYPE("socket.keystore.type"),
    SOCKET_KEYSTORE_PASSPHRASE("socket.keystore.passphrase"),
    SOCKET_KEYSTORE_FILE("socket.keystore.file"),
    SOCKET_SERVER_ADDRESS("socket.server.address"),
    SOCKET_SERVER_PORT("socket.server.port");

    private final String key;
}
