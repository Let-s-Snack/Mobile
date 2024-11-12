package com.example.lets_snack.data.remote.callbacks

import com.example.lets_snack.data.remote.dto.UserDto




interface UserCallback {
    fun onSuccess(user: UserDto?)
    fun onFailure(error: Throwable?)
}