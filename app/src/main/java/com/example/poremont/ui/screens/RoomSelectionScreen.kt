package com.example.poremont.ui.screens

<<<<<<< HEAD
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.poremont.data.PreferencesManager

@Composable
fun RoomSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val rooms = remember {
        listOf(
            "Жилая комната",
            "Кухня",
            "Ванная/туалет",
            "Входная группа/коридор/гардероб"
        )
    }
    var selectedRooms by remember { mutableStateOf<Set<String>>(emptySet()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
    ) {
        RoomSelectionHeader(
            onBack = { navController.popBackStack() },
            onReference = { navController.navigate("reference") }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(rooms) { room ->
                RoomOptionCard(
                    roomName = room,
                    checked = room in selectedRooms,
                    onClick = {
                        selectedRooms = if (room in selectedRooms) {
                            selectedRooms - room
                        } else {
                            selectedRooms + room
                        }
                    }
                )
            }
        }

        Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
            Button(
                onClick = {
                    prefs.saveRooms(selectedRooms.toList())
                    navController.navigate("dashboard")
                },
                enabled = selectedRooms.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(58.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A16E8),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF2B2D3A),
                    disabledContentColor = Color(0xFF8E91A3)
                )
            ) {
                Text(
                    text = "Создать ремонт (${selectedRooms.size})",
                    color = if (selectedRooms.isNotEmpty()) Color.White else Color(0xFF8E91A3),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RoomSelectionHeader(onBack: () -> Unit, onReference: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF4A16E8), Color(0xFF7048C7))
                )
            )
            .padding(horizontal = 24.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(56.dp)
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxSize()) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.Black)
=======
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
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
                }
            }
        }

<<<<<<< HEAD
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 78.dp)
        ) {
            Text(
                text = "Выбор комнат",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Отметьте комнаты для ремонта",
                color = Color.White.copy(alpha = 0.86f),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        IconButton(
            onClick = onReference,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(56.dp)
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxSize()) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "📖", fontSize = 26.sp)
                }
            }
        }
    }
}

@Composable
private fun RoomOptionCard(roomName: String, checked: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, if (checked) Color(0xFF4A16E8) else Color(0xFFE1E4EA)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (checked) Color(0xFF5B20E8) else Color(0xFFF3F4F7)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⌂", fontSize = 28.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.size(18.dp))
            Text(
                text = roomName,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF202633),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Checkbox(checked = checked, onCheckedChange = { onClick() })
        }
    }
}
=======
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
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
