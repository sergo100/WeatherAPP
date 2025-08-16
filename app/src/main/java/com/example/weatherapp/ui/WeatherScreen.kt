@file:OptIn(ExperimentalMaterial3Api::class) // Подавляет экспериментальные API для всего файла

package com.example.weatherapp.ui // Правильный пакет для UI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Добавлен для LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons // Добавлен для иконок
import androidx.compose.material.icons.filled.Info // Добавлен для иконок
import androidx.compose.material.icons.filled.ShoppingCart // Добавлен для иконок
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.ForecastDay // Добавлен для отображения прогноза по дням
import com.example.weatherapp.data.WeatherResponse // Используем WeatherResponse
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme

// Основной экран погоды
@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
    navController: NavController = rememberNavController()
) {
    var cityInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val weatherState by weatherViewModel.weatherState.collectAsState()
    val isLoading by weatherViewModel.isLoading.collectAsState()
    val errorMessage by weatherViewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        weatherViewModel.loadSavedCityAndFetchWeather()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val cityName = weatherState?.location?.name
                    if (!cityName.isNullOrBlank()) {
                        Text("Погода в городе: $cityName ☀️")
                    } else {
                        Text("Мой Погодный Бот")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize() // Заполняет весь доступный размер
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Центрирование содержимого по горизонтали
        ) {
            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("Введите город") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (cityInput.isNotBlank()) {
                            weatherViewModel.fetchWeather(cityInput)
                            keyboardController?.hide()
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row( // Добавлена Row для кнопок "Получить погоду" и "Прогноз на 3 дня"
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        if (cityInput.isNotBlank()) {
                            weatherViewModel.fetchWeather(cityInput) // Получает 3 дня по умолчанию
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text("Получить погоду")
                }

                Button( // НОВАЯ КНОПКА: Прогноз на 3 дня
                    onClick = {
                        if (cityInput.isNotBlank()) {
                            weatherViewModel.fetchWeather(cityInput) // Также получает 3 дня по умолчанию
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Text("Прогноз на 3 дня")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
            }

            errorMessage?.let { message ->
                Text(
                    text = "Ошибка: $message",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            weatherState?.location?.name?.let { cityName ->
                Text(
                    text = "Погода в городе: $cityName",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                weatherState?.current?.temp_c?.let { currentTemp ->
                    val currentConditionText = weatherState?.current?.condition?.text ?: ""
                    Text(
                        text = "Текущая температура: ${currentTemp}°C ${getWeatherEmoji(currentConditionText)}", // ДОБАВЛЕН ЭМОДЗИ
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // ОБНОВЛЕНО: Возвращен LazyColumn для отображения прогноза на несколько дней
            weatherState?.forecast?.forecastday?.let { forecasts ->
                if (forecasts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp)) // Отступ перед прогнозом
                    Text(
                        text = "Прогноз на ближайшие дни:",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                            .weight(1f) // Занимает доступное пространство
                    ) {
                        items(forecasts) { forecastDay ->
                            WeatherForecastCard(forecastDay = forecastDay)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else if (!isLoading && errorMessage == null && !cityInput.isNullOrBlank()) {
                    Text("Нет данных прогноза для этого города.")
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Дополнительный отступ

            // Возвращены кнопки "Об авторе" и "Помочь проекту"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        navController.navigate("about_screen") // Навигация на AboutScreen
                    },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    // Иконки
                    // import androidx.compose.material.icons.Icons
                    // import androidx.compose.material.icons.filled.Info
                    Icon(Icons.Filled.Info, contentDescription = "Об авторе")
                    Spacer(Modifier.width(8.dp))
                    Text("Об авторе")
                }
                Button(
                    onClick = {
                        navController.navigate("help_screen") // Навигация на HelpScreen
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    // Иконки
                    // import androidx.compose.material.icons.Icons
                    // import androidx.compose.material.icons.filled.ShoppingCart
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Помочь проекту")
                    Spacer(Modifier.width(8.dp))
                    Text("Помочь проекту")
                }
            }
            // TODO: Здесь будет место для рекламного баннера AdMob
        }
    }
}

// ОБНОВЛЕНО: Композируемая функция для отображения одной карточки прогноза погоды
// Принимает ForecastDay из weatherapi.com
@Composable
fun WeatherForecastCard(forecastDay: ForecastDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Дата уже в формате "YYYY-MM-DD" от weatherapi.com
            Text(
                text = forecastDay.date,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Макс. температура: ${forecastDay.day.maxtemp_c}°C",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Мин. температура: ${forecastDay.day.mintemp_c}°C",
                style = MaterialTheme.typography.bodyLarge
            )
            Row(verticalAlignment = Alignment.CenterVertically) { // Добавлена Row для текста и эмодзи
                Text(
                    text = "Описание: ${forecastDay.day.condition.text}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(getWeatherEmoji(forecastDay.day.condition.text), style = MaterialTheme.typography.bodyMedium)
            }

            // Здесь можно добавить иконку погоды, если нужно:
            // Image(
            //     painter = rememberAsyncImagePainter("https:${forecastDay.day.condition.icon}"),
            //     contentDescription = "Иконка погоды",
            //     modifier = Modifier.size(48.dp)
            // )
        }
    }
}

// НОВАЯ ФУНКЦИЯ: Возвращает эмодзи на основе описания погоды
fun getWeatherEmoji(conditionText: String): String {
    return when {
        conditionText.contains("солнечно", ignoreCase = true) || conditionText.contains("ясно", ignoreCase = true) -> "☀️"
        conditionText.contains("облачно", ignoreCase = true) || conditionText.contains("пасмурно", ignoreCase = true) -> "☁️"
        conditionText.contains("дождь", ignoreCase = true) -> "🌧️"
        conditionText.contains("снег", ignoreCase = true) -> "❄️"
        conditionText.contains("гроза", ignoreCase = true) -> "⛈️"
        conditionText.contains("туман", ignoreCase = true) || conditionText.contains("дымка", ignoreCase = true) -> "🌫️"
        else -> "❓" // Эмодзи по умолчанию
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreen() {
    WeatherAppTheme {
        WeatherScreen()
    }
}
