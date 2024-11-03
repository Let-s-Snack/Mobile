package com.example.lets_snack.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RestrictionsDto(
    @SerializedName("id") val id: RestrictionIdDTO,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("urlPhoto") val urlPhoto: String,
    @SerializedName("creationDate") val creationDate: String
)

data class RestrictionIdDTO(
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("date") val date: String
)
