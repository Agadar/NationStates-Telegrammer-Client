package com.github.agadar.telegrammer.client.telegrammer;

import lombok.NonNull;

public interface SocketClientListener {

    public void onDataReceived(@NonNull String data);
}
