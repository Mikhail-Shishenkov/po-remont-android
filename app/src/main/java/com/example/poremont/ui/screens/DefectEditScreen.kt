package com.example.poremont.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.content.ContextCompat
import com.example.poremont.data.PreferencesManager
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun DefectEditScreen(
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
    val savedDefect = remember(roomName, stageName, questionIndex) {
        prefs.loadDefect(roomName, stageName, questionIndex)
    }

    var selectedStatus by remember { mutableStateOf(savedDefect?.status ?: PreferencesManager.STATUS_DEFECT) }
    var title by remember { mutableStateOf(savedDefect?.title ?: question) }
    var description by remember { mutableStateOf(savedDefect?.description.orEmpty()) }
    var photoUris by remember { mutableStateOf(savedDefect?.photoUris.orEmpty().take(2)) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null && photoUris.size < 2) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Не падаем, если поставщик файла не дал постоянное разрешение.
            }
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
            title = "Редактирование дефекта",
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
                selectedStatus = selectedStatus,
                enabled = true,
                onStatusSelected = { selectedStatus = it }
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
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF202633),
                    textDecoration = if (selectedStatus == PreferencesManager.STATUS_FIXED) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                ),
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
                            status = selectedStatus,
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
