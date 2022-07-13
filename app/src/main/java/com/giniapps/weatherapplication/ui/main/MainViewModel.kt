package com.giniapps.weatherapplication.ui.main

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giniapps.weatherapplication.model.Weather
import com.giniapps.weatherapplication.networking.NetworkStatusChecker
import com.giniapps.weatherapplication.repository.WeatherRepository
import com.giniapps.weatherapplication.ui.map.WeatherMapsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _weatherUIState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherUIState: StateFlow<WeatherUiState> = _weatherUIState
    private val networkStatusChecker =
        NetworkStatusChecker(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)


    fun getCurrentWeatherByLocation(
        lat: Double = 32.16030,
        lng: Double = 34.81042,
    ) =
        viewModelScope.launch {
            _weatherUIState.value = WeatherUiState.Loading
            if (networkStatusChecker.hasInternetConnection()) {
                try {
                    repository.getCurrentWeatherByLocation(lat, lng).collect {
                        _weatherUIState.value = WeatherUiState.Success(it)
                    }

                } catch (e: IndexOutOfBoundsException) {
                    _weatherUIState.value = WeatherUiState.Error("Unknown Location",null)

                } catch (e: Throwable) {
                    _weatherUIState.value = WeatherUiState.Error(e.message ?: "Unknown Error", null)
                }
            } else {
                repository.getCurrentWeatherByLocationFromCache().collect {
                    _weatherUIState.value = WeatherUiState.Error(
                        "Internet unavailable,showing cached data.",
                        it
                    )

                }


            }

        }

    fun getLocation() = repository.getLocations()

}



sealed class WeatherUiState {
    data class Success(val currentWeather: Weather) : WeatherUiState()
    data class Error(val message: String, val currentWeather: Weather?) : WeatherUiState()
    object Loading : WeatherUiState()
    object Empty : WeatherUiState()
}