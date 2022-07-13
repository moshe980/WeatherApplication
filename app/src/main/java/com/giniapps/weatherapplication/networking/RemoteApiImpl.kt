package com.giniapps.weatherapplication.networking

import com.giniapps.weatherapplication.model.Failure
import com.giniapps.weatherapplication.model.ResponseWrapper
import com.giniapps.weatherapplication.model.Result
import com.giniapps.weatherapplication.model.Success
import javax.inject.Inject

class RemoteApiImpl @Inject constructor(private val weatherApi: WeatherApi) : RemoteApi {
    override suspend fun latestWeatherByLocation(
        lat: Double,
        lng: Double
    ): Result<ResponseWrapper> = try {
        Success(weatherApi.latestWeatherByLocation(lat, lng))
    } catch (e: Throwable) {
        Failure(e)
    }
}