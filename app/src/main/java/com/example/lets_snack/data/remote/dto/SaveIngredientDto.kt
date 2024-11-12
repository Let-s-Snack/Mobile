package com.example.lets_snack.data.remote.dto

data class SaveIngredientDto(
    val ingredientId: String,
    var isChecked: Boolean,
    val ingredientName: String,
    val meditionType: String,
    val quantity: Int
)