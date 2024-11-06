package com.example.lets_snack.data.remote.repository.rest

import android.content.Context
import com.example.lets_snack.constants.LetsSnackConstants
import com.example.lets_snack.data.remote.api.LetsSnackService
import com.example.lets_snack.data.remote.dto.TokenRequestDto
import com.example.lets_snack.data.remote.retrofit.LetsSnackRetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenRepository(private val context: Context) {
    val retrofit = LetsSnackRetrofitBuilder.retrofit
    val interfaceService = retrofit.create(LetsSnackService::class.java)
    private val tokenStorage = TokenStorage(context)

    suspend fun generateAndValidToken(): String? {
        val token = withContext(Dispatchers.IO) {
            val response = generateToken(TokenRequestDto(LetsSnackConstants.EMAIL_TOKEN.value, LetsSnackConstants.PASSWORD_TOKEN.value)).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenStorage.saveToken(it.message)
                    return@withContext it.message
                }
            }
            return@withContext null
        }
        return token
    }

    fun generateToken(tokenRequestDto: TokenRequestDto) = interfaceService.generateToken(tokenRequestDto)

    suspend fun verifyTokenIsValid(): String? {
        return tokenStorage.verifyTokenIsValid(this)
    }
}
