package com.example.lets_snack.data.remote.repository.rest

import com.example.lets_snack.data.remote.api.LetsSnackService
import com.example.lets_snack.data.remote.dto.SendCommentDto
import com.example.lets_snack.data.remote.retrofit.LetsSnackRetrofitBuilder
import retrofit2.http.Query

class PersonsRepository {
    //aqui estamos criando uma instância do retrofit
    val retrofit = LetsSnackRetrofitBuilder.retrofit
    //agora estamos criando uma instância do nosso service
    val interfaceService = retrofit.create(LetsSnackService::class.java)


    //chama a função exampleFunService, que está no nosso service
    fun findWishlistByUserEmail(personEmail: String) = interfaceService.findWishlistByUserEmail(personEmail)

    fun likeRecipe(recipesId: String, personsEmail: String) = interfaceService.likeRecipe(recipesId, personsEmail)

    fun saveRecipeIngredients(recipesId: String, personsEmail: String) = interfaceService.saveRecipeIngredients(recipesId, personsEmail)

    fun getWeekRecipeByEmail(personEmail: String) = interfaceService.getWeekRecipeByEmail(personEmail)
}