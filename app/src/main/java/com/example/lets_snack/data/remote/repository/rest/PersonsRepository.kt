package com.example.lets_snack.data.remote.repository.rest

import android.content.Context
import com.example.lets_snack.data.remote.api.LetsSnackService
import com.example.lets_snack.data.remote.callbacks.MessageCallback
import com.example.lets_snack.data.remote.callbacks.UserCallback
import com.example.lets_snack.data.remote.dto.PersonDto
import com.example.lets_snack.data.remote.dto.PersonDtoUpdate
import com.example.lets_snack.data.remote.dto.UserDto
import com.example.lets_snack.data.remote.retrofit.LetsSnackRetrofitBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonsRepository(context: Context) {
    val tokenRepository = TokenRepository(context)

    val retrofit = LetsSnackRetrofitBuilder.retrofit

    val interfaceService = retrofit.create(LetsSnackService::class.java)
    suspend fun insertPerson(personDto: PersonDto) = interfaceService.insertPerson(tokenRepository.verifyTokenIsValid(),personDto)

    fun listPersonByEmail(email: String) = interfaceService.listPersonByEmail(email)

    fun listPersonByUsername(username: String) = interfaceService.listPersonByUsername(username)

    suspend fun updatePerson(email: String, personDto: PersonDtoUpdate) = interfaceService.updatePerson(tokenRepository.verifyTokenIsValid(),email, personDto)

    fun findWishlistByUserEmail(personEmail: String) = interfaceService.findWishlistByUserEmail(personEmail)

    fun likeRecipe(recipesId: String, personsEmail: String, callback: MessageCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = tokenRepository.verifyTokenIsValid()
                val response = interfaceService.likeRecipe(token, recipesId, personsEmail).execute()
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

    fun saveRecipeIngredients(recipesId: String, personsEmail: String, callback: MessageCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = tokenRepository.verifyTokenIsValid()
                val response = interfaceService.saveRecipeIngredients(token,recipesId, personsEmail).execute()
                if (response.isSuccessful){
                    val message = response.body()
                    withContext(Dispatchers.Main) {
                        if (message != null) {
                            callback.onSuccess(message)
                        } else {
                            callback.onFailure(Throwable("Response body is null"))
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onMessage(e.toString())
                }
            }
        }
    }

    fun getWeekRecipeByEmail(personEmail: String) = interfaceService.getWeekRecipeByEmail(personEmail)

    fun findAdmByEmail(email: String, callback: UserCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = tokenRepository.verifyTokenIsValid()
                val response = interfaceService.findAdmByEmail(token, email).execute() // Síncrono para callback
                if (response.isSuccessful) {
                    val user = response.body()
                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            callback.onSuccess(user)
                        } else {
                            callback.onFailure(Exception("Response body is null"))
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        callback.onFailure(Exception("Erro na resposta: ${response.errorBody()?.string()}"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(e)
                }
            }
        }
    }
}