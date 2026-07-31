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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.poremont.data.PreferencesManager
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ChecklistScreen(navController: NavController, roomParam: String, stageParam: String) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val roomName = remember(roomParam) {
        URLDecoder.decode(roomParam, StandardCharsets.UTF_8.toString())
    }
    val stageName = remember(stageParam) {
        URLDecoder.decode(stageParam, StandardCharsets.UTF_8.toString())
    }
    val questions = remember(stageName) { checklistQuestionsForStage(stageName) }
    val encodedRoom = remember(roomName) { URLEncoder.encode(roomName, StandardCharsets.UTF_8.toString()) }

    val statuses = remember(roomName, stageName) {
        mutableStateMapOf<Int, CheckStatus>().apply {
            prefs.loadChecklistStatuses(roomName, stageName).forEach { (index, value) ->
                this[index] = CheckStatus.fromStorage(value)
            }
        }
    }

    var selectedQuestionIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
    ) {
        ChecklistHeader(
            title = "Чек-лист проверки",
            subtitle = stageName,
            onBack = {
                navController.navigate("room_stages/$encodedRoom") {
                    launchSingleTop = true
                }
            },
            onReference = { navController.navigate("reference") }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HowToCheckCard(stageName)
            }

            items(questions.indices.toList()) { index ->
                val status = statuses[index] ?: CheckStatus.NotChecked
                ChecklistQuestionCard(
                    question = questions[index],
                    status = status,
                    onAction = {
                        val hasDefectRecord = prefs.loadDefect(roomName, stageName, index) != null
                        if (status == CheckStatus.Defect && hasDefectRecord) {
                            val encodedStage = URLEncoder.encode(stageName, StandardCharsets.UTF_8.toString())
                            navController.navigate("defect_edit/$encodedRoom/$encodedStage/$index")
                        } else {
                            selectedQuestionIndex = index
                        }
                    },
                    onEdit = {
                        val hasDefectRecord = prefs.loadDefect(roomName, stageName, index) != null
                        if (hasDefectRecord) {
                            val encodedStage = URLEncoder.encode(stageName, StandardCharsets.UTF_8.toString())
                            navController.navigate("defect_edit/$encodedRoom/$encodedStage/$index")
                        } else {
                            selectedQuestionIndex = index
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        Button(
            onClick = { navController.navigate("defects_list/$encodedRoom") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Перейти к списку дефектов", fontWeight = FontWeight.Bold)
        }
    }

    val questionIndex = selectedQuestionIndex
    if (questionIndex != null) {
        StatusDialog(
            onDismiss = { selectedQuestionIndex = null },
            onGood = {
                statuses[questionIndex] = CheckStatus.Good
                prefs.saveChecklistStatus(roomName, stageName, questionIndex, CheckStatus.Good.storageValue)
                selectedQuestionIndex = null
            },
            onDefect = {
                selectedQuestionIndex = null
                val encodedStage = URLEncoder.encode(stageName, StandardCharsets.UTF_8.toString())
                val hasDefectRecord = prefs.loadDefect(roomName, stageName, questionIndex) != null
                if (hasDefectRecord) {
                    navController.navigate("defect_edit/$encodedRoom/$encodedStage/$questionIndex")
                } else {
                    navController.navigate("defect_create/$encodedRoom/$encodedStage/$questionIndex")
                }
            },
            onSkipped = {
                statuses[questionIndex] = CheckStatus.Skipped
                prefs.saveChecklistStatus(roomName, stageName, questionIndex, CheckStatus.Skipped.storageValue)
                selectedQuestionIndex = null
            }
        )
    }
}

@Composable
private fun ChecklistHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onReference: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF4A16E8), Color(0xFF7048C7))
                )
            )
            .padding(horizontal = 20.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(44.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.Black)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 62.dp)
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        IconButton(
            onClick = onReference,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(44.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📖", fontSize = 22.sp)
            }
        }
    }
}

@Composable
private fun HowToCheckCard(stageName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Как проверять:",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202633),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = howToCheckText(stageName),
                color = Color(0xFF7A8190),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ChecklistQuestionCard(
    question: String,
    status: CheckStatus,
    onAction: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = question,
                    color = Color(0xFF202633),
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (status == CheckStatus.Fixed) TextDecoration.LineThrough else TextDecoration.None,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (status != CheckStatus.NotChecked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = status.label,
                        color = status.color,
                        modifier = Modifier
                            .background(status.background, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (status == CheckStatus.Defect) "Редактировать дефект →" else "Редактировать →",
                        color = Color(0xFF4A16E8),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clickable { onEdit() }
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(status.buttonColor, RoundedCornerShape(8.dp))
                    .clickable { onAction() },
                contentAlignment = Alignment.Center
            ) {
                Text(status.iconText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

@Composable
private fun StatusDialog(
    onDismiss: () -> Unit,
    onGood: () -> Unit,
    onDefect: () -> Unit,
    onSkipped: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите статус") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onGood) {
                    Text("Все хорошо", color = Color(0xFF10C987), fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDefect) {
                    Text("Создать дефект", color = Color(0xFFFF1E1E), fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onSkipped) {
                    Text("Пропустить проверку", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

private enum class CheckStatus(
    val label: String,
    val iconText: String,
    val color: Color,
    val background: Color,
    val buttonColor: Color,
    val storageValue: String
) {
    NotChecked(
        label = "",
        iconText = "+",
        color = Color(0xFF4A16E8),
        background = Color.Transparent,
        buttonColor = Color(0xFF4A16E8),
        storageValue = PreferencesManager.STATUS_NOT_CHECKED
    ),
    Good(
        label = "Все хорошо",
        iconText = "✓",
        color = Color(0xFF10C987),
        background = Color(0xFFD9F8EA),
        buttonColor = Color(0xFF10C987),
        storageValue = PreferencesManager.STATUS_GOOD
    ),
    Defect(
        label = "Дефект",
        iconText = "!",
        color = Color(0xFFFF1E1E),
        background = Color(0xFFFFE0E0),
        buttonColor = Color(0xFFFF1E1E),
        storageValue = PreferencesManager.STATUS_DEFECT
    ),
    Skipped(
        label = "Пропущено",
        iconText = "!",
        color = Color(0xFFFF9800),
        background = Color(0xFFFFF2CC),
        buttonColor = Color(0xFFFF9800),
        storageValue = PreferencesManager.STATUS_SKIPPED
    ),
    Fixed(
        label = "Исправлено",
        iconText = "✓",
        color = Color(0xFF10C987),
        background = Color(0xFFD9F8EA),
        buttonColor = Color(0xFF10C987),
        storageValue = PreferencesManager.STATUS_FIXED
    );

    companion object {
        fun fromStorage(value: String): CheckStatus {
            return entries.firstOrNull { it.storageValue == value } ?: NotChecked
        }
    }
}

fun checklistQuestionsForStage(stageName: String): List<String> = when (stageName) {
    "Демонтаж" -> listOf(
        "Вынесен ли строительный мусор?",
        "Очищены ли поверхности после демонтажа?",
        "Нет ли повреждений коммуникаций?"
    )
    "Покраска стен", "Стены" -> listOf(
        "Есть ли трещины (паутинка или глубокие) при боковом освещении?",
        "Идеально ли ровные внутренние и внешние углы?",
        "Есть ли просветы основания (темные пятна сквозь краску)?",
        "Есть ли подтеки краски у плинтусов или вокруг розеток?",
        "Есть ли «рыбий глаз» или бугорки от плохо процеженной шпаклевки?",
        "Отваливается ли краска при проведении скотч-теста?",
        "Есть ли зазоры между стеной и рамой окна/двери?",
        "Видны ли переходы слоев или полосы от валика?"
    )
    "Потолок" -> listOf(
        "Есть ли трещины или пятна на потолке?",
        "Ровно ли выполнена отделка потолка?",
        "Нет ли следов протечек?"
    )
    "Пол", "Полы" -> listOf(
        "Ровная ли поверхность пола?",
        "Нет ли скрипов или провалов?",
        "Качественно ли выполнено покрытие?"
    )
    "Электрика" -> listOf(
        "Работают ли розетки и выключатели?",
        "Есть ли заземление там, где оно предусмотрено?",
        "Не греются ли розетки при нагрузке?"
    )
    "Отопление" -> listOf(
        "Работают ли радиаторы?",
        "Нет ли протечек на соединениях?",
        "Можно ли регулировать температуру?"
    )
    "Освещение", "Свет" -> listOf(
        "Работают ли все светильники?",
        "Нет ли мерцания?",
        "Удобно ли расположены выключатели?"
    )
    "Вентиляция" -> listOf(
        "Есть ли тяга в вентиляции?",
        "Работает ли вытяжка?",
        "Нет ли постороннего запаха?"
    )
    "Сантехника" -> listOf(
        "Нет ли протечек?",
        "Нормальное ли давление воды?",
        "Работают ли сливы?"
    )
    else -> listOf(
        "Проверка выполнена визуально?",
        "Нет ли видимых дефектов?",
        "Этап готов к следующей работе?"
    )
}

private fun howToCheckText(stageName: String): String = when (stageName) {
    "Покраска стен", "Стены" -> "Включи яркую лампу-переноску и свети вдоль стены сбоку. Так проявятся все микронеровности. Проведи рукой по всей поверхности. Постучи костяшками пальцев."
    "Демонтаж" -> "Проверь, что мусор вынесен, поверхности очищены, коммуникации не повреждены, а помещение готово к следующему этапу работ."
    else -> "Осмотрите результат работ при хорошем освещении. Проверьте ровность, отсутствие видимых дефектов и готовность этапа к дальнейшим работам."
}
