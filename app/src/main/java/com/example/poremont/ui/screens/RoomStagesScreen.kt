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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun RoomStagesScreen(navController: NavController, roomParam: String) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val roomName = remember(roomParam) {
        URLDecoder.decode(roomParam, StandardCharsets.UTF_8.toString())
    }
    val stages = prefs.loadStages()[roomName].orEmpty()
    val roomProgress = calculateRoomProgress(prefs, roomName, stages)
    val encodedRoom = URLEncoder.encode(roomName, StandardCharsets.UTF_8.toString())

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Button(
                    onClick = { navController.navigate("defects_list/$encodedRoom") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(58.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Перейти к списку дефектов",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))
        ) {
            RoomStagesHeader(
                roomName = roomName,
                roomProgress = roomProgress,
                onBack = { navController.navigate("dashboard") { launchSingleTop = true } },
                onReference = { navController.navigate("reference") }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(stages) { stage ->
                    val encodedStage = URLEncoder.encode(stage, StandardCharsets.UTF_8.toString())
                    val tasksCount = checklistQuestionsForStage(stage).size
                    val stageProgress = calculateStageProgress(prefs, roomName, stage)
                    val defectsCount = countStageDefects(prefs, roomName, stage)

                    StageProgressCard(
                        stageName = stage,
                        tasksCount = tasksCount,
                        progress = stageProgress,
                        defectsCount = defectsCount,
                        onClick = {
                            navController.navigate("checklist/$encodedRoom/$encodedStage")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomStagesHeader(
    roomName: String,
    roomProgress: Float,
    onBack: () -> Unit,
    onReference: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
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
                .align(Alignment.TopStart)
                .padding(top = 36.dp)
                .size(52.dp)
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.fillMaxSize()) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.Black)
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 76.dp, top = 42.dp)
        ) {
            Text(
                text = roomName,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Этапы ремонта",
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        ReferenceButton(
            onClick = onReference,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 36.dp)
        )

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 28.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Прогресс", color = Color(0xFF7A8190), fontWeight = FontWeight.Bold)
                    Text(
                        "${(roomProgress * 100).toInt()}%",
                        color = Color(0xFF4A16E8),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = roomProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color(0xFF4A16E8),
                    trackColor = Color(0xFFEFEFF3)
                )
            }
        }
    }
}

@Composable
private fun ReferenceButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(52.dp)
    ) {
        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "📖", fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun StageProgressCard(
    stageName: String,
    tasksCount: Int,
    progress: Float,
    defectsCount: Int,
    onClick: () -> Unit
) {
    val completed = progress >= 1f && defectsCount == 0
    val accent = if (completed) Color(0xFF10C987) else Color(0xFF4A16E8)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (defectsCount > 0) 168.dp else 146.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (completed) Color(0xFF10C987) else Color(0xFFF3F4F7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓", color = Color.Black, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.size(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stageName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF202633)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$tasksCount задачи",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7A8190)
                    )
                }
                Text(text = "›", fontSize = 36.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Выполнено", color = Color(0xFF7A8190), fontWeight = FontWeight.Bold)
                Text("${(progress * 100).toInt()}%", color = accent, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = accent,
                trackColor = Color(0xFFEFEFF3)
            )

            if (defectsCount > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Дефектов = $defectsCount",
                    color = Color(0xFFFF1E1E),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun calculateStageProgress(prefs: PreferencesManager, roomName: String, stageName: String): Float {
    val questions = checklistQuestionsForStage(stageName)
    if (questions.isEmpty()) return 0f

    val statuses = prefs.loadChecklistStatuses(roomName, stageName)
    val done = questions.indices.count { index ->
        val status = statuses[index]
        status == PreferencesManager.STATUS_GOOD ||
            status == PreferencesManager.STATUS_SKIPPED ||
            status == PreferencesManager.STATUS_FIXED
    }

    return done.toFloat() / questions.size.toFloat()
}

private fun calculateRoomProgress(prefs: PreferencesManager, roomName: String, stages: List<String>): Float {
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

private fun countStageDefects(prefs: PreferencesManager, roomName: String, stageName: String): Int {
    return prefs.loadChecklistStatuses(roomName, stageName).values.count { it == PreferencesManager.STATUS_DEFECT }
}
