package com.giniapps.weatherapplication.database.dal

import androidx.room.*
import com.giniapps.weatherapplication.model.Weather
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM WEATHERS")
    fun findLastWeather(): Flow<Weather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeather(weather: Weather)

    @Delete
    suspend fun deleteWeather(weather: Weather)
}