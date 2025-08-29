// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Этот файл обычно остается очень простым в современных проектах.
// Большая часть конфигурации (pluginManagement, dependencyResolutionManagement)
// теперь находится в settings.gradle.kts

// ЗАКОММЕНТИРОВАНО: Этот блок 'tasks.register("clean", Delete::class) { ... }' обычно не нужен
// в современных проектах, так как 'gradle clean' работает по умолчанию.
// Если вы хотите его оставить, убедитесь, что Gradle знает о классе Delete.
// Для упрощения, обычно его удаляют.
// tasks.register("clean", Delete::class) {
//     delete(rootProject.buildDir)
// }

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
        // ИСПРАВЛЕНО: Обновлена версия Android Application Plugin для лучшей совместимости с JDK.
        id("com.android.application") version "8.12.2" // <-- ИЗМЕНЕНО!
        // Kotlin Android Plugin - для поддержки Kotlin в Android-проектах
        // ИСПРАВЛЕНО: Обновлена версия Kotlin до 1.9.22, чтобы соответствовать Compose Compiler 1.5.10
        id("org.jetbrains.kotlin.android") version "1.9.22" // <-- ИСПРАВЛЕНО!
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
