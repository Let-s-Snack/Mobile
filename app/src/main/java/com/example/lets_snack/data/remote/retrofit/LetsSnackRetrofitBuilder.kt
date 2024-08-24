package com.example.lets_snack.data.remote.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LetsSnackRetrofitBuilder {
    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        inline fun <reified T> createService(): T {
            return retrofit.create(T::class.java)
        }
    }
}