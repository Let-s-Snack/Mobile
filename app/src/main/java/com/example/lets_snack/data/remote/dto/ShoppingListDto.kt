package com.example.lets_snack.data.remote.dto

import java.util.Date

data class ShoppingListDto(
    val recipesId: String,
    val recipeName: String,
    val ingredients: List<SaveIngredientDto>,
    val creationDate: Date
)
