package com.example.weatherapp.utils // Новый пакет для утилит

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weatherapp.R
import android.Manifest
import androidx.core.content.ContextCompat
import android.annotation.SuppressLint // НОВЫЙ ИМПОРТ: для подавления предупреждений

// ID канала уведомлений
const val WEATHER_NOTIFICATION_CHANNEL_ID = "weather_notification_channel"
// Имя канала уведомлений, видимое пользователю
const val WEATHER_NOTIFICATION_CHANNEL_NAME = "Ежедневная погода"
// Описание канала уведомлений
const val WEATHER_NOTIFICATION_CHANNEL_DESCRIPTION = "Ежедневный прогноз погоды"
// ID для уникальности уведомления
const val WEATHER_NOTIFICATION_ID = 1001

/**
 * Создает канал уведомлений.
 * Вызывается при первом запуске приложения или при необходимости.
 * @param context Контекст приложения.
 */
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

/**
 * Отображает уведомление о погоде.
 * @param context Контекст приложения.
 * @param title Заголовок уведомления.
 * @param message Сообщение уведомления.
 */
@SuppressLint("MissingPermission") // ДОБАВЛЕНО: Подавляем предупреждение о недостающем разрешении
fun showWeatherNotification(context: Context, title: String, message: String) {
    // Проверяем разрешение POST_NOTIFICATIONS для Android 13 (API 33) и выше
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU == API 33
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешение не предоставлено.
            // В реальном приложении здесь нужно было бы запросить разрешение у пользователя
            // через Activity, но Worker не может это сделать напрямую.
            // Уведомление не будет показано без разрешения.
            return
        }
    }

    val builder = NotificationCompat.Builder(context, WEATHER_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground) // Временно используем стандартную иконку
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    // Отправляем уведомление
    with(NotificationManagerCompat.from(context)) {
        notify(WEATHER_NOTIFICATION_ID, builder.build())
    }
}
