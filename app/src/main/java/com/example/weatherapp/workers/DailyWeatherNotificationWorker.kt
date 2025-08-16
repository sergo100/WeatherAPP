package com.example.weatherapp.workers // Новый пакет для Worker'ов

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.data.CityDataStore
import com.example.weatherapp.data.WeatherApi
import com.example.weatherapp.utils.showWeatherNotification
import com.example.weatherapp.BuildConfig // Для доступа к API_KEY
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.util.Log // Для логирования

// Worker для ежедневного получения погоды и отправки уведомления
class DailyWeatherNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    // Используем API_KEY из BuildConfig
    private val API_KEY = BuildConfig.WEATHER_API_KEY // Убедитесь, что это правильное имя поля

    private val BASE_URL = "https://api.weatherapi.com/v1/"

    // Инициализация WeatherApi для запросов
    private val weatherApi: WeatherApi by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    // DataStore для получения сохраненного города
    private val cityDataStore = CityDataStore(appContext)

    // Основная логика Worker'а
    override suspend fun doWork(): Result {
        Log.d(TAG, "DailyWeatherNotificationWorker started.")
        val savedCity = cityDataStore.getCityName()

        if (savedCity.isNullOrBlank()) {
            Log.d(TAG, "No city saved, skipping notification.")
            showWeatherNotification(
                applicationContext,
                "Настройка погоды",
                "Пожалуйста, введите город в приложении, чтобы получать ежедневные уведомления о погоде."
            )
            return Result.success()
        }

        try {
            // ИСПРАВЛЕНИЕ: Изменены имена параметров на 'key' и 'q' в соответствии с WeatherApi.kt
            val weatherResponse = weatherApi.getWeatherForecast(
                key = API_KEY, // Передаем API-ключ в параметр 'key'
                q = savedCity, // Передаем название города в параметр 'q'
                days = 1,      // Количество дней для прогноза
                aqi = "no",    // Параметр для качества воздуха
                alerts = "no"  // Параметр для предупреждений
            )

            val cityName = weatherResponse.location.name
            val currentTemp = weatherResponse.current.temp_c
            val conditionText = weatherResponse.current.condition.text

            val title = "Прогноз погоды для $cityName"
            val message = "Сегодня: ${conditionText}, ${currentTemp}°C. Приятного дня!"

            showWeatherNotification(applicationContext, title, message)
            Log.d(TAG, "Weather notification sent for $cityName.")
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather for notification: ${e.message}", e)
            val errorMessage = when {
                e is java.net.SocketTimeoutException -> "Таймаут соединения. Проверьте интернет."
                e is java.net.UnknownHostException -> "Нет интернет-соединения."
                else -> "Ошибка при получении данных для уведомления: ${e.localizedMessage ?: "неизвестная ошибка"}"
            }
            showWeatherNotification(
                applicationContext,
                "Ошибка уведомления о погоде",
                errorMessage
            )
            return Result.retry()
        }
    }

    companion object {
        private const val TAG = "WeatherWorker"
        const val WORK_NAME = "DailyWeatherNotification"
    }
}
