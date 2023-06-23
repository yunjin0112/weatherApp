package com.example.weatherapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// DTO(Data Transfer Objects) are needed to parse Json response from the network

data class WeatherDTO(
    @Json(name = "name") val city: String,
    @Json(name = "dt") val dateTime: Long,
    val visibility: Double,
    val main: MainDTO,
    val wind: WindDTO,
    val sys: SysDTO,
    @Json(name = "weather") val weathers: List<WeatherItemDTO>
)

@JsonClass(generateAdapter = true)
data class MainDTO(
    val temp: Double,
    val pressure: Double,
    val humidity: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val minTemp: Double,
    @Json(name = "temp_max") val maxTemp: Double
)

@JsonClass(generateAdapter = true)
data class WindDTO(val speed: Double)

data class SysDTO(
    val sunrise: Long,
    val sunset: Long
)

@JsonClass(generateAdapter = true)
data class WeatherItemDTO(
    val description: String,
    @Json(name = "icon") val icon: String
)

// Convert DTO to Weather object (Kotlin object)
fun WeatherDTO.asDomainModel(): Weather {
    return Weather(
        city = city,
        dateTime = dateTime,
        temp = main.temp,
        pressure = main.pressure,
        humidity = main.humidity,
        feelsLike = main.feelsLike,
        minTemp = main.minTemp,
        maxTemp = main.maxTemp,
        windSpeed = wind.speed,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        visibility = visibility,
        weathers = weathers.map {
            WeatherItem(
                description = it.description,
                iconId = it.icon
            )
        }
    )
}