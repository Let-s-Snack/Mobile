package com.example.lets_snack.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class RestrictionsDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("urlPhoto") val urlPhoto: String)