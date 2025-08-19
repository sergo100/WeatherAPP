package com.space_ranger3209.weatherapp.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitClient {
    private const val BASE_URL = "https://api.weatherapi.com/v1/" // HTTPS для WeatherAPI.com

    val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Логируем тело запросов и ответов
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    // Интерфейс для API WeatherAPI.com
    interface WeatherApiService {
        @GET("current.json")
        suspend fun getCurrentWeather(
            @Query("key") apiKey: String,
            @Query("q") query: String
        ): Response<WeatherResponse>

        @GET("forecast.json")
        suspend fun getForecast(
            @Query("key") apiKey: String,
            @Query("q") query: String,
            @Query("days") days: Int = 7
        ): Response<ForecastResponse>
    }

    // Использование: val apiService = RetrofitClient.retrofit.create(WeatherApiService::class.java)
}

// Модели данных для ответа API (основаны на документации WeatherAPI.com)
// Замените на ваши реальные модели, если они уже есть
data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class ForecastResponse(
    val location: Location,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String
)

data class Current(
    val last_updated: String,
    val last_updated_epoch: Long,
    val temp_c: Double,
    val temp_f: Double,
    val is_day: Int,
    val condition: Condition,
    val wind_kph: Double,
    val wind_degree: Int,
    val wind_dir: String,
    val pressure_mb: Double,
    val pressure_in: Double,
    val precip_mm: Double,
    val precip_in: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val vis_km: Double,
    val vis_miles: Double,
    val uv: Double,
    val gust_kph: Double
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val date_epoch: Long,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)

data class Day(
    val maxtemp_c: Double,
    val maxtemp_f: Double,
    val mintemp_c: Double,
    val mintemp_f: Double,
    val avgtemp_c: Double,
    val avgtemp_f: Double,
    val maxwind_kph: Double,
    val totalprecip_mm: Double,
    val totalprecip_in: Double,
    val avgvis_km: Double,
    val avgvis_miles: Double,
    val avghumidity: Int,
    val condition: Condition,
    val uv: Double
)

data class Astro(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moon_phase: String,
    val moon_illumination: Int
)

data class Hour(
    val time_epoch: Long,
    val time: String,
    val temp_c: Double,
    val temp_f: Double,
    val is_day: Int,
    val condition: Condition,
    val wind_kph: Double,
    val wind_degree: Int,
    val wind_dir: String,
    val pressure_mb: Double,
    val pressure_in: Double,
    val precip_mm: Double,
    val precip_in: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val chance_of_rain: Int,
    val chance_of_snow: Int,
    val vis_km: Double,
    val vis_miles: Double,
    val uv: Double,
    val gust_kph: Double
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)