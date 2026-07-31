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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.widget.Toast
import coil.compose.AsyncImage
import com.example.poremont.data.PreferencesManager
import com.example.poremont.util.PdfReportGenerator
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DefectsListScreen(navController: NavController, roomParam: String) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val roomName = remember(roomParam) {
        URLDecoder.decode(roomParam, StandardCharsets.UTF_8.toString())
    }
    val isAllRooms = roomName == "all"
    val defects = if (isAllRooms) prefs.loadDefects() else prefs.loadDefects(roomName)
    val activeDefectsCount = defects.count { it.status == PreferencesManager.STATUS_DEFECT }
    val fixedDefectsCount = defects.count { it.status == PreferencesManager.STATUS_FIXED }
    val skippedCount = defects.count { it.status == PreferencesManager.STATUS_SKIPPED }
    val titleRoom = if (isAllRooms) "Все комнаты" else roomName

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
    ) {
        DefectsHeader(
            title = "Список дефектов",
            subtitle = titleRoom,
            onBack = { navController.popBackStack() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StatisticsCard(
                    active = activeDefectsCount,
                    fixed = fixedDefectsCount,
                    skipped = skippedCount
                )
            }

            if (defects.isEmpty()) {
                item { EmptyDefectsCard() }
            } else {
                items(defects) { defect ->
                    DefectCard(
                        defect = defect,
                        onClick = {
                            val encodedRoom = URLEncoder.encode(defect.roomName, StandardCharsets.UTF_8.toString())
                            val encodedStage = URLEncoder.encode(defect.stageName, StandardCharsets.UTF_8.toString())
                            navController.navigate("defect_edit/$encodedRoom/$encodedStage/${defect.questionIndex}")
                        }
                    )
                }
            }
        }

        Surface(color = Color.White, shadowElevation = 4.dp) {
            Button(
                onClick = {
                    val result = PdfReportGenerator.generateDefectsReport(
                        context = context,
                        defects = defects,
                        roomTitle = titleRoom
                    )

                    Toast.makeText(
                        context,
                        result.fold(
                            onSuccess = { "Отчёт сохранён: $it" },
                            onFailure = { "Не удалось сформировать отчёт: ${it.message.orEmpty()}" }
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                },
                enabled = defects.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A16E8),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFC8C9D4),
                    disabledContentColor = Color.White
                )
            ) {
                Text("↧  Выгрузить отчет в PDF", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DefectsHeader(title: String, subtitle: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF4A16E8), Color(0xFF7048C7))
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StatisticsCard(active: Int, fixed: Int, skipped: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Статистика",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF202633),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox(active.toString(), "Дефекты", Color(0xFFFFE0E0), Color(0xFFC01822), Modifier.weight(1f))
                StatBox(fixed.toString(), "Исправлено", Color(0xFFD9F8EA), Color(0xFF10C987), Modifier.weight(1f))
                StatBox(skipped.toString(), "Пропущено", Color(0xFFFFF2CC), Color(0xFFFF9800), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(86.dp)
            .background(bg, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = value,
                color = fg,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                maxLines = 1
            )
            Text(
                text = label,
                color = fg,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyDefectsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9F8EA))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Дефекты не обнаружены",
                color = Color(0xFF10C987),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun DefectCard(
    defect: PreferencesManager.DefectRecord,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = defect.title,
                    color = Color(0xFF202633),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (defect.status == PreferencesManager.STATUS_FIXED) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(defect.status)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(defect.stageName, color = Color(0xFF7A8190), style = MaterialTheme.typography.bodyMedium)

            if (defect.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(defect.description, color = Color(0xFF7A8190), style = MaterialTheme.typography.bodyMedium)
            }

            val realPhotos = defect.photoUris.take(2)
            if (realPhotos.isNotEmpty() || defect.photoCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (realPhotos.isNotEmpty()) {
                        realPhotos.forEach { photoUri ->
                            SmallPhotoPreview(photoUri)
                        }
                    } else {
                        repeat(defect.photoCount.coerceIn(0, 2)) {
                            SmallPhotoPlaceholder()
                        }
=======
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.poremont.data.entity.Defect

@Composable
fun DefectsListScreen(navController: NavController, roomId: Int) {
    val defects = listOf(
        Defect(1, 1, "Трещина на стене", "Небольшая трещина", "Дефект"),
        Defect(2, 2, "Неровная покраска", "", "Исправлено"),
        Defect(3, 3, "Пятно на потолке", "Требует перекраски", "Пропущен")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Список дефектов комнаты #$roomId", style = MaterialTheme.typography.headlineMedium)
        Text("Всего: ${defects.size} | Дефект: ${defects.count { it.status == "Дефект" }}", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(defects) { defect ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { navController.navigate("defect_edit/${defect.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(defect.title, style = MaterialTheme.typography.titleMedium)
                        if (defect.description.isNotBlank()) Text(defect.description, style = MaterialTheme.typography.bodySmall)
                        val statusColor = when (defect.status) {
                            "Дефект" -> Color.Red
                            "Исправлено" -> Color.Green
                            else -> Color.Yellow
                        }
                        Text("Статус: ${defect.status}", color = statusColor, style = MaterialTheme.typography.labelMedium)
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
                    }
                }
            }
        }
<<<<<<< HEAD
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (text, fg, bg) = when (status) {
        PreferencesManager.STATUS_FIXED -> Triple("Исправлено", Color(0xFF087D55), Color(0xFFD9F8EA))
        PreferencesManager.STATUS_SKIPPED -> Triple("Пропущено", Color(0xFFFF9800), Color(0xFFFFF2CC))
        else -> Triple("Дефект", Color(0xFFC01822), Color(0xFFFFE0E0))
    }

    Text(
        text = text,
        color = fg,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun SmallPhotoPreview(uri: String) {
    AsyncImage(
        model = uri,
        contentDescription = "Фото дефекта",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(width = 112.dp, height = 82.dp)
            .background(Color(0xFFE2E2E2), RoundedCornerShape(6.dp))
    )
}

@Composable
private fun SmallPhotoPlaceholder() {
    Box(
        modifier = Modifier
            .size(width = 112.dp, height = 82.dp)
            .background(Color(0xFFE2E2E2), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("400 × 300", color = Color(0xFF9D9D9D), fontWeight = FontWeight.Bold)
    }
}
=======

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Generate PDF */ }, modifier = Modifier.fillMaxWidth()) { Text("Выгрузить отчет в PDF") }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Назад") }
    }
}
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
