package com.example.lets_snack.data.remote.dto

data class UserDto(
val pkId: Int,
val email: String,
val password: String,
val name: String,
val isDeleted: Boolean,
val creationDate: String
)
