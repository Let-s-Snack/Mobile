package com.example.lets_snack.data.remote.repository.rest

import com.example.lets_snack.data.remote.api.LetsSnackService
import com.example.lets_snack.data.remote.retrofit.LetsSnackRetrofitBuilder

class ExampleIRepositoryRest {
    //aqui estamos criando uma instância do retrofit
    val retrofit = LetsSnackRetrofitBuilder.retrofit
    //agora estamos criando uma instância do nosso service
    val interfaceService = retrofit.create(LetsSnackService::class.java)


    //chama a função exampleFunService, que está no nosso service
    fun exampleFunRepository() = interfaceService.exampleFunService()

}