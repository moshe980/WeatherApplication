package com.giniapps.weatherapplication.repository

import android.location.Location
import com.giniapps.weatherapplication.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeatherByLocation(lat: Double, lng: Double): Flow<Weather>
    suspend fun getCurrentWeatherByLocationFromCache(): Flow<Weather>
    fun getLocations(): Flow<Location>
    suspend fun  refreshCache(location: Location)

}