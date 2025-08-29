package com.example.space_ranger3209.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.space_ranger3209.ui.theme.WeatherAppTheme
import androidx.compose.ui.res.painterResource // ИСПРАВЛЕНО: Импорт для локальных ресурсов
import androidx.compose.ui.platform.LocalContext // Сохраняем, если понадобится в будущем
import androidx.compose.ui.res.stringResource
import com.example.space_ranger3209.weatherapp.R // Правильный импорт R

// Экран "Помощь"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    val context = LocalContext.current // Сохраняем LocalContext на случай, если он понадобится для других целей
    // ИСПРАВЛЕНО: Убраны переменные qrCodeUrl и donationLink

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Помощь проекту") }, // Изменено название экрана
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Поддержите разработку WeatherApp!",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Ваши пожертвования помогают нам улучшать приложение и добавлять новые функции.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ИСПРАВЛЕНО: Используем локальный drawable-ресурс для QR-кода
                Image(
                    painter = painterResource(id = R.drawable.placeholder_qr_code),
                    contentDescription = "QR-код для донатов",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = "Отсканируйте QR-код для поддержки проекта", // Изменен текст
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ИСПРАВЛЕНО: Удалена кнопка для перехода по ссылке доната

                Spacer(modifier = Modifier.height(32.dp))

                // Дополнительная информация
                Text(
                    text = stringResource(id = R.string.help_info),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewHelpScreen() {
    WeatherAppTheme {
        HelpScreen(navController = rememberNavController())
    }
}
