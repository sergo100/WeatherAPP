package com.example.space_ranger3209.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.space_ranger3209.weatherapp.R // <- правильный импорт R
import android.Manifest
import androidx.core.content.ContextCompat
import android.annotation.SuppressLint

const val WEATHER_NOTIFICATION_CHANNEL_ID = "weather_notification_channel"
const val WEATHER_NOTIFICATION_CHANNEL_NAME = "Ежедневная погода"
const val WEATHER_NOTIFICATION_CHANNEL_DESCRIPTION = "Ежедневный прогноз погоды"
const val WEATHER_NOTIFICATION_ID = 1001

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            WEATHER_NOTIFICATION_CHANNEL_ID,
            WEATHER_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = WEATHER_NOTIFICATION_CHANNEL_DESCRIPTION
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

@SuppressLint("MissingPermission")
fun showWeatherNotification(context: Context, title: String, message: String) {
    // Проверка разрешения для Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    // Проверка наличия ресурса иконки
    val smallIconRes = context.resources.getIdentifier(
        "ic_weather_notification",
        "drawable",
        context.packageName
    )
    if (smallIconRes == 0) {
        // Если иконки нет, используем системную
        showFallbackNotification(context, title, message)
        return
    }

    val builder = NotificationCompat.Builder(context, WEATHER_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(smallIconRes)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(WEATHER_NOTIFICATION_ID, builder.build())
    }
}

// Фолбэк на случай отсутствия иконки
private fun showFallbackNotification(context: Context, title: String, message: String) {
    val builder = NotificationCompat.Builder(context, WEATHER_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(WEATHER_NOTIFICATION_ID, builder.build())
    }
}
