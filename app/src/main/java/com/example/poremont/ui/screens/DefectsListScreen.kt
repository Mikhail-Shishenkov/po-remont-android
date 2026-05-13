package com.example.poremont.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.poremont.data.entity.Defect

@Composable
fun DefectsListScreen(navController: NavController, roomId: Int) {
    val defects = listOf(
        Defect(1, 1, "Трещина на стене", "Небольшая трещина", "Дефект"),
        Defect(2, 2, "Неровная покраска", "", "Исправлено"),
        Defect(3, 3, "Пятно на потолке", "Требует перекраски", "Пропущен")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Список дефектов комнаты #$roomId", style = MaterialTheme.typography.headlineMedium)
        Text("Всего: ${defects.size} | Дефект: ${defects.count { it.status == "Дефект" }}", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(defects) { defect ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { navController.navigate("defect_edit/${defect.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(defect.title, style = MaterialTheme.typography.titleMedium)
                        if (defect.description.isNotBlank()) Text(defect.description, style = MaterialTheme.typography.bodySmall)
                        val statusColor = when (defect.status) {
                            "Дефект" -> Color.Red
                            "Исправлено" -> Color.Green
                            else -> Color.Yellow
                        }
                        Text("Статус: ${defect.status}", color = statusColor, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Generate PDF */ }, modifier = Modifier.fillMaxWidth()) { Text("Выгрузить отчет в PDF") }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Назад") }
    }
}