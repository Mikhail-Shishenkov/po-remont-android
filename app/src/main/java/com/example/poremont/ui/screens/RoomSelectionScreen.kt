package com.example.poremont.ui.screens

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
fun RoomSelectionScreen(navController: NavController) {
    val rooms = listOf("Гостиная", "Спальня", "Кухня", "Ванная", "Туалет", "Коридор", "Балкон")
    var selectedRooms by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Выбор комнат", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(rooms) { room ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = room in selectedRooms,
                        onCheckedChange = {
                            selectedRooms = if (room in selectedRooms) selectedRooms - room else selectedRooms + room
                        }
                    )
                    Text(room, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("dashboard") },
            enabled = selectedRooms.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Создать ремонт (${selectedRooms.size})")
        }
        Button(onClick = { navController.navigate("reference") }, modifier = Modifier.fillMaxWidth()) { Text("Справочник материалов") }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Назад") }
    }
}