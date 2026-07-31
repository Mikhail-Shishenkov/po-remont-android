package com.example.poremont.ui.screens

<<<<<<< HEAD
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val hasActiveProject = prefs.hasActiveProject()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF4A16E8), Color(0xFF7048C7))
                    )
                )
                .padding(horizontal = 28.dp, vertical = 32.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = "Управление ремонтом",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Контроль этапов и дефектов",
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!hasActiveProject) {
                MainActionCard(
                    title = "Новый ремонт",
                    subtitle = "Создать новый проект",
                    iconText = "+",
                    highlighted = true,
                    onClick = { navController.navigate("room_selection") }
                )
            } else {
                MainActionCard(
                    title = "Продолжить ремонт",
                    subtitle = "Открыть текущий проект",
                    iconText = "✓",
                    highlighted = false,
                    onClick = { navController.navigate("dashboard") }
                )
            }

            MainActionCard(
                title = "Справочник",
                subtitle = "Полезная информация",
                iconText = "📖",
                highlighted = false,
                onClick = { navController.navigate("reference") }
            )

            if (hasActiveProject) {
                MainActionCard(
                    title = "Список дефектов",
                    subtitle = "Все обнаруженные проблемы",
                    iconText = "⚠️",
                    highlighted = false,
                    onClick = { navController.navigate("defects_list/all") }
                )
            }
        }
    }
}

@Composable
private fun MainActionCard(
    title: String,
    subtitle: String,
    iconText: String,
    highlighted: Boolean,
    onClick: () -> Unit
) {
    val cardColor = if (highlighted) Color(0xFF4A16E8) else Color.White
    val titleColor = if (highlighted) Color.White else Color(0xFF202633)
    val subtitleColor = if (highlighted) Color.White.copy(alpha = 0.82f) else Color(0xFF7A8190)
    val iconBoxColor = if (highlighted) Color.White else Color(0xFFF3F4F7)
    val iconColor = if (highlighted) Color.Black else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
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
                    .background(iconBoxColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    color = iconColor,
                    fontSize = if (iconText == "📖") 26.sp else 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.size(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = titleColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = subtitleColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "›",
                color = titleColor,
                fontSize = 40.sp
            )
        }
    }
}
=======
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ПО-Ремонт",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Проверка качества ремонта",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("room_selection") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Новый ремонт")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("reference") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Справочник материалов")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("dashboard") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Продолжить ремонт (демо)")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Приложение работает оффлайн. Данные сохраняются локально.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
