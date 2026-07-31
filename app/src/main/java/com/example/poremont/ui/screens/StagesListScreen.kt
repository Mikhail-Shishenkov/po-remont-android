package com.example.poremont.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.poremont.data.PreferencesManager
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Screen displaying the list of selected stages for a given room along with
 * a simplified checklist for each stage.  Each task can be marked as
 * "ok", "skipped" or "defect" by tapping on the coloured indicator.  The
 * progress for a stage is calculated as the proportion of tasks that are
 * either OK or skipped (i.e. non‑defect and checked).  A room may have
 * multiple stages selected during the stage selection step.  The room name
 * is passed via the [roomNameParam] argument and is URL‑decoded here to
 * support names containing characters such as slashes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StagesListScreen(navController: NavController, roomNameParam: String) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    // Decode the room name from the navigation argument
    val roomName = remember(roomNameParam) {
        URLDecoder.decode(roomNameParam, StandardCharsets.UTF_8.toString())
    }
    // Load the project from preferences and extract the list of stages for this room
    val project = prefs.loadProject()
    val selectedStages: List<String> = project?.second?.get(roomName) ?: emptyList()

    // Provide a sample checklist for each stage.  The keys must match the stage names
    // defined in StageSelectionScreen.  If a stage is not found, the tasks list
    // will be empty.
    val checklistMap: Map<String, List<String>> = mapOf(
        "Стены" to listOf(
            "Проверьте ровность поверхности",
            "Проверьте отсутствие трещин и сколов",
            "Осмотрите качество покраски"
        ),
        "Потолок" to listOf(
            "Проверьте ровность и отсутствие трещин",
            "Проверьте качество отделки",
            "Убедитесь в отсутствии протечек"
        ),
        "Пол" to listOf(
            "Проверить ровность пола",
            "Проверить отсутствие скрипов",
            "Осмотреть качество покрытия"
        ),
        "Электрика" to listOf(
            "Проверить работу розеток и выключателей",
            "Проверить наличие заземления",
            "Проверить работу светильников"
        ),
        "Отопление" to listOf(
            "Проверить работу радиаторов",
            "Проверить герметичность соединений",
            "Проверить регулировку температуры"
        ),
        "Освещение" to listOf(
            "Проверить работу ламп",
            "Проверить отсутствие мерцания",
            "Убедиться в правильном расположении светильников"
        ),
        "Вентиляция" to listOf(
            "Проверить тягу вентиляции",
            "Очистить фильтры",
            "Проверить работу вытяжки"
        ),
        "Сантехника" to listOf(
            "Проверить давление воды",
            "Проверить отсутствие протечек",
            "Проверить работу сливов"
        ),
        "Свет" to listOf(
            "Проверить освещение коридора",
            "Проверить работу подсветки",
            "Убедиться, что выключатели работают"
        ),
        "Двери" to listOf(
            "Проверить открывание и закрывание",
            "Проверить отсутствие люфтов",
            "Проверить работу замков"
        )
    )

    // Keep a mutable state for task statuses.  The key is the stage name, the value is
    // a mutable list of statuses corresponding to the tasks in the same order as
    // the checklist.  Possible statuses: "not_checked", "ok", "skipped", "defect".
    val taskStatuses = remember { mutableStateMapOf<String, MutableList<String>>() }
    // Initialize statuses for each selected stage on first composition
    LaunchedEffect(selectedStages) {
        selectedStages.forEach { stage ->
            val tasks = checklistMap[stage] ?: emptyList()
            if (taskStatuses[stage] == null) {
                taskStatuses[stage] = MutableList(tasks.size) { "not_checked" }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(roomName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate("reference") }) {
                        Text("Справочник")
                    }
                }
            )
        },
        bottomBar = {
            // Show a button to navigate to the defect list if any tasks are marked as defects
            val hasDefects by remember {
                derivedStateOf {
                    selectedStages.any { stage ->
                        taskStatuses[stage]?.any { it == "defect" } == true
                    }
                }
            }
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            // Navigate to the list of defects for this room. We encode the room name
                            // so that slashes and spaces are preserved.
                            val encoded = java.net.URLEncoder.encode(roomName, StandardCharsets.UTF_8.toString())
                            navController.navigate("defects_list/$encoded")
                        },
                        enabled = hasDefects
                    ) {
                        Text("Список дефектов")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (selectedStages.isEmpty()) {
                Text(
                    "Этапы для комнаты не выбраны.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn {
                    items(selectedStages) { stage ->
                        val tasks = checklistMap[stage] ?: emptyList()
                        val statuses = taskStatuses[stage] ?: MutableList(tasks.size) { "not_checked" }
                        // Compute progress: tasks marked ok or skipped count towards completion
                        val done = statuses.count { it == "ok" || it == "skipped" }
                        val total = tasks.size
                        val progress = if (total == 0) 0f else done.toFloat() / total.toFloat()
                        val defectCount = statuses.count { it == "defect" }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(stage, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
                                Text(
                                    "Прогресс: ${(progress * 100).toInt()}%", 
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (defectCount > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Дефектов: $defectCount",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                // Render tasks
                                tasks.forEachIndexed { index, task ->
                                    val status = statuses[index]
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val color = when (status) {
                                            "ok" -> MaterialTheme.colorScheme.primary
                                            "skipped" -> MaterialTheme.colorScheme.tertiary
                                            "defect" -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.outline
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(color, shape = MaterialTheme.shapes.small)
                                                .clickable {
                                                    // Cycle through statuses: not_checked -> ok -> skipped -> defect -> ok
                                                    val newStatus = when (status) {
                                                        "not_checked" -> "ok"
                                                        "ok" -> "skipped"
                                                        "skipped" -> "defect"
                                                        "defect" -> "ok"
                                                        else -> "ok"
                                                    }
                                                    taskStatuses[stage]?.set(index, newStatus)
                                                }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(task)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}