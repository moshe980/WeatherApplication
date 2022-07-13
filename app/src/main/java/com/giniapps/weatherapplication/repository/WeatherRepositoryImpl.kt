package com.giniapps.weatherapplication.repository

import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.giniapps.weatherapplication.service.SharedLocationManager
import com.giniapps.weatherapplication.database.logic.DataSource
import com.giniapps.weatherapplication.model.Failure
import com.giniapps.weatherapplication.model.Success
import com.giniapps.weatherapplication.model.Weather
import com.giniapps.weatherapplication.networking.RemoteApi
import com.giniapps.weatherapplication.utils.toCelsius
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteApi: RemoteApi,
    private val dataSource: DataSource,
    private val sharedLocationManager: SharedLocationManager,
    private val geocoder: Geocoder
) :
    WeatherRepository {
    override suspend fun getCurrentWeatherByLocation(lat: Double, lng: Double): Flow<Weather> {
        return when (val result = remoteApi.latestWeatherByLocation(lat, lng)) {
            is Success -> {
                val currentWeather= result.data.weather
                flow {
                    currentWeather.address = getAddress(lat, lng)
                    currentWeather.temperature = currentWeather.temperature.toCelsius()
                    dataSource.saveWeather(currentWeather)
                    emit(currentWeather)
                }
            }
            is Failure -> {
                //throw RuntimeException(result.exc)
                dataSource.findLastWeather()

            }

        }


    }

    override suspend fun getCurrentWeatherByLocationFromCache() = dataSource.findLastWeather()
    private fun getAddress(lat: Double, lng: Double): String {
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list[0].getAddressLine(0)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLocations() = sharedLocationManager.locationFlow()

    override suspend fun refreshCache(location: Location) {
        Log.d("moshe", "doWork: HEREHERE")

        when (val result =
            remoteApi.latestWeatherByLocation(location.latitude, location.longitude)) {
            is Success -> {
                result.data.weather.address = getAddress(location.latitude, location.longitude)
                result.data.weather.temperature = result.data.weather.temperature.toCelsius()

                dataSource.saveWeather(result.data.weather)

            }
            is Failure -> {
                throw RuntimeException(result.exc)
            }
        }

    }


}