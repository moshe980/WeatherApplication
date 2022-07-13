package com.giniapps.weatherapplication.database.logic

import com.giniapps.weatherapplication.model.Weather
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun findLastWeather(): Flow<Weather>
    suspend fun saveWeather(weather: Weather)
    suspend fun deleteWeather(weather: Weather)
}