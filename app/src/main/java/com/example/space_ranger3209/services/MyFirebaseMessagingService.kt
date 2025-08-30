package com.example.space_ranger3209.weatherapp.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "Уведомление получено от: ${remoteMessage.from}")

        // Логирование данных полезной нагрузки (payload data)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Данные полезной нагрузки сообщения: ${remoteMessage.data}")
            // Здесь вы можете обрабатывать эти данные
        }

        // Логирование содержимого уведомления (title и body)
        remoteMessage.notification?.let {
            Log.d(TAG, "Заголовок уведомления: ${it.title}")
            Log.d(TAG, "Тело уведомления: ${it.body}")
            // Здесь вы можете извлечь заголовок и тело уведомления
            // showWeatherNotification(applicationContext, it.title ?: "Уведомление", it.body ?: "Новое сообщение")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Новый токен регистрации FCM: $token")
        // Отправьте этот токен на ваш сервер приложений, если у вас есть бэкенд
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService" // Тег для Logcat
    }
}
