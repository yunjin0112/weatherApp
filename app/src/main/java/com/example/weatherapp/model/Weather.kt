package com.example.weatherapp.model

data class Weather(
    val city: String,
    val dateTime: Long,
    val temp: Double,
    val pressure: Double,
    val humidity: Double,
    val feelsLike: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val windSpeed: Double,
    val sunrise: Long,
    val sunset: Long,
    val visibility: Double,
    val weathers: List<WeatherItem>
)
