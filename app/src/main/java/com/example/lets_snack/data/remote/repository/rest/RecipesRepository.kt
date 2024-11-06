package com.example.lets_snack.data.remote.repository.rest

import android.content.Context
import com.example.lets_snack.data.remote.api.LetsSnackService
import com.example.lets_snack.data.remote.callbacks.MessageCallback
import com.example.lets_snack.data.remote.dto.SendCommentDto
import com.example.lets_snack.data.remote.retrofit.LetsSnackRetrofitBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.Query

class RecipesRepository(context: Context) {
    //aqui estamos criando uma instância do retrofit
    val tokenRepository = TokenRepository(context)

    val retrofit = LetsSnackRetrofitBuilder.retrofit
    //agora estamos criando uma instância do nosso service
    val interfaceService = retrofit.create(LetsSnackService::class.java)


    //chama a função exampleFunService, que está no nosso service
    fun findRecipesByRestrictions(restrictionId: String, personsEmail: String) = interfaceService.findRecipesByRestrictions(restrictionId, personsEmail)

    fun findRecipeById(recipesId: String, personsEmail: String) = interfaceService.findRecipeById(recipesId, personsEmail)

    fun findRecipesByName(recipesName: String, personsEmail: String) = interfaceService.findRecipesByName(recipesName, personsEmail)

    fun insertComment(recipesId: String, comment: SendCommentDto, callback: MessageCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = tokenRepository.verifyTokenIsValid()
                val response = interfaceService.insertComment(token, recipesId, comment).execute()
                if (response.isSuccessful) {
                    val message = response.body()
                    withContext(Dispatchers.Main) {
                        if (message != null) {
                            callback.onSuccess(message)
                        } else {
                            callback.onFailure(Throwable("Response body is null"))
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        callback.onFailure(Throwable("Erro na resposta: ${response.errorBody()?.string()}"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onMessage(e.toString())
                }
            }
        }
    }

    fun getRecommendedRecipesByEmail(personEmail: String) = interfaceService.getRecommendedRecipesByEmail(personEmail)

    fun getTrendingRecipesByEmail(personEmail: String) = interfaceService.getTrendingRecipesByEmail(personEmail)

    fun getMostCommentedRecipesByEmail(personEmail: String) = interfaceService.getMostCommentedRecipesByEmail(personEmail)
}