package com.example.lets_snack.data.remote.repository.rest

import android.content.Context
import com.example.lets_snack.data.remote.api.LetsSnackService
import com.example.lets_snack.data.remote.dto.PersonDto
import com.example.lets_snack.data.remote.dto.PersonDtoUpdate
import com.example.lets_snack.data.remote.retrofit.LetsSnackRetrofitBuilder

class PersonsRepository(context: Context) {
    val tokenRepository = TokenRepository(context)

    val retrofit = LetsSnackRetrofitBuilder.retrofit

    val interfaceService = retrofit.create(LetsSnackService::class.java)
    suspend fun insertPerson(personDto: PersonDto) = interfaceService.insertPerson(tokenRepository.verifyTokenIsValid(),personDto)

    fun listPersonByEmail(email: String) = interfaceService.listPersonByEmail(email)

    fun listPersonByUsername(username: String) = interfaceService.listPersonByUsername(username)

    suspend fun updatePerson(email: String, personDto: PersonDtoUpdate) = interfaceService.updatePerson(tokenRepository.verifyTokenIsValid(),email, personDto)

    fun findWishlistByUserEmail(personEmail: String) = interfaceService.findWishlistByUserEmail(personEmail)

    fun likeRecipe(recipesId: String, personsEmail: String) = interfaceService.likeRecipe(recipesId, personsEmail)

    fun saveRecipeIngredients(recipesId: String, personsEmail: String) = interfaceService.saveRecipeIngredients(recipesId, personsEmail)

    fun getWeekRecipeByEmail(personEmail: String) = interfaceService.getWeekRecipeByEmail(personEmail)
}