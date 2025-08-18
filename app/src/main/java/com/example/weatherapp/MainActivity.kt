package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.WeatherScreen
import com.example.weatherapp.ui.AboutScreen
import com.example.weatherapp.ui.HelpScreen
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.google.android.gms.ads.MobileAds
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapp.workers.DailyWeatherNotificationWorker
import com.example.weatherapp.utils.createNotificationChannel
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация AdMob
        MobileAds.initialize(this) {}

        // Создаем канал уведомлений
        createNotificationChannel(this)

        // Планируем ежедневное уведомление о погоде
        scheduleDailyWeatherNotification()

        // Для проверки API_KEY
        Log.d("API_KEY_CHECK", "BuildConfig.WEATHER_API_KEY = '${BuildConfig.WEATHER_API_KEY}'")

        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val weatherViewModel: WeatherViewModel = viewModel()

                    NavHost(navController = navController, startDestination = "weather_screen") {
                        composable("weather_screen") {
                            WeatherScreen(weatherViewModel = weatherViewModel, navController = navController)
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

    private fun scheduleDailyWeatherNotification() {
        val currentTime = Calendar.getInstance()
        val eightAm = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (currentTime.after(eightAm)) eightAm.add(Calendar.DAY_OF_MONTH, 1)
        val initialDelay = eightAm.timeInMillis - currentTime.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyWeatherNotificationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(DailyWeatherNotificationWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            DailyWeatherNotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
        Log.d("Scheduler", "Daily weather notification scheduled.")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherAppTheme {
        val navController = rememberNavController()
        val weatherViewModel: WeatherViewModel = viewModel()
        WeatherScreen(weatherViewModel = weatherViewModel, navController = navController)
    }
}
