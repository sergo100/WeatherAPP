package com.example.weatherapp.data

// --------------------- API MODELS ---------------------

data class WeatherResponse(
    val location: Location,
    val current: CurrentWeather,
    val forecast: Forecast
) {
    // Конвертация из API-ответа в простую модель для UI
    fun toWeatherModel(): WeatherModel {
        val today = forecast.forecastday.firstOrNull()
        val condition = current.condition.text
        val iconUrl = "https:${current.condition.icon}"

        return WeatherModel(
            city = location.name,
            country = location.country,
            temperature = "${current.temp_c}°C",
            condition = condition,
            iconUrl = iconUrl,
            forecast = today?.hour?.map {
                ForecastModel(
                    time = it.time.takeLast(5), // оставляем только "часы:минуты"
                    temperature = "${it.temp_c}°C",
                    condition = it.condition.text,
                    iconUrl = "https:${it.condition.icon}"
                )
            } ?: emptyList()
        )
    }
}

data class Location(
    val name: String,
    val region: String,
    val country: String
)

data class CurrentWeather(
    val temp_c: Double,
    val condition: Condition,
    val wind_kph: Double,
    val humidity: Int
)

data class Condition(
    val text: String,
    val icon: String
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour>
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val condition: Condition
)

data class Hour(
    val time: String,
    val temp_c: Double,
    val condition: Condition
)

// --------------------- UI MODELS ---------------------

data class WeatherModel(
    val city: String,
    val country: String,
    val temperature: String,
    val condition: String,
    val iconUrl: String,
    val forecast: List<ForecastModel>
)

data class ForecastModel(
    val time: String,
    val temperature: String,
    val condition: String,
    val iconUrl: String
)
