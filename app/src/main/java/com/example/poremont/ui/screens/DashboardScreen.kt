package com.example.poremont.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.poremont.data.entity.RoomEntity

@Composable
fun DashboardScreen(navController: NavController) {
    val rooms = listOf(
        RoomEntity(1, 1, "Гостиная", "гостиная", 45f, 2),
        RoomEntity(2, 1, "Кухня", "кухня", 70f, 0),
        RoomEntity(3, 1, "Спальня", "спальня", 20f, 1)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Ремонт квартиры", style = MaterialTheme.typography.headlineMedium)
        Text("Прогресс: 45%", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(rooms) { room ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { navController.navigate("stages_list/${room.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(room.name, style = MaterialTheme.typography.titleMedium)
                        Text("Этапов: ${room.progress.toInt()}% | Дефектов: ${room.defectsCount}")
                        LinearProgressIndicator(progress = room.progress / 100f, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("reference") }, modifier = Modifier.fillMaxWidth()) { Text("Справочник материалов") }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Назад на главную") }
    }
}