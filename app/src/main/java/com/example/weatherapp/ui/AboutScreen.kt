package com.example.weatherapp.ui

// Удалены импорты, связанные с текстовой ссылкой, которые больше не нужны
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
import com.example.weatherapp.ui.theme.WeatherAppTheme
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

// Экран "Об авторе"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current // Получаем контекст для Intent
    // Ваша реальная ссылка на Политику конфиденциальности
    val privacyPolicyUrl = "https://docs.google.com/document/d/1oLSwIfuKiCcQQaPQL8nj6nCA2PqtZ88T_o3wM8nZmfQ/edit?usp=sharing"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Об авторе") },
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
                    text = "Автор: Сергей Сергиенко",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Email: sergo100@yandex.ru",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Telegram: @space_ranger",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // КНОПКА: Политика конфиденциальности (возвращено к Button)
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    Text("Политика конфиденциальности")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAboutScreen() {
    WeatherAppTheme {
        AboutScreen(navController = rememberNavController())
    }
}
