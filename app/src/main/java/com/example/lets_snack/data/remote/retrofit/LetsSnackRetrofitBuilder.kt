package com.example.lets_snack.data.remote.retrofit

import com.example.lets_snack.constants.LetsSnackConstants
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LetsSnackRetrofitBuilder {
    //o companion object equivale a um método static em java
    //isso facilita o nosso acesso, seria a mesma coisa de ter que iniciailizar uma outra classe para ele
    //dessa maneira não é necessário você inicializar a classe LetsSnackRetrofitBuilder para acessar o retrofit
    companion object {
        val gson = GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        // o by lazy faz com que a instância seja criada uma vez só.
        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(LetsSnackConstants.BASE_URL.value)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }
}