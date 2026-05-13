package com.example.poremont.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun StageSelectionScreen(navController: NavController, roomId: Int) {
    val stages = when (roomId) {
        1 -> listOf("Подготовка стен", "Покраска", "Укладка пола", "Электрика")
        2 -> listOf("Установка мебели", "Плитка", "Сантехника")
        else -> listOf("Подготовка", "Основной этап", "Финиш")
    }
    var selectedStages by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Выбор этапов ремонта", style = MaterialTheme.typography.headlineMedium)
        Text("Комната ID: $roomId", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(stages) { stage ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Checkbox(
                        checked = stage in selectedStages,
                        onCheckedChange = { selectedStages = if (stage in selectedStages) selectedStages - stage else selectedStages + stage }
                    )
                    Text(stage, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("stages_list/$roomId") },
            enabled = selectedStages.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Создать план ремонта") }
        Button(onClick = { navController.navigate("reference") }, modifier = Modifier.fillMaxWidth()) { Text("Справочник материалов") }
    }
}