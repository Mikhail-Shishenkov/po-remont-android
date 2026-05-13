package com.example.poremont.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ПО-Ремонт",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Проверка качества ремонта",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("room_selection") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Новый ремонт")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("reference") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Справочник материалов")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("dashboard") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Продолжить ремонт (демо)")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Приложение работает оффлайн. Данные сохраняются локально.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}