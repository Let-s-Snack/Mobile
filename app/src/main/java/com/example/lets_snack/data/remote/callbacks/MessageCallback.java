package com.example.lets_snack.data.remote.callbacks;

import com.example.lets_snack.data.remote.dto.MessageDto;

public interface MessageCallback {
    void onSuccess(MessageDto messageDto);
    void onMessage(String message);
    void onFailure(Throwable throwable);
}
