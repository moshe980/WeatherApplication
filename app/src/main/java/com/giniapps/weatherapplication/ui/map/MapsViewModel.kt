package com.giniapps.weatherapplication.ui.map

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giniapps.weatherapplication.model.Weather
import com.giniapps.weatherapplication.networking.NetworkStatusChecker
import com.giniapps.weatherapplication.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext  context: Context

    ) : ViewModel() {
    private val _weatherUIState = MutableStateFlow<WeatherMapsUiState>(
        WeatherMapsUiState.Empty
    )
    val weatherUIState: StateFlow<WeatherMapsUiState> = _weatherUIState
    private val networkStatusChecker =
        NetworkStatusChecker(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)

    fun getCurrentWeatherByLocation(lat: Double, lng: Double) =
        viewModelScope.launch {
            _weatherUIState.value = WeatherMapsUiState.Loading
            if (networkStatusChecker.hasInternetConnection()) {
                try {
                    repository.getCurrentWeatherByLocation(lat, lng).collect {
                        _weatherUIState.value = WeatherMapsUiState.Success(it)
                    }

                } catch (e: IndexOutOfBoundsException) {
                    _weatherUIState.value = WeatherMapsUiState.Error("Unknown Location")

                } catch (e: Throwable) {
                    _weatherUIState.value = WeatherMapsUiState.Error(e.message ?: "Unknown Error")
                }
            } else {
                _weatherUIState.value =
                    WeatherMapsUiState.Error("This feature cannot work offline, please check your internet connection")

            }

        }


}

sealed class WeatherMapsUiState {
    data class Success(val currentWeather: Weather) : WeatherMapsUiState()
    data class Error(val message: String) : WeatherMapsUiState()
    object Loading : WeatherMapsUiState()
    object Empty : WeatherMapsUiState()
}