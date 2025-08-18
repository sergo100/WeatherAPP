# ============================================
# ProGuard правила для WeatherApp
# ============================================

# Общие настройки
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# --------------------------------------------
# Основные библиотеки
# --------------------------------------------

# Kotlin
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# --------------------------------------------
# Модели данных приложения
# --------------------------------------------

# Сохраняем все модели данных (адаптируйте под ваш пакет)
-keep class com.example.weatherapp.data.model.** { *; }
-keepclassmembers class com.example.weatherapp.data.model.** {
    *;
}

# --------------------------------------------
# Android специфичные правила
# --------------------------------------------

# Активности, сервисы и др. компоненты
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# ViewBinding
-keepclassmembers class * extends android.view.View {
    <init>(android.content.Context);
    <init>(android.content.Context, android.util.AttributeSet);
    <init>(android.content.Context, android.util.AttributeSet, int);
}

# --------------------------------------------
# Дополнительные настройки
# --------------------------------------------

# Для дебага (можно отключить в релизе)
-keepattributes SourceFile,LineNumberTable

# Игнорировать предупреждения
-dontwarn kotlin.**
-dontwarn com.google.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**