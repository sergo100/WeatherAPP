package com.example.space_ranger3209.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О приложении") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "WeatherApp — приложение для просмотра погоды.\nВерсия: 1.9.6",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Автор: Сергей Сергиенко",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Email: sergo100@yandex.ru",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Telegram: @space_ranger3209",
                style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable {
                    val telegramIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://t.me/space_ranger3209")
                    )
                    context.startActivity(telegramIntent)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                val privacyIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://docs.google.com/document/d/1oLSwIfuKiCcQQaPQL8nj6nCA2PqtZ88T_o3wM8nZmfQ/edit?usp=sharing")
                )
                context.startActivity(privacyIntent)
            }) {
                Text("Политика конфиденциальности")
            }
        }
    }
}
