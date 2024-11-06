package com.example.lets_snack.data.remote.callbacks;

import com.example.lets_snack.data.remote.dto.RecipeDto;

import java.util.List;

public interface RecipesCallback {
    void onSuccess(List<RecipeDto> recipes);
    void onMessage(String message);
    void onFailure(Throwable throwable);
}
