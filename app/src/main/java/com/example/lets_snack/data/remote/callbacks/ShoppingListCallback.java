package com.example.lets_snack.data.remote.callbacks;

import com.example.lets_snack.data.remote.dto.CheckedUserDto;
import com.example.lets_snack.data.remote.dto.ShoppingListDto;


public interface ShoppingListCallback {
    void onSuccess(ShoppingListDto recipes);
    void onMessage(String message);
    void onFailure(Throwable throwable);
}
