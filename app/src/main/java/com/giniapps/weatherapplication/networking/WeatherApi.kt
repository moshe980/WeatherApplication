package com.giniapps.weatherapplication.networking

import com.giniapps.weatherapplication.BuildConfig
import com.giniapps.weatherapplication.model.ResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {


    @GET("weather/latest/by-lat-lng")
    suspend fun latestWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): ResponseWrapper

}