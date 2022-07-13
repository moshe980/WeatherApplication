package com.giniapps.weatherapplication.database.logic

import com.giniapps.weatherapplication.database.dal.WeatherDao
import com.giniapps.weatherapplication.model.Weather
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataSourceImpl @Inject constructor(private val weatherDao: WeatherDao) : DataSource {
    override fun findLastWeather(): Flow<Weather> = weatherDao.findLastWeather()

    override suspend fun saveWeather(weather: Weather) =weatherDao.saveWeather(weather)

    override suspend fun deleteWeather(weather: Weather) =weatherDao.deleteWeather(weather)
}