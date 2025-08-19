package com.example.space_ranger3209.services // ИСПРАВЛЕНО: Правильный пакет для сервисов

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Класс MyFirebaseMessagingService обрабатывает входящие Firebase Cloud Messages (FCM)
class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Этот метод вызывается, когда приложение получает сообщение FCM.
    // Здесь вы можете обрабатывать данные сообщения и отображать уведомления.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Логируем ID сообщения
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Проверяем, содержит ли сообщение данные.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            // TODO: Здесь вы можете обработать данные, например, показать уведомление
            // scheduleJob() // Если вам нужна фоновая задача
        }

        // Проверяем, содержит ли сообщение уведомление.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // TODO: Здесь вы можете извлечь заголовок и тело уведомления
            // sendNotification(it.title, it.body)
        }
    }

    // Этот метод вызывается, когда создается новый токен регистрации FCM.
    // Токен необходим для отправки сообщений на конкретное устройство.
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // TODO: Отправьте этот токен на ваш сервер приложений.
        // sendRegistrationToServer(token)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
