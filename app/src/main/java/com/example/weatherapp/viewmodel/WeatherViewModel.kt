package com.example.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.WeatherApi
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.data.CityDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val API_KEY = BuildConfig.WEATHER_API_KEY.removeSurrounding("\"")
    private val BASE_URL = "https://api.weatherapi.com/v1/"

    // Проверка API ключа при инициализации
    init {
        require(API_KEY.isNotEmpty()) {
            "API ключ не настроен. Проверьте local.properties и BuildConfig"
        }
    }

    private val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(15, TimeUnit.SECONDS)
            readTimeout(15, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }.build()
    }

    private val cityDataStore = CityDataStore(application)

    // Состояния UI
    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Добавлено: Состояние для текущего города
    private val _currentCity = MutableStateFlow<String?>(null)
    val currentCity: StateFlow<String?> = _currentCity.asStateFlow()

    fun loadSavedCityAndFetchWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val savedCity = cityDataStore.getCityName()
                if (!savedCity.isNullOrBlank()) {
                    _currentCity.value = savedCity
                    fetchWeatherData(savedCity)
                }
            } catch (e: Exception) {
                handleError(e, "сохраненную погоду")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchWeatherData(city)
                cityDataStore.saveCityName(city)
                _currentCity.value = city
            } catch (e: Exception) {
                handleError(e, "город $city")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Добавлено: Обновление погоды для текущего города
    fun refreshWeather() {
        _currentCity.value?.let { city ->
            fetchWeather(city)
        }
    }

    private suspend fun fetchWeatherData(city: String) {
        val response = weatherApi.getWeatherForecast(
            key = API_KEY,
            q = city,
            days = 3,  // Увеличено количество дней для прогноза
            aqi = "no",
            alerts = "no"
        )
        _weatherState.value = response
        _errorMessage.value = null
    }

    private fun handleError(e: Exception, context: String) {
        _errorMessage.value = when {
            e is java.net.SocketTimeoutException -> "Превышено время ожидания ответа от сервера"
            e is java.net.UnknownHostException -> "Отсутствует интернет-соединение"
            e.message?.contains("403") == true -> "Ошибка доступа. Проверьте API ключ"
            e.message?.contains("400") == true -> "Некорректный запрос. Проверьте название города"
            e.message?.contains("404") == true -> "Город не найден"
            else -> "Ошибка при получении данных для $context: ${e.message ?: "Неизвестная ошибка"}"
        }
        _weatherState.value = null
    }

    // Добавлено: Очистка ошибок
    fun clearError() {
        _errorMessage.value = null
    }
}