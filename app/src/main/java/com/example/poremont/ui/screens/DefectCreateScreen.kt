package com.example.poremont.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DefectCreateScreen(navController: NavController, stageId: Int) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Дефект") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Создание дефекта", style = MaterialTheme.typography.headlineMedium)
        Text("Этап ID: $stageId", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название дефекта *") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Статус:")
        Row {
            listOf("Дефект", "Исправлено", "Пропущен").forEach { s ->
                RadioButton(selected = status == s, onClick = { status = s })
                Text(s)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { if (title.isNotBlank()) navController.popBackStack() },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Сохранить дефект") }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Отмена") }
    }
}