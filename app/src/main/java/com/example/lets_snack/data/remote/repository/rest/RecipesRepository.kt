package com.example.lets_snack.data.remote.repository.rest

import com.example.lets_snack.data.remote.api.LetsSnackService
import com.example.lets_snack.data.remote.dto.SendCommentDto
import com.example.lets_snack.data.remote.retrofit.LetsSnackRetrofitBuilder
import retrofit2.http.Query

class RecipesRepository {
    //aqui estamos criando uma instância do retrofit
    val retrofit = LetsSnackRetrofitBuilder.retrofit
    //agora estamos criando uma instância do nosso service
    val interfaceService = retrofit.create(LetsSnackService::class.java)


    //chama a função exampleFunService, que está no nosso service
    fun findRecipesByRestrictions(restrictionId: String, personsEmail: String) = interfaceService.findRecipesByRestrictions(restrictionId, personsEmail)

    fun findRecipeById(recipesId: String, personsEmail: String) = interfaceService.findRecipeById(recipesId, personsEmail)

    fun findRecipesByName(recipesName: String, personsEmail: String) = interfaceService.findRecipesByName(recipesName, personsEmail)

    fun insertComment(recipesId: String, comment: SendCommentDto) = interfaceService.insertComment(recipesId, comment)

    fun getRecommendedRecipesByEmail(personEmail: String) = interfaceService.getRecommendedRecipesByEmail(personEmail)

    fun getTrendingRecipesByEmail(personEmail: String) = interfaceService.getTrendingRecipesByEmail(personEmail)

    fun getMostCommentedRecipesByEmail(personEmail: String) = interfaceService.getMostCommentedRecipesByEmail(personEmail)
}