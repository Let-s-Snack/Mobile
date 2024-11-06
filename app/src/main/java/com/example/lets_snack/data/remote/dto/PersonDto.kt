package com.example.lets_snack.data.remote.dto


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
    val restrictions: MutableList<RestrcitionID?>
)

data class PersonDtoUpdate(
    val gender: String,
    val name: String,
    val nickname: String,
    val email: String,
    val urlPhoto: String,
    val restrictions: MutableList<RestrcitionID?>
)

data class PersonDtoResponseEmail(
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
    val restrictions: MutableList<RestrictionsDto?>
)
data class PersonDtoResponse(
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
    val restrictions: MutableList<RestrcitionID?>,
    val message: String?
)

data class RestrcitionID(val restrictionId:String)