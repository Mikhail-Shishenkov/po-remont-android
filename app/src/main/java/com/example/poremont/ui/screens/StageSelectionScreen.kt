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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.poremont.data.PreferencesManager
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun StageSelectionScreen(navController: NavController, roomParam: String) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val roomName = remember(roomParam) {
        URLDecoder.decode(roomParam, StandardCharsets.UTF_8.toString())
    }
    val stageOptions = remember(roomName) { stageOptionsForRoom(roomName) }
    val savedStages = remember(roomName) { prefs.loadStages()[roomName].orEmpty().toSet() }
    var selectedStages by remember { mutableStateOf(savedStages) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF4A16E8), Color(0xFF7048C7))
                        )
                    )
                    .padding(horizontal = 24.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(52.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = Color.Black
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 72.dp)
                ) {
                    Text(
                        text = roomName,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Выберите этапы ремонта",
                        color = Color.White.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(
                    onClick = { navController.navigate("reference") },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(52.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "📖", fontSize = 24.sp)
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Button(
                    onClick = {
                        prefs.saveStagesForRoom(roomName, selectedStages.toList())
                        val encodedRoom = URLEncoder.encode(
                            roomName,
                            StandardCharsets.UTF_8.toString()
                        )
                        navController.navigate("room_stages/$encodedRoom") {
                            popUpTo("dashboard") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    enabled = selectedStages.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(58.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Сохранить этапы (${selectedStages.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(stageOptions) { stage ->
                StageOptionCard(
                    name = stage,
                    checked = stage in selectedStages,
                    onClick = {
                        selectedStages =
                            if (stage in selectedStages) selectedStages - stage
                            else selectedStages + stage
                    }
                )
            }
        }
    }
}

private fun stageOptionsForRoom(room: String): List<String> = when (room) {
    "Жилая комната" -> listOf("Демонтаж", "Покраска стен", "Укладка линолеума", "Установка натяжных потолков", "Установка плинтусов")
    "Кухня" -> listOf("Демонтаж", "Стены", "Пол", "Электрика", "Вентиляция", "Освещение")
    "Ванная/туалет" -> listOf("Демонтаж", "Стены", "Пол", "Сантехника", "Электрика", "Освещение")
    "Входная группа/коридор/гардероб" -> listOf("Демонтаж", "Стены", "Пол", "Свет", "Двери")
    else -> listOf("Демонтаж", "Стены", "Пол")
}

@Composable
private fun StageOptionCard(name: String, checked: Boolean, onClick: () -> Unit) {
    val borderColor = if (checked) Color(0xFF4A16E8) else Color(0xFFE1E4EA)
    val backgroundColor = if (checked) Color(0xFFF2EEFF) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = if (checked) Color(0xFF5B20E8) else Color(0xFFF3F4F7),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⌂", fontSize = 26.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF202633),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = checked,
                onCheckedChange = { onClick() }
            )
        }
    }
}
=======
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
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
