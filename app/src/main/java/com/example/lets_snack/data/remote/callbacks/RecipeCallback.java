package com.example.lets_snack.data.remote.callbacks;

import com.example.lets_snack.data.remote.dto.RecipeDto;

public interface RecipeCallback {
    void onSuccess(RecipeDto recipes);
    void onMessage(String message);
    void onFailure(Throwable throwable);
}
