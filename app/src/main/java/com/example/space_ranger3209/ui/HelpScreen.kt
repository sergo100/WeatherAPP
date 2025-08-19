package com.example.space_ranger3209.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.space_ranger3209.weatherapp.R // <-- правильный импорт R

// Если у вас есть QR-код или другое изображение
// import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Помощь проекту") },
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
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Если вам нравится приложение и вы хотите помочь проекту, вы можете сделать пожертвование:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Пример QR-кода (если есть ресурс QR-кода)
            // val context = LocalContext.current
            // val qrResId = context.resources.getIdentifier("qr_code_image", "drawable", context.packageName)
            // if (qrResId != 0) {
            //     Image(
            //         painter = painterResource(id = qrResId),
            //         contentDescription = "QR-код для пожертвования",
            //         modifier = Modifier.size(200.dp)
            //     )
            //     Spacer(modifier = Modifier.height(16.dp))
            //     Text(
            //         text = "Отсканируйте QR-код",
            //         style = MaterialTheme.typography.bodyMedium
            //     )
            // } else {
            //     Text(
            //         text = "QR-код временно недоступен.",
            //         style = MaterialTheme.typography.bodyMedium
            //     )
            // }

            Text(
                text = "Благодарим за вашу поддержку!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
