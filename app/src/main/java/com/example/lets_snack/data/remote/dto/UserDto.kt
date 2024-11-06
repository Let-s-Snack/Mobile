package com.example.lets_snack.data.remote.dto

data class UserDto(
    val pkId: String,
    val email: String,
    val password: String,
    val name: String,
    val isDeleted: Boolean,
    val creationDate: String,
    val message: String
)
