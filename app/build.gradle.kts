import java.util.Properties
import java.io.File

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

val apiKey by lazy {
    try {
        val properties = Properties().apply {
            File(rootDir, "local.properties").inputStream().use { load(it) }
        }
        properties.getProperty("WEATHERAPI_TOKEN")?.trim()?.takeIf { it.isNotEmpty() } ?: run {
            logger.error("⛔ WEATHERAPI_TOKEN не найден или пуст в local.properties!")
            "\"\""
        }
    } catch (e: Exception) {
        logger.error("⛔ Ошибка чтения local.properties: ${e.message}")
        "\"\""
    }
}

// НОВЫЙ БЛОК: Загрузка свойств подписи из local.properties
val signingProperties = Properties().apply {
    val signingPropertiesFile = File(rootDir, "local.properties")
    if (signingPropertiesFile.exists()) {
        signingPropertiesFile.inputStream().use { load(it) }
    } else {
        logger.warn("local.properties not found! Signing properties may be missing.")
    }
}

android {
    // ИСПРАВЛЕНО: namespace теперь ТОЧНО соответствует applicationId
    namespace = "com.example.space_ranger3209.weatherapp" // <-- ИЗМЕНЕНО!
    compileSdk = 34

    defaultConfig {
        // ИСПРАВЛЕНО: applicationId теперь соответствует пакету, используемому для R
        applicationId = "com.example.space_ranger3209.weatherapp" // <-- ИСПРАВЛЕНО!
        minSdk = 26
        targetSdk = 34
        versionCode = 52 // ИЗМЕНЕНО: Увеличьте этот номер для каждой новой загрузки (теперь 52)
        versionName = "2.0.12" // Обновите имя версии

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // ИСПРАВЛЕНО: Добавлены кавычки вокруг apiKey для buildConfigField
        buildConfigField("String", "WEATHER_API_KEY", "\"$apiKey\"")
        resValue("string", "google_maps_key", apiKey)
        multiDexEnabled = true
    }

    buildFeatures {
        compose = true
        buildConfig = true // Обязательно для генерации BuildConfig
        viewBinding = true
    }

    composeOptions {
        // ИСПРАВЛЕНО: Обновлена версия расширения компилятора Kotlin для Compose
        kotlinCompilerExtensionVersion = "1.5.10" // <-- ИЗМЕНЕНО!
    }

    // БЛОК: signingConfigs - здесь Gradle узнает, какой ключ использовать для подписи релизной сборки
    signingConfigs {
        create("release") { // Создаем конфигурацию подписи с именем "release"
            // Читаем путь к файлу ключа (keystore) из local.properties
            storeFile = file(signingProperties.getProperty("RELEASE_STORE_FILE") ?: throw GradleException("RELEASE_STORE_FILE not found in local.properties"))
            // Читаем пароль хранилища (keystore password) из local.properties
            storePassword = signingProperties.getProperty("RELEASE_STORE_PASSWORD") ?: throw GradleException("RELEASE_STORE_PASSWORD not found in local.properties")
            // Читаем alias ключа из local.properties
            keyAlias = signingProperties.getProperty("RELEASE_KEY_ALIAS") ?: throw GradleException("RELEASE_KEY_ALIAS not found in local.properties")
            // Читаем пароль ключа из local.properties
            keyPassword = signingProperties.getProperty("RELEASE_KEY_PASSWORD") ?: throw GradleException("RELEASE_KEY_PASSWORD not found in local.properties")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // ВОЗВРАЩЕНО НА TRUE
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" // Подключаем ваши правила ProGuard
            )
            // Применяем созданную выше конфигурацию подписи "release" к нашей релизной сборке
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug" // Суффикс для отладочной сборки
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17 // <-- ИСПРАВЛЕНО на 17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17" // <-- ИСПРАВЛЕНО на 17
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xjvm-default=all"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" // <-- ИЗМЕНЕНО!
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/*.version",
                "kotlin/**",
                "**/DebugProbesKt.bin"
            )
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.startup:startup-runtime:1.1.1")
    implementation("com.google.android.gms:play-services-ads:23.0.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
