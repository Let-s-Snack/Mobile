package com.example.lets_snack.data.remote.repository.rest

import android.content.Context
import android.util.Log
import com.example.lets_snack.constants.LetsSnackConstants
import com.example.lets_snack.data.remote.dto.TokenRequestDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenStorage(private val context: Context) {
    // Remova a linha de inicialização do TokenRepository
    fun saveToken(token: String) {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val currentTimeMillis = System.currentTimeMillis()
        with(sharedPref.edit()) {
            putString("TOKEN_KEY", token)
            putLong("TOKEN_TIMESTAMP_KEY", currentTimeMillis)
            apply()
        }
    }

    fun getToken(): String? {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPref.getString("TOKEN_KEY", null)
    }

    fun isTokenExpired(): Boolean {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val tokenTimestamp = sharedPref.getLong("TOKEN_TIMESTAMP_KEY", -1L)
        if (tokenTimestamp == -1L) {
            return true
        }

        val currentTimeMillis = System.currentTimeMillis()
        val threeMinutesInMillis = 3 * 60 * 1000

        return (currentTimeMillis - tokenTimestamp) > threeMinutesInMillis
    }

    // Mova a lógica de geração de token para o TokenRepository
    fun verifyTokenIsValid(tokenRepository: TokenRepository): String? {
        val token = getToken()
         if (token != null && !isTokenExpired()) {
             return token
        }
        return tokenRepository.generateAndValidToken()

    }
}
