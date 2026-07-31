package com.example.poremont.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.poremont.data.PreferencesManager
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val rooms = prefs.loadRooms()
    val stagesByRoom = prefs.loadStages()

    var showFinishDialog by remember { mutableStateOf(false) }

    val allRoomsCompleted = rooms.isNotEmpty() && rooms.all { roomName ->
        val stages = stagesByRoom[roomName].orEmpty()
        stages.isNotEmpty() && calculateRoomProgress(prefs, roomName, stages) >= 1f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
    ) {
        DashboardHeader(
            onBack = {
                navController.navigate("main") {
                    launchSingleTop = true
                }
            },
            onReference = {
                navController.navigate("reference")
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(rooms) { roomName ->
                val stages = stagesByRoom[roomName].orEmpty()
                val progress = calculateRoomProgress(prefs, roomName, stages)
                val defectsCount = prefs.countDefectsForRoom(roomName)

                RoomCard(
                    roomName = roomName,
                    stagesCount = stages.size,
                    progress = progress,
                    defectsCount = defectsCount,
                    onClick = {
                        val encodedRoom = URLEncoder.encode(
                            roomName,
                            StandardCharsets.UTF_8.toString()
                        )

                        if (stages.isEmpty()) {
                            navController.navigate("stage_selection/$encodedRoom")
                        } else {
                            navController.navigate("room_stages/$encodedRoom")
                        }
                    }
                )
            }

            if (allRoomsCompleted) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { showFinishDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4A16E8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Завершить ремонт",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = {
                showFinishDialog = false
            },
            title = {
                Text(
                    text = "Завершить ремонт?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202633)
                )
            },
            text = {
                Text(
                    text = "Текущий ремонт будет завершён, а данные проекта будут очищены. После этого можно будет создать новый ремонт.",
                    color = Color(0xFF6F7685)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        prefs.clearProject()
                        showFinishDialog = false

                        navController.navigate("main") {
                            popUpTo("main") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A16E8),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Завершить",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showFinishDialog = false
                    }
                ) {
                    Text(
                        text = "Отмена",
                        color = Color(0xFF4A16E8),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

@Composable
private fun DashboardHeader(
    onBack: () -> Unit,
    onReference: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF4A16E8),
                        Color(0xFF7048C7)
                    )
                )
            )
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(56.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
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

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Ремонт квартиры",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Выберите комнату",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            ReferenceButton(onClick = onReference)
        }
    }
}

@Composable
private fun ReferenceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(56.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "📖",
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
private fun RoomCard(
    roomName: String,
    stagesCount: Int,
    progress: Float,
    defectsCount: Int,
    onClick: () -> Unit
) {
    val progressPercent = (progress * 100).toInt()
    val isCompleted = progress >= 1f

    val accentColor = if (isCompleted) {
        Color(0xFF10C283)
    } else {
        Color(0xFF4A16E8)
    }

    val iconBackgroundColor = if (isCompleted) {
        Color(0xFF10C283)
    } else {
        Color(0xFF5B20E8)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (defectsCount > 0) 176.dp else 154.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌂",
                        fontSize = 28.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = roomName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF202633),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Этапов = $stagesCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7A8190)
                    )
                }

                Text(
                    text = "›",
                    fontSize = 36.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Прогресс",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7A8190),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = accentColor,
                trackColor = Color(0xFFEFEFF3)
            )

            if (defectsCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Дефектов = $defectsCount",
                    color = Color(0xFFFF1E1E),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun calculateRoomProgress(
    prefs: PreferencesManager,
    roomName: String,
    stages: List<String>
): Float {
    var doneTasks = 0
    var totalTasks = 0

    stages.forEach { stage ->
        val questions = checklistQuestionsForStage(stage)
        val statuses = prefs.loadChecklistStatuses(roomName, stage)

        totalTasks += questions.size

        doneTasks += questions.indices.count { index ->
            val status = statuses[index]

            status == PreferencesManager.STATUS_GOOD ||
                    status == PreferencesManager.STATUS_SKIPPED ||
                    status == PreferencesManager.STATUS_FIXED
        }
    }

    if (totalTasks == 0) return 0f

    return doneTasks.toFloat() / totalTasks.toFloat()
}
