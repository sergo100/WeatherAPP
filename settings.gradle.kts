// Определяет плагины, используемые в проекте.
// Здесь Gradle определяет, откуда брать плагины и какие версии использовать.
pluginManagement {
    repositories {
        // Репозиторий Google для плагинов Gradle
        google()
        // Репозиторий Maven Central для плагинов Gradle
        mavenCentral()
        // Репозиторий Gradle Plugin Portal - стандартный источник для большинства плагинов
        gradlePluginPortal()
    }
    // Здесь объявляются плагины и их версии, которые будут доступны во всем проекте.
    plugins {
        // Android Application Plugin - основной плагин для сборки Android-приложений
        id("com.android.application") version "8.12.0" // Актуальная версия Android Gradle Plugin
        // Kotlin Android Plugin - для поддержки Kotlin в Android-проектах
        id("org.jetbrains.kotlin.android") version "1.9.10" // Версия Kotlin должна совпадать
        // Google Services Plugin - для интеграции Firebase
        id("com.google.gms.google-services") version "4.3.15" // Последняя стабильная версия Firebase Google Services
        // Secrets Gradle Plugin - для безопасного хранения API-ключей (например, для Google Maps)
        id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" // Последняя стабильная версия
    }
}

// Определяет репозитории для зависимостей всех модулей проекта.
// Это гарантирует, что все модули используют одни и те же источники зависимостей,
// что помогает избежать конфликтов.
dependencyResolutionManagement {
    // Рекомендуемый режим для безопасности и предсказуемости.
    // Если зависимость не найдена в объявленных репозиториях, сборка завершится ошибкой.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Репозиторий Google для AndroidX и других Google библиотек
        google()
        // Репозиторий Maven Central для многих сторонних библиотек
        mavenCentral()
    }
}

// Название вашего корневого проекта.
rootProject.name = "WeatherApp"
// Включает модуль вашего приложения в проект.
// Если у вас будут другие модули (например, :data, :domain), они тоже добавляются сюда.
include(":app")
