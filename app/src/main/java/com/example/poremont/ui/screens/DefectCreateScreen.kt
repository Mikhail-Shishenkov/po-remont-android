package com.example.poremont.ui.screens

<<<<<<< HEAD
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.core.content.ContextCompat
import com.example.poremont.data.PreferencesManager
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun DefectCreateScreen(
    navController: NavController,
    roomParam: String,
    stageParam: String,
    questionIndex: Int
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val roomName = remember(roomParam) {
        URLDecoder.decode(roomParam, StandardCharsets.UTF_8.toString())
    }
    val stageName = remember(stageParam) {
        URLDecoder.decode(stageParam, StandardCharsets.UTF_8.toString())
    }
    val question = remember(stageName, questionIndex) {
        checklistQuestionsForStage(stageName).getOrNull(questionIndex).orEmpty()
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null && photoUris.size < 2) {
            persistImagePermissionSafely(context, uri)
            photoUris = (photoUris + uri.toString()).distinct().take(2)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null && photoUris.size < 2) {
            saveCameraBitmapToInternalStorage(context, bitmap)?.let { uri ->
                photoUris = (photoUris + uri).distinct().take(2)
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && photoUris.size < 2) {
            cameraLauncher.launch(null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
    ) {
        DefectFormHeader(
            title = "Создание дефекта",
            onBack = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            StatusSelector(
                selectedStatus = PreferencesManager.STATUS_DEFECT,
                enabled = false,
                onStatusSelected = {}
            )

            Text(
                text = "Название дефекта",
                color = Color(0xFF737A89),
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Введите название дефекта") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = defectTextFieldColors()
            )

            Text(
                text = "Описание дефекта",
                color = Color(0xFF737A89),
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Введите описание дефекта") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(10.dp),
                colors = defectTextFieldColors()
            )

            Text(
                text = "Фотографии (макс. 2)",
                color = Color(0xFF737A89),
                style = MaterialTheme.typography.bodyMedium
            )
            PhotoPickerRow(
                photoUris = photoUris,
                onAdd = {
                    if (photoUris.size < 2) {
                        showPhotoSourceDialog = true
                    }
                },
                onRemove = { uri ->
                    photoUris = photoUris.filterNot { it == uri }
                }
            )
        }

        Surface(color = Color.White, shadowElevation = 4.dp) {
            Button(
                onClick = {
                    prefs.saveDefect(
                        PreferencesManager.DefectRecord(
                            roomName = roomName,
                            stageName = stageName,
                            questionIndex = questionIndex,
                            question = question,
                            title = title.trim(),
                            description = description.trim(),
                            status = PreferencesManager.STATUS_DEFECT,
                            photoCount = photoUris.size,
                            photoUris = photoUris
                        )
                    )
                    navController.popBackStack()
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A16E8),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFC8C9D4),
                    disabledContentColor = Color.White
                )
            ) {
                Text("Сохранить", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showPhotoSourceDialog) {
        PhotoSourceDialog(
            onDismiss = { showPhotoSourceDialog = false },
            onGallery = {
                showPhotoSourceDialog = false
                photoPickerLauncher.launch(arrayOf("image/*"))
            },
            onCamera = {
                showPhotoSourceDialog = false

                val hasCameraPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (hasCameraPermission) {
                    cameraLauncher.launch(null)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }
}

private fun persistImagePermissionSafely(context: android.content.Context, uri: Uri) {
    try {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (_: SecurityException) {
        // Некоторые поставщики файлов не дают persistable permission. Это не критично для текущей сессии.
    } catch (_: IllegalArgumentException) {
        // Uri может быть не document-uri. Оставляем без падения приложения.
    }
}

fun saveCameraBitmapToInternalStorage(context: android.content.Context, bitmap: Bitmap): String? {
    return try {
        val photosDir = File(context.filesDir, "defect_photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }

        val file = File(photosDir, "defect_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)
        }

        Uri.fromFile(file).toString()
    } catch (_: Exception) {
        null
    }
}

@Composable
fun PhotoSourceDialog(
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Добавить фото",
                color = Color(0xFF202633),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Выберите источник фотографии",
                color = Color(0xFF737A89)
            )
        },
        confirmButton = {
            Button(
                onClick = onCamera,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A16E8),
                    contentColor = Color.White
                )
            ) {
                Text("Камера")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onGallery) {
                    Text(
                        text = "Галерея",
                        color = Color(0xFF4A16E8),
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Отмена",
                        color = Color(0xFF737A89)
                    )
                }
            }
        }
    )
}

@Composable
fun DefectFormHeader(
    title: String,
    onBack: () -> Unit
) {
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

            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 28.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatusSelector(
    selectedStatus: String,
    enabled: Boolean,
    onStatusSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Статус дефекта",
                color = Color(0xFF737A89),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(
                    text = "Исправл.",
                    status = PreferencesManager.STATUS_FIXED,
                    selectedStatus = selectedStatus,
                    enabled = enabled,
                    onClick = onStatusSelected,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(
                    text = "Пропущ.",
                    status = PreferencesManager.STATUS_SKIPPED,
                    selectedStatus = selectedStatus,
                    enabled = enabled,
                    onClick = onStatusSelected,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(
                    text = "Дефект",
                    status = PreferencesManager.STATUS_DEFECT,
                    selectedStatus = selectedStatus,
                    enabled = enabled,
                    onClick = onStatusSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    status: String,
    selectedStatus: String,
    enabled: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedStatus == status
    val accent = when (status) {
        PreferencesManager.STATUS_FIXED -> Color(0xFF10C987)
        PreferencesManager.STATUS_SKIPPED -> Color(0xFFFF9800)
        else -> Color(0xFFE51E2A)
    }
    val bg = if (isSelected) accent.copy(alpha = 0.14f) else Color.White
    val border = if (isSelected) accent else Color(0xFFE1E4EA)
    val textColor = if (isSelected) accent else Color(0xFF737A89)

    OutlinedButton(
        onClick = { if (enabled) onClick(status) },
        enabled = enabled || isSelected,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = bg,
            contentColor = textColor,
            disabledContainerColor = bg,
            disabledContentColor = textColor
        ),
        border = BorderStroke(1.dp, border)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PhotoPickerRow(
    photoUris: List<String>,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        photoUris.take(2).forEach { uri ->
            PhotoPreview(
                uri = uri,
                onRemove = { onRemove(uri) }
            )
        }
        if (photoUris.size < 2) {
            AddPhotoPlaceholder(onClick = onAdd)
        }
    }
}

@Composable
private fun PhotoPreview(uri: String, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 132.dp, height = 106.dp)
            .background(Color(0xFFE2E2E2), RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Фото дефекта",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE2E2E2), RoundedCornerShape(8.dp))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(30.dp)
                .background(Color(0xFFE92828), RoundedCornerShape(50))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AddPhotoPlaceholder(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 132.dp, height = 106.dp)
            .background(Color(0xFFF8F9FB), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("+", color = Color(0xFF737A89), fontSize = 34.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Добавить фото", color = Color(0xFF737A89), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun defectTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF202633),
    unfocusedTextColor = Color(0xFF202633),
    disabledTextColor = Color(0xFF202633),
    cursorColor = Color(0xFF4A16E8),
    focusedBorderColor = Color(0xFF4A16E8),
    unfocusedBorderColor = Color(0xFFE1E4EA),
    focusedPlaceholderColor = Color(0xFF9AA1AD),
    unfocusedPlaceholderColor = Color(0xFF9AA1AD),
    focusedLabelColor = Color(0xFF737A89),
    unfocusedLabelColor = Color(0xFF737A89)
)
=======
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DefectCreateScreen(navController: NavController, stageId: Int) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Дефект") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Создание дефекта", style = MaterialTheme.typography.headlineMedium)
        Text("Этап ID: $stageId", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название дефекта *") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Статус:")
        Row {
            listOf("Дефект", "Исправлено", "Пропущен").forEach { s ->
                RadioButton(selected = status == s, onClick = { status = s })
                Text(s)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { if (title.isNotBlank()) navController.popBackStack() },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Сохранить дефект") }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Отмена") }
    }
}
>>>>>>> 4d10c6215eb7563b03f068add459083a6174924d
