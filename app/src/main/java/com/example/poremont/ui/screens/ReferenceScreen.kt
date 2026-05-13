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

@Composable
fun ReferenceScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val sections = listOf(
        "Напольные покрытия" to listOf("Линолеум (бытовой 21-23, полукоммерческий 31-34, коммерческий 41-43)", "Керамогранит (PEI I-V, R9-R13, водопоглощение ≤0.05%)", "Плитка (бикоттура, монокоттура, клинкер)"),
        "Стены" to listOf("Штукатурка + шпаклевка + покраска", "Обои (бумажные, флизелиновые, виниловые)", "Декоративная штукатурка (короед, венецианка)"),
        "Потолок" to listOf("Натяжной ПВХ/ткань", "Гипсокартонный", "Покраска"),
        "Электрика" to listOf("Розетки и выключатели", "Электрощиток (УЗО, автоматы)")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Справочник материалов", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, label = { Text("Поиск по справочнику...") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(sections) { (section, items) ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(section, style = MaterialTheme.typography.titleMedium)
                        items.forEach { item ->
                            if (searchQuery.isBlank() || item.contains(searchQuery, ignoreCase = true)) {
                                Text("• $item", modifier = Modifier.padding(start = 16.dp, top = 4.dp).clickable { /* Detail */ })
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Назад") }
    }
}