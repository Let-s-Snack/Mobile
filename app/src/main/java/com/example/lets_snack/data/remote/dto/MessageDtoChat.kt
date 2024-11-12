package com.example.lets_snack.data.remote.dto

data class MessageDtoChat(val username: String = "", val message:String= "", val timestamp: Long = System.currentTimeMillis())
