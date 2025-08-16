package com.example.weatherapp // Правильный пакет для Activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.WeatherScreen
import com.example.weatherapp.ui.AboutScreen
import com.example.weatherapp.ui.HelpScreen

// Импорты для Google Mobile Ads SDK и WorkManager
import com.google.android.gms.ads.MobileAds
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapp.workers.DailyWeatherNotificationWorker // НОВЫЙ ИМПОРТ: Для нашей фоновой задачи
import com.example.weatherapp.utils.createNotificationChannel // НОВЫЙ ИМПОРТ: Для создания канала уведомлений
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.util.Log // Для логирования

// Главная активность приложения
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ИНИЦИАЛИЗАЦИЯ ADMOB SDK: Инициализирует рекламный SDK при запуске приложения.
        MobileAds.initialize(this) {}

        // СОЗДАНИЕ КАНАЛА УВЕДОМЛЕНИЙ И ПЛАНИРОВАНИЕ РАБОТЫ:
        // Создаем канал уведомлений. Это нужно сделать только один раз.
        createNotificationChannel(this)
        // Планируем ежедневную фоновую задачу для уведомлений о погоде.
        scheduleDailyWeatherNotification()

        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "weather_screen") {
                        composable("weather_screen") {
                            WeatherScreen(navController = navController)
                        }
                        composable("about_screen") {
                            AboutScreen(navController = navController)
                        }
                        composable("help_screen") {
                            HelpScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }

    /**
     * Планирует ежедневное уведомление о погоде на 8:00 утра.
     * Задача будет запускаться каждый день в 8:00 утра, используя WorkManager.
     */
    private fun scheduleDailyWeatherNotification() {
        // Получаем текущее время, чтобы рассчитать задержку до 8:00 следующего дня.
        val currentTime = Calendar.getInstance()
        val eightAm = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8) // Устанавливаем час на 8 утра
            set(Calendar.MINUTE, 0)      // Устанавливаем минуты на 0
            set(Calendar.SECOND, 0)      // Устанавливаем секунды на 0
            set(Calendar.MILLISECOND, 0) // Устанавливаем миллисекунды на 0
        }

        // Если текущее время уже после 8 утра, планируем на 8 утра следующего дня.
        if (currentTime.after(eightAm)) {
            eightAm.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Вычисляем задержку в миллисекундах до первого запуска задачи.
        val initialDelay = eightAm.timeInMillis - currentTime.timeInMillis
        Log.d("Scheduler", "Initial delay for weather notification: $initialDelay ms")

        // Создаем запрос на периодическую работу (выполняется регулярно).
        // Задача будет повторяться каждые 24 часа.
        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyWeatherNotificationWorker>(
            repeatInterval = 24, // Интервал повторения: 24 часа
            repeatIntervalTimeUnit = TimeUnit.HOURS // Единица измерения интервала: часы
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS) // Устанавливаем начальную задержку
            .addTag(DailyWeatherNotificationWorker.WORK_NAME) // Добавляем тег для уникальной идентификации работы
            .build() // Строим запрос на работу

        // Планируем уникальную периодическую работу.
        // ExistingPeriodicWorkPolicy.REPLACE: если работа с таким же именем уже существует, она будет заменена новой.
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            DailyWeatherNotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyWorkRequest
        )
        Log.d("Scheduler", "Daily weather notification scheduled.")
    }
}

// Пример предпросмотра для Android Studio
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherAppTheme {
        WeatherScreen(navController = rememberNavController())
    }
}
