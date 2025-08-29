package com.example.space_ranger3209.weatherapp.widget // ИСПРАВЛЕНО: Пакет теперь соответствует структуре приложения

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.RemoteViews
import com.example.space_ranger3209.weatherapp.BuildConfig // Правильный импорт BuildConfig
import com.example.space_ranger3209.weatherapp.R // Правильный импорт R
import com.example.space_ranger3209.weatherapp.MainActivity // Правильный импорт MainActivity
import com.example.space_ranger3209.data.CityDataStore
import com.example.space_ranger3209.data.WeatherApi
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class WeatherAppWidget : AppWidgetProvider() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val TAG = "WeatherAppWidget"

    // Инициализация API для виджета
    private val weatherApi: WeatherApi by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate called for widget.")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        Log.d(TAG, "onEnabled: First widget created.")
    }

    override fun onDisabled(context: Context) {
        Log.d(TAG, "onDisabled: Last widget removed.")
        job.cancel()
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_city_name, pendingIntent)

        scope.launch {
            try {
                Log.d(TAG, "Attempting to update widget data.")

                if (!isNetworkAvailable(context)) {
                    Log.w(TAG, "No network available for widget update.")
                    views.setTextViewText(R.id.widget_city_name, "Нет интернета 🌐")
                    views.setTextViewText(R.id.widget_temperature, "--°C")
                    views.setTextViewText(R.id.widget_condition, "Проверьте соединение")
                    views.setTextViewText(R.id.widget_last_updated, "Обновлено: " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
                    withContext(Dispatchers.Main) {
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                    return@launch
                }

                val cityDataStore = CityDataStore(context)
                val savedCity = cityDataStore.getCityName()

                val apiKey = BuildConfig.WEATHER_API_KEY.removeSurrounding("\"")

                if (apiKey.isBlank()) {
                    Log.e(TAG, "API Key is blank. Cannot fetch weather for widget.")
                    views.setTextViewText(R.id.widget_city_name, "Ошибка API 🔑")
                    views.setTextViewText(R.id.widget_temperature, "--°C")
                    views.setTextViewText(R.id.widget_condition, "Ключ не настроен")
                    views.setTextViewText(R.id.widget_last_updated, "Обновлено: " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
                    withContext(Dispatchers.Main) {
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                    return@launch
                }

                if (savedCity.isNullOrBlank()) {
                    Log.d(TAG, "No city saved for widget. Prompting user to open app.")
                    views.setTextViewText(R.id.widget_city_name, "Нет города 📍")
                    views.setTextViewText(R.id.widget_temperature, "--°C")
                    views.setTextViewText(R.id.widget_condition, "Нажмите для настройки")
                    views.setTextViewText(R.id.widget_last_updated, "Обновлено: " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
                } else {
                    Log.d(TAG, "Fetching weather for widget for city: $savedCity")
                    val weatherResponse = weatherApi.getWeatherForecast(
                        q = savedCity,
                        key = apiKey,
                        days = 1,
                        lang = "ru"
                    )

                    val cityName = weatherResponse.location.name
                    val currentTemp = weatherResponse.current.temp_c
                    val conditionText = weatherResponse.current.condition.text

                    views.setTextViewText(R.id.widget_city_name, cityName)
                    views.setTextViewText(R.id.widget_temperature, "$currentTemp°C")
                    views.setTextViewText(R.id.widget_condition, conditionText)
                    views.setTextViewText(R.id.widget_last_updated, "Обновлено: " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
                    Log.d(TAG, "Widget data updated successfully for $cityName.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating widget: ${e.message}", e)
                views.setTextViewText(R.id.widget_city_name, "Ошибка 🐞")
                views.setTextViewText(R.id.widget_temperature, "--°C")
                views.setTextViewText(R.id.widget_condition, "Ошибка загрузки")
                views.setTextViewText(R.id.widget_last_updated, "Обновлено: " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
            } finally {
                withContext(Dispatchers.Main) {
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}
