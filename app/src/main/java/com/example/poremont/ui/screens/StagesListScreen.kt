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
import com.example.poremont.data.entity.Stage

@Composable
fun StagesListScreen(navController: NavController, roomId: Int) {
    val stages = listOf(
        Stage(1, roomId, "Подготовка стен", 100f, true, 0),
        Stage(2, roomId, "Покраска", 60f, false, 2),
        Stage(3, roomId, "Укладка пола", 30f, false, 1)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Этапы ремонта комнаты", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(stages) { stage ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { navController.navigate("checklist/${stage.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stage.name, style = MaterialTheme.typography.titleMedium)
                        Text("Прогресс: ${stage.progress.toInt()}% | Дефектов: ${stage.defectsCount}")
                        LinearProgressIndicator(progress = stage.progress / 100f, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                        if (stage.isCompleted) Text("✓ Завершено", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("defects_list/$roomId") }, modifier = Modifier.fillMaxWidth()) { Text("Перейти к списку дефектов") }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Назад") }
    }
}