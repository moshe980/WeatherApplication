package com.giniapps.weatherapplication.model

import com.google.gson.annotations.SerializedName

data class ResponseWrapper(
    @SerializedName("data")
    val weather: Weather,
    val message: String
)