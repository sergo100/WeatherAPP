package com.example.space_ranger3209.data // ИСПРАВЛЕНО: Правильный пакет

import retrofit2.http.GET
import retrofit2.http.Query

// Интерфейс для взаимодействия с погодным API (weatherapi.com)
interface WeatherApi {

    // Запрос на получение прогноза погоды
    @GET("forecast.json")
    suspend fun getWeatherForecast(
        @Query("q") q: String, // Название города
        @Query("key") key: String, // Ваш API ключ
        @Query("days") days: Int = 3, // Количество дней для прогноза
        @Query("lang") lang: String = "ru" // Язык ответа
    ): WeatherResponse
}
