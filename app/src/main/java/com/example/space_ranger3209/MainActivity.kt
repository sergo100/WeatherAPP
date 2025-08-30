package com.example.space_ranger3209.weatherapp // ИСПРАВЛЕНО: Пакет должен соответствовать applicationId

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.space_ranger3209.ui.WeatherScreen
import com.example.space_ranger3209.ui.AboutScreen
import com.example.space_ranger3209.ui.HelpScreen
import com.example.space_ranger3209.ui.theme.WeatherAppTheme
import com.example.space_ranger3209.viewmodel.WeatherViewModel
import com.google.android.gms.ads.MobileAds
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints // Добавлен импорт Constraints
import androidx.work.NetworkType // Добавлен импорт NetworkType
import com.example.space_ranger3209.utils.createNotificationChannel
import com.example.space_ranger3209.workers.DailyWeatherNotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
// import com.google.firebase.messaging.FirebaseMessaging // <-- УДАЛЕН ИМПОРТ
import android.util.Log // <-- УДАЛЕН ИМПОРТ, если не используется в другом месте

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}
        createNotificationChannel(this)
        scheduleDailyWeatherNotification()

        // УДАЛЕН ВРЕМЕННЫЙ КОД ДЛЯ ЛОГИРОВАНИЯ FCM ТОКЕНА
        /*
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_TOKEN", "Current token: $token")
        }
        */
        // КОНЕЦ УДАЛЕННОГО КОДА

        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
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

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyWeatherNotificationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 1,
            flexTimeIntervalUnit = TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(DailyWeatherNotificationWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            DailyWeatherNotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
    }
}
