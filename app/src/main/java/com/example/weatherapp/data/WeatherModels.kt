package com.example.weatherapp.data // ОБЯЗАТЕЛЬНО: Это должен быть ваш правильный package

// --- Модели данных для ответа от API (weatherapi.com) ---

// Основной класс ответа от WeatherAPI.com
data class WeatherResponse(
    val location: Location, // Информация о местоположении
    val current: CurrentWeather, // Текущая погода
    val forecast: Forecast // Прогноз погоды
)

// Информация о местоположении
data class Location(
    val name: String, // Название города
    val region: String?, // Регион (область/штат)
    val country: String, // Страна
    val lat: Double, // Широта
    val lon: Double, // Долгота
    val localtime: String // Локальное время
)

// Текущая погода
data class CurrentWeather(
    val temp_c: Double, // Температура в градусах Цельсия
    val condition: Condition, // Описание погодного условия
    val wind_kph: Double, // Скорость ветра в км/ч
    val humidity: Int, // Влажность
    val feelslike_c: Double // Ощущаемая температура в Цельсиях
)

// Описание погодного условия (для текущей и прогнозируемой погоды)
data class Condition(
    val text: String, // Текстовое описание (например, "Ясно", "Пасмурно")
    val icon: String // URL иконки погоды
)

// Прогноз погоды
data class Forecast(
    val forecastday: List<ForecastDay> // Список прогнозов по дням
)

// Прогноз на один день
data class ForecastDay(
    val date: String, // Дата в формате YYYY-MM-DD
    val day: DayDetails, // Детали погоды за день
    val astro: Astro // Астрономические данные (восход/закат)
)

// Детали погоды за день
data class DayDetails(
    val maxtemp_c: Double, // Максимальная температура в Цельсиях
    val mintemp_c: Double, // Минимальная температура в Цельсиях
    val avgtemp_c: Double, // Средняя температура в Цельсиях
    val condition: Condition // Описание погодного условия за день
    // Могут быть и другие поля: maxwind_kph, totalprecip_mm, avghumidity, uv и т.д.
)

// Астрономические данные
data class Astro(
    val sunrise: String, // Время восхода солнца
    val sunset: String // Время заката солнца
)
