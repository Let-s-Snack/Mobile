package com.example.lets_snack.data.remote.dto

import java.util.Date

data class PersonDto(
    val gender: String,
    val name: String,
    val nickname: String,
    val email: String,
    val password: String,
    val isPro: Boolean,
    val urlPhoto: String,
    val birthDate: String,
    val cellphone: String,
    val registrationCompleted: Boolean,
    val restrictions: List<RestrictionsDto>
)