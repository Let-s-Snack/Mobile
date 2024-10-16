package com.example.lets_snack.data.remote.dto

data class MessageDto(val username: String = "", val message:String= "", val timestamp: Long = System.currentTimeMillis() // Adiciona um timestamp
)
