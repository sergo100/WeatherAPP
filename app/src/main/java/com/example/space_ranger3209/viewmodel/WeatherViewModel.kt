package com.example.space_ranger3209.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.space_ranger3209.weatherapp.BuildConfig // ИСПРАВЛЕНО: Правильный импорт BuildConfig
import com.example.space_ranger3209.data.WeatherApi
import com.example.space_ranger3209.data.WeatherResponse
import com.example.space_ranger3209.data.CityDataStore
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

    // ИСПРАВЛЕНО: Теперь BuildConfig будет корректно доступен
    private val API_KEY = BuildConfig.WEATHER_API_KEY.removeSurrounding("\"")
    private val BASE_URL = "https://api.weatherapi.com/v1/"

    init {
        Log.d(TAG, "API Key accessed: ${API_KEY.take(5)}...")
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

    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _currentCity = MutableStateFlow<String?>(null)
    val currentCity: StateFlow<String?> = _currentCity.asStateFlow()

    fun loadSavedCityAndFetchWeather() {
        viewModelScope.launch {
            Log.d(TAG, "loadSavedCityAndFetchWeather: Started.")
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val savedCity = cityDataStore.getCityName()
                if (!savedCity.isNullOrBlank()) {
                    Log.d(TAG, "loadSavedCityAndFetchWeather: Saved city found: $savedCity")
                    _currentCity.value = savedCity
                    fetchWeatherData(savedCity)
                } else {
                    Log.d(TAG, "loadSavedCityAndFetchWeather: No saved city found.")
                }
            } catch (e: Exception) {
                handleError(e, "сохраненную погоду")
            } finally {
                _isLoading.value = false
                Log.d(TAG, "loadSavedCityAndFetchWeather: Finished. IsLoading: ${_isLoading.value}")
            }
        }
    }

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            Log.d(TAG, "fetchWeather: Started for city: $city")
            _isLoading.value = true
            _errorMessage.value = null
            try {
                fetchWeatherData(city)
                cityDataStore.saveCityName(city)
                _currentCity.value = city
                Log.d(TAG, "fetchWeather: Successfully fetched weather and saved city: $city")
            } catch (e: Exception) {
                handleError(e, "город $city")
            } finally {
                _isLoading.value = false
                Log.d(TAG, "fetchWeather: Finished. IsLoading: ${_isLoading.value}")
            }
        }
    }

    fun refreshWeather() {
        _currentCity.value?.let { city ->
            Log.d(TAG, "refreshWeather: Refreshing for city: $city")
            fetchWeather(city)
        } ?: run {
            Log.d(TAG, "refreshWeather: No current city to refresh.")
            _errorMessage.value = "Нет выбранного города для обновления погоды."
        }
    }

    private suspend fun fetchWeatherData(city: String) {
        Log.d(TAG, "fetchWeatherData: Making API call for city '$city' with API_KEY: '${API_KEY.take(5)}...'")
        try {
            val response = weatherApi.getWeatherForecast(
                q = city,
                key = API_KEY,
                days = 3,
                lang = "ru"
            )
            _weatherState.value = response
            _errorMessage.value = null
            Log.d(TAG, "fetchWeatherData: API call successful. Response data received.")
        } catch (e: Exception) {
            Log.e(TAG, "fetchWeatherData: API call failed!", e)
            throw e
        }
    }

    private fun handleError(e: Exception, context: String) {
        val errorMessageText = when {
            e is java.net.SocketTimeoutException -> "Превышено время ожидания ответа от сервера. Проверьте стабильность соединения."
            e is java.net.UnknownHostException -> "Отсутствует интернет-соединение или неверный адрес сервера."
            e.message?.contains("403") == true -> "Ошибка доступа (403). Проверьте API ключ или ограничения API."
            e.message?.contains("400") == true -> "Некорректный запрос (400). Проверьте название города или параметры запроса."
            e.message?.contains("404") == true -> "Город не найден (404). Пожалуйста, введите корректное название города."
            e is retrofit2.HttpException -> "HTTP ошибка: Код ${e.code()}. ${e.message()}"
            else -> "Ошибка при получении данных для $context: ${e.localizedMessage ?: "Неизвестная ошибка"}"
        }
        _errorMessage.value = errorMessageText
        _weatherState.value = null
        Log.e(TAG, "Handled error: $errorMessageText", e)
    }

    fun clearError() {
        _errorMessage.value = null
        Log.d(TAG, "clearError: Error message cleared.")
    }

    companion object {
        private const val TAG = "WeatherViewModel"
    }
}
