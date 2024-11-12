package com.example.lets_snack.data.remote.dto

data class CheckedUserDto(
    val recipesId: String,
    val ingredients: List<SaveIngredientDto>,
    val creationDate: String
)
