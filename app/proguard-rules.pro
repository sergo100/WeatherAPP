# Правила для Retrofit и OkHttp
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Правила для Gson
-dontwarn com.google.gson.**
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * implements com.google.gson.TypeAdapterFactory { *; }
-keep class * implements com.google.gson.JsonSerializer { *; }
-keep class * implements com.google.gson.JsonDeserializer { *; }

# ОЧЕНЬ ВАЖНО: Сохраняет ваши модели данных Kotlin!
# Это гарантирует, что классы и их поля не будут обфусцированы.
# Пакет 'com.example.space_ranger3209.data' должен ТОЧНО соответствовать вашим моделям.
-keep class com.example.space_ranger3209.data.** { *; } # ИСПРАВЛЕНО: Правильный пакет для моделей

# Дополнительные правила для Kotlin data class
-keepclassmembers class ** {
    @kotlin.Metadata <methods>;
}

# Android специфичные правила
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# ViewBinding (если используется, хотя в Compose не так актуально)
-keepclassmembers class * extends android.view.View {
    <init>(android.content.Context);
    <init>(android.content.Context, android.util.AttributeSet);
    <init>(android.content.Context, android.util.AttributeSet, int);
}

# Дополнительные настройки
-keepattributes SourceFile,LineNumberTable

# Игнорировать предупреждения (полезно для уменьшения "шума" в логах сборки)
-dontwarn kotlin.**
-dontwarn com.google.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**
