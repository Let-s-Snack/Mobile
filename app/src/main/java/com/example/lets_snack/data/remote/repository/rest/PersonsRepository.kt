package com.example.lets_snack.data.remote.repository.rest

import android.content.Context
import com.example.lets_snack.data.remote.api.LetsSnackService
import com.example.lets_snack.data.remote.dto.PersonDto
import com.example.lets_snack.data.remote.dto.PersonDtoUpdate
import com.example.lets_snack.data.remote.retrofit.LetsSnackRetrofitBuilder

class PersonsRepository(context: Context) {
    val tokenRepository = TokenRepository(context)

    val retrofit = LetsSnackRetrofitBuilder.retrofit

    val interfaceService = retrofit.create(LetsSnackService::class.java)
    fun insertPerson(personDto: PersonDto) = interfaceService.insertPerson(tokenRepository.verifyTokenIsValid(),personDto)

    fun listPersonByEmail(email: String) = interfaceService.listPersonByEmail(email)

    fun listPersonByUsername(username: String) = interfaceService.listPersonByUsername(username)

    fun updatePerson(email: String, personDto: PersonDtoUpdate) = interfaceService.updatePerson(tokenRepository.verifyTokenIsValid(),email, personDto)
}