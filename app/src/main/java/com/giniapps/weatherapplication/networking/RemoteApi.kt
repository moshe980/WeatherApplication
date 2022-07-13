package com.giniapps.weatherapplication.networking

import com.giniapps.weatherapplication.BuildConfig
import com.giniapps.weatherapplication.model.ResponseWrapper
import com.giniapps.weatherapplication.model.Result

interface RemoteApi {

    //Weather:
    suspend fun latestWeatherByLocation(
        lat: Double ,
        lng: Double ,
    ): Result<ResponseWrapper>


}