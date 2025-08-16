package com.example.weatherapp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast.json")
    suspend fun getWeatherForecast(
        @Query("key") key: String,         // Обязательный параметр (должен быть первым)
        @Query("q") q: String,             // Город или координаты
        @Query("days") days: Int = 1,      // Уменьшено до 1 дня (как в вашем ViewModel)
        @Query("aqi") aqi: String = "no",  // Добавлено согласно вашему ViewModel
        @Query("alerts") alerts: String = "no", // Добавлено согласно вашему ViewModel
        @Query("lang") lang: String = "ru" // Язык ответа
    ): WeatherResponse
}