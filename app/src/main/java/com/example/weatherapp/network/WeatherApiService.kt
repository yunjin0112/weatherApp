package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
private const val APP_ID = "appid=e35b6a7b7215da1489f3c2ccf33b57f2"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WeatherApiService {
    @GET("weather?$APP_ID")
    suspend fun getCurrentWeather(@Query("q") city: String): WeatherDTO

    @GET("weather?$APP_ID")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double, @Query("lon") lon: Double
    ): WeatherDTO
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(
            WeatherApiService::class.java
        )
    }
}
