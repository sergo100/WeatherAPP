# ============================================
# ProGuard правила для WeatherApp
# ============================================

# Основные настройки
-dontoptimize                     # Отключаем оптимизацию (можно включить позже, если нужно)
-dontshrink                       # Отключаем сжатие (добавлено для совместимости с isShrinkResources)
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose                          # Включаем подробные логи для отладки ProGuard

# Сохранение аннотаций и метаданных
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes EnclosingMethod
-keepattributes Signature          # Для поддержки generic-типов (Gson)

# ============================================
# Правила для Kotlin и Compose
# ============================================

# Сохранение метаданных Kotlin
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# Сохранение Preview-функций Compose
-keepclassmembers class ** {
    @androidx.compose.ui.tooling.preview.Preview *;
}

# Игнорирование предупреждений для Kotlin
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**

# ============================================
# Правила для Retrofit и OkHttp
# ============================================

# Сохранение Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Сохранение OkHttp и Okio
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn okio.**

# ============================================
# Правила для Gson
# ============================================

# Сохранение Gson и его интерфейсов
-dontwarn com.google.gson.**
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * implements com.google.gson.TypeAdapterFactory { *; }
-keep class * implements com.google.gson.JsonSerializer { *; }
-keep class * implements com.google.gson.JsonDeserializer { *; }

# ============================================
# Правила для моделей данных
# ============================================

# Сохранение всех классов и членов в пакете моделей
-keep class com.example.space_ranger3209.data.** { *; }
-keepclassmembers class com.example.space_ranger3209.data.** { *; }

# Если у вас есть другие пакеты с моделями, добавьте их
# -keep class com.example.space_ranger3209.weatherapp.data.models.** { *; }
# -keepclassmembers class com.example.space_ranger3209.weatherapp.data.models.** { *; }

# ============================================
# Правила для Android
# ============================================

# Сохранение Activity, Service, BroadcastReceiver
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends androidx.fragment.app.Fragment

# Сохранение ViewBinding (если используется, хотя в Compose это редко)
-keepclassmembers class * extends android.view.View {
    <init>(android.content.Context);
    <init>(android.content.Context, android.util.AttributeSet);
    <init>(android.content.Context, android.util.AttributeSet, int);
}

# Сохранение ресурсов и R-класса
-keepclassmembers class **.R$* {
    public static <fields>;
}

# ============================================
# Правила для Firebase
# ============================================

# Сохранение Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ============================================
# Правила для WorkManager и других библиотек
# ============================================

# Сохранение WorkManager
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Сохранение Coil (для изображений)
-keep class io.coil.** { *; }