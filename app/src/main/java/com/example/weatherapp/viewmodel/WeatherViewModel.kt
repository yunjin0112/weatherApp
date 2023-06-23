package com.example.weatherapp.viewmodel

import android.location.Location
import android.text.Editable
import androidx.lifecycle.*
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherDTO
import com.example.weatherapp.model.asDomainModel
import com.example.weatherapp.network.WeatherApi
import kotlinx.coroutines.launch

enum class WeatherApiStatus { START, LOADING, ERROR, SUCCESS }

class WeatherViewModel(): ViewModel() {
    private val _currentLocation = MutableLiveData<Location>()

    private val query = MutableLiveData<String>()

    private val _weather = MutableLiveData<WeatherDTO>()
    val weather: LiveData<Weather> =
        _weather.map { it.asDomainModel() }

    private val _status = MutableLiveData<WeatherApiStatus>()
    val status: LiveData<WeatherApiStatus>
        get() = _status

    private val _resultForCurrentLocation = MutableLiveData<Boolean>()
    val resultForCurrentLocation: LiveData<Boolean>
        get() = _resultForCurrentLocation

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _toastWarningEvent = MutableLiveData<Boolean?>()
    val toastWarningEvent: LiveData<Boolean?>
        get() = _toastWarningEvent

    private val _typingCompleteEvent = MutableLiveData<Boolean?>()
    val typingCompleteEvent: LiveData<Boolean?>
        get() = _typingCompleteEvent

    var isSeachSuccessful = false
    var buttonCliked = false

    init {
        query.value = ""
        _status.value = WeatherApiStatus.START
    }

    // called whenever the location is updated.
    fun onLocationUpdated(location: Location) {
        _currentLocation.value = location
        getWeatherDataForCurrentLocation()
    }

    // update the current weather live data
    // result for current location to be true
    private fun getWeatherDataForCurrentLocation() = launchDataLoad {
        _currentLocation.value?.let { it ->
            try {
                _weather.value = WeatherApi.retrofitService.getCurrentWeather(it.latitude, it.longitude)
                _resultForCurrentLocation.value = true
            } catch (t: Throwable) {
                throw Throwable(t)
            }
        }
    }

    fun setCityQuery(e: Editable)  {
        query.value = e.trim().toString()
    }

    fun getQuery(e: Editable): String  {
        query.value = e.trim().toString()
        return query.value!!
    }

    // when the search button is clicked, retrieve the weather data
    // If editText is empty, shows warning toast
    fun onStartSearching(): Boolean {

        _typingCompleteEvent.value = true

        query.value?.let {
            if (it.isBlank())
                _toastWarningEvent.value = true
            else {
                getWeatherDataForCity(it)
                buttonCliked = true

            }
        }
        return true
    }

    // update the current weather live data
    // result for current location to be true
    fun getWeatherDataForCity(cityName: String) = launchDataLoad {
        _resultForCurrentLocation.value = false

        try {
            var cityNameWithCountry =""
            if (!cityName.endsWith("US")) {
                cityNameWithCountry = cityName + ",US";
                _weather.value = WeatherApi.retrofitService.getCurrentWeather(cityNameWithCountry)
            }
            else {
                _weather.value = WeatherApi.retrofitService.getCurrentWeather(cityName)
            }
        } catch (t: Throwable) {
            throw Throwable(t)
        }
    }

    // Data loading coroutine gets data
    // Update weatherApi status
    private fun launchDataLoad(block: suspend () -> Unit) {
        isSeachSuccessful = false
        viewModelScope.launch {
            try {
                _status.value = WeatherApiStatus.LOADING
                block()
                _status.value = WeatherApiStatus.SUCCESS
                isSeachSuccessful = true
            } catch (error: Throwable) {
                _errorMessage.value = error.message
                _status.value = WeatherApiStatus.ERROR
            }
        }
    }
}