package com.example.lets_snack.data.remote.repository.rest

import android.content.Context
import android.util.Log
import com.example.lets_snack.constants.LetsSnackConstants
import com.example.lets_snack.data.remote.api.LetsSnackService
import com.example.lets_snack.data.remote.dto.TokenRequestDto
import com.example.lets_snack.data.remote.dto.TokenResponseDto
import com.example.lets_snack.data.remote.retrofit.LetsSnackRetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class TokenRepository(private val context: Context) {
    val retrofit = LetsSnackRetrofitBuilder.retrofit

    val interfaceService = retrofit.create(LetsSnackService::class.java)

    private val tokenStorage = TokenStorage(context)
    fun generateAndValidToken(): String? {
        val call = generateToken(TokenRequestDto(LetsSnackConstants.EMAIL_TOKEN.value, LetsSnackConstants.PASSWORD_TOKEN.value))
        call.enqueue(object : Callback<TokenResponseDto> {
            override fun onResponse(call: Call<TokenResponseDto>, response: Response<TokenResponseDto>) {
                response.body()?.let { tokenStorage.saveToken(it.message) }
            }

            override fun onFailure(call: Call<TokenResponseDto>, t: Throwable) {
                Log.e("TokenError", t.message.toString())
            }
        })
        return tokenStorage.getToken() // Retorne o token depois da chamada
    }

    fun generateToken(tokenRequestDto: TokenRequestDto) = interfaceService.generateToken(tokenRequestDto)

    // Verifique se o token é válido
    fun verifyTokenIsValid(): String? {
        return tokenStorage.verifyTokenIsValid(this)
    }

}
