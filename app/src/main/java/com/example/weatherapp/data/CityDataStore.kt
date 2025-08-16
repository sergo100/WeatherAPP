package com.example.weatherapp.data // Правильный пакет для данных

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Объявление делегата DataStore
// Context.dataStore - это свойство-расширение, которое возвращает экземпляр DataStore
// Имя "city_preferences" будет использоваться для файла, в котором будут храниться настройки
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "city_preferences")

// Класс для управления сохранением и загрузкой названия города
class CityDataStore(private val context: Context) {

    // Ключ для хранения названия города в DataStore
    private val CITY_NAME_KEY = stringPreferencesKey("city_name")

    /**
     * Сохраняет название города в DataStore.
     * @param cityName Название города для сохранения.
     */
    suspend fun saveCityName(cityName: String) {
        context.dataStore.edit { preferences ->
            // Устанавливаем значение для ключа CITY_NAME_KEY
            preferences[CITY_NAME_KEY] = cityName
        }
    }

    /**
     * Получает сохраненное название города из DataStore.
     * @return Название города или null, если город не сохранен.
     */
    suspend fun getCityName(): String? {
        // map { it[CITY_NAME_KEY] } создает Flow, который преобразует Preferences
        // в значение, связанное с CITY_NAME_KEY.
        // first() приостанавливает выполнение корутины и возвращает первое значение из Flow.
        return context.dataStore.data.map { preferences ->
            preferences[CITY_NAME_KEY]
        }.first()
    }
}
