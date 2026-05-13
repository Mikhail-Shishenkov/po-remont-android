package com.example.poremont.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.poremont.data.entity.ChecklistItem

@Composable
fun ChecklistScreen(navController: NavController, stageId: Int) {
    var items by remember {
        mutableStateOf(
            listOf(
                ChecklistItem(1, stageId, "Стены ровные?", "Все хорошо"),
                ChecklistItem(2, stageId, "Нет трещин?", "Не проверен"),
                ChecklistItem(3, stageId, "Покраска равномерная?", "Дефект")
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Чек-лист этапа", style = MaterialTheme.typography.headlineMedium)
        Text("Этап ID: $stageId", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(items) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.question)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(onClick = { /* Update to Все хорошо */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("✓ Хорошо") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { navController.navigate("defect_create/$stageId") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("✗ Дефект") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { /* Skip */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("Пропустить") }
                        }
                        Text("Статус: ${item.status}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Назад к этапам") }
    }
}