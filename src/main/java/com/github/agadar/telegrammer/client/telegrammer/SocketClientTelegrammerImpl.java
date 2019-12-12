package com.github.agadar.telegrammer.client.telegrammer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.github.agadar.javacommander.JavaCommander;
import com.github.agadar.javacommander.exception.JavaCommanderException;
import com.github.agadar.telegrammer.core.Telegrammer;
import com.github.agadar.telegrammer.core.TelegrammerListener;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterAction;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.settings.CoreSettingKey;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketClientTelegrammerImpl implements Telegrammer, SocketClientListener {

    private final Collection<TelegrammerListener> listeners = new HashSet<>();
    private final SocketClient socketClient;
    private final JavaCommander javaCommander;

    public SocketClientTelegrammerImpl(@NonNull SocketClient socketClient, @NonNull JavaCommander javaCommander)
            throws JavaCommanderException {
        this.socketClient = socketClient;
        this.javaCommander = javaCommander;
        javaCommander.registerFromObject(this); // TODO: Add annotated listeners.
        socketClient.addListener(this);
    }

    @Override
    public void addListeners(@NonNull TelegrammerListener... listeners) {
        synchronized (this.listeners) {
            this.listeners.addAll(Arrays.asList(listeners));
        }
    }

    @Override
    public void removeListener(@NonNull TelegrammerListener... listeners) {
        synchronized (this.listeners) {
            this.listeners.removeAll(Arrays.asList(listeners));
        }
    }

    @Override
    public void updateSetting(@NonNull CoreSettingKey key, @NonNull Object value) {
        String data = String.format("updateSetting %s %s", key.getKey(), value.toString());
        socketClient.write(data);
    }

    @Override
    public void addFilter(@NonNull RecipientsFilterType filterType, @NonNull RecipientsFilterAction filterAction,
            @NonNull Collection<String> input) {
        String data = String.format("addFilter %s %s %s", filterType.name(), filterAction.name(), input.toString());
        socketClient.write(data);
    }

    @Override
    public void removeFilterAtIndex(int index) {
        String data = String.format("removeFilterAtIndex %s", Integer.toString(index));
        socketClient.write(data);
    }

    @Override
    public void refreshFilters() {
        socketClient.write("refreshFilters");
    }

    @Override
    public void startSending() {
        socketClient.write("startSending");
    }

    @Override
    public void stopSending() {
        socketClient.write("stopSending");
    }

    @Override
    public void onDataReceived(@NonNull String data) {
        try {
            javaCommander.execute(data);
        } catch (JavaCommanderException ex) {
            String errorMsg = String.format("Failed to execute command '%s'", data);
            log.error(errorMsg, ex);
        }
    }
}
