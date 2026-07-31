package com.example.poremont.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.poremont.data.PreferencesManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

object PdfReportGenerator {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 42f
    private const val CONTENT_WIDTH = PAGE_WIDTH - MARGIN * 2
    private const val BOTTOM_MARGIN = 48f

    fun generateDefectsReport(
        context: Context,
        defects: List<PreferencesManager.DefectRecord>,
        roomTitle: String
    ): Result<String> {
        return runCatching {
            val date = Date()
            val displayDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru", "RU")).format(date)
            val fileDate = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(date)
            val fileName = "Отчет_о_дефектах_$fileDate.pdf"

            val document = PdfDocument()
            val page = ReportPage(document)

            page.drawTitle("Отчёт о дефектах")
            page.drawSmallText("Дата формирования: $displayDate")
            page.drawSmallText("Объект: $roomTitle")
            page.addSpace(18f)

            defects.forEachIndexed { index, defect ->
                page.ensureSpace(170f)
                page.drawDefectBlock(context, index + 1, defect)
            }

            page.finish()
            savePdf(context, document, fileName).also {
                document.close()
            }
        }
    }

    private fun savePdf(context: Context, document: PdfDocument, fileName: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/ПО Ремонт")
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: error("Не удалось создать файл отчёта")

            resolver.openOutputStream(uri)?.use { output ->
                document.writeTo(output)
            } ?: error("Не удалось открыть файл отчёта")

            return "Загрузки/ПО Ремонт/$fileName"
        }

        @Suppress("DEPRECATION")
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ПО Ремонт")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, fileName)
        FileOutputStream(file).use { output ->
            document.writeTo(output)
        }
        return file.absolutePath
    }

    private class ReportPage(
        private val document: PdfDocument
    ) {
        private var pageNumber = 0
        private var page = newPage()
        private var canvas: Canvas = page.canvas
        private var y = MARGIN

        private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(32, 38, 51)
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        private val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(32, 38, 51)
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(95, 103, 118)
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(122, 129, 144)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        private val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        fun drawTitle(text: String) {
            ensureSpace(42f)
            canvas.drawText(text, MARGIN, y + 24f, titlePaint)
            y += 42f
        }

        fun drawSmallText(text: String) {
            drawWrappedText(text, bodyPaint, MARGIN, CONTENT_WIDTH)
        }

        fun addSpace(value: Float) {
            y += value
        }

        fun drawDefectBlock(context: Context, number: Int, defect: PreferencesManager.DefectRecord) {
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(225, 228, 234)
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }

            // Сначала резервируем место с запасом, чтобы блок не разрезался внизу страницы.
            // Рамку рисуем после контента по фактической высоте: так она не уезжает,
            // если описание или фотографии заняли больше места, чем ожидалось.
            ensureSpace(estimateBlockHeight(defect))

            val blockTop = y
            val contentLeft = MARGIN + 18f
            val contentWidth = CONTENT_WIDTH - 36f

            y += 22f
            drawLabel("Этап", defect.stageName)
            y += 4f

            val statusLabel = when (defect.status) {
                PreferencesManager.STATUS_FIXED -> "исправлено"
                PreferencesManager.STATUS_SKIPPED -> "пропущено"
                else -> "дефект"
            }

            val title = "$number. ${defect.title}"
            val strike = defect.status == PreferencesManager.STATUS_FIXED || defect.status == PreferencesManager.STATUS_SKIPPED
            drawWrappedText(title, sectionPaint, contentLeft, contentWidth, strikeThrough = strike)
            drawStatusBadge(statusLabel)
            y += 8f

            if (defect.description.isNotBlank()) {
                drawLabel("Описание", defect.description)
                y += 6f
            }

            val photos = defect.photoUris.take(2)
            val visiblePhotoCount = if (photos.isNotEmpty()) photos.size else defect.photoCount.coerceIn(0, 2)
            if (visiblePhotoCount > 0) {
                canvas.drawText("Фотографии", contentLeft, y + 13f, labelPaint)
                y += 22f

                val gap = 14f

                if (visiblePhotoCount == 1) {
                    val photoWidth = contentWidth
                    val photoHeight = 255f
                    if (photos.isNotEmpty()) {
                        drawPhoto(context, photos.first(), contentLeft, y, photoWidth, photoHeight)
                    } else {
                        drawPhotoPlaceholder(contentLeft, y, photoWidth, photoHeight)
                    }
                    y += photoHeight + 14f
                } else {
                    val photoWidth = (contentWidth - gap) / 2f
                    val photoHeight = 185f
                    var x = contentLeft

                    if (photos.isNotEmpty()) {
                        photos.forEach { uriString ->
                            drawPhoto(context, uriString, x, y, photoWidth, photoHeight)
                            x += photoWidth + gap
                        }
                    } else {
                        repeat(visiblePhotoCount) {
                            drawPhotoPlaceholder(x, y, photoWidth, photoHeight)
                            x += photoWidth + gap
                        }
                    }
                    y += photoHeight + 14f
                }
            }

            val blockBottom = y + 8f
            val rect = RectF(MARGIN, blockTop, PAGE_WIDTH - MARGIN, blockBottom)
            canvas.drawRoundRect(rect, 12f, 12f, borderPaint)

            y = blockBottom + 14f
        }

        fun ensureSpace(height: Float) {
            if (y + height > PAGE_HEIGHT - BOTTOM_MARGIN) {
                finishCurrentPage()
                page = newPage()
                canvas = page.canvas
                y = MARGIN
            }
        }

        fun finish() {
            finishCurrentPage()
        }

        private fun drawLabel(label: String, value: String) {
            canvas.drawText(label, MARGIN + 18f, y + 13f, labelPaint)
            y += 21f
            drawWrappedText(value, bodyPaint, MARGIN + 18f, CONTENT_WIDTH - 36f)
        }

        private fun drawWrappedText(
            text: String,
            paint: Paint,
            x: Float,
            width: Float,
            strikeThrough: Boolean = false
        ) {
            val words = text.split(Regex("\\s+"))
            var line = ""
            val lineHeight = paint.textSize + 7f
            val lines = mutableListOf<String>()

            words.forEach { word ->
                val candidate = if (line.isBlank()) word else "$line $word"
                if (paint.measureText(candidate) <= width) {
                    line = candidate
                } else {
                    if (line.isNotBlank()) lines.add(line)
                    line = word
                }
            }
            if (line.isNotBlank()) lines.add(line)

            lines.forEach { currentLine ->
                ensureSpace(lineHeight + 4f)
                val baseline = y + paint.textSize
                canvas.drawText(currentLine, x, baseline, paint)
                if (strikeThrough) {
                    val strikeY = baseline - paint.textSize / 3f
                    canvas.drawLine(x, strikeY, x + paint.measureText(currentLine), strikeY, paint)
                }
                y += lineHeight
            }
        }

        private fun drawStatusBadge(status: String) {
            val (fg, bg) = when (status) {
                "исправлено" -> Color.rgb(8, 125, 85) to Color.rgb(217, 248, 234)
                "пропущено" -> Color.rgb(255, 152, 0) to Color.rgb(255, 242, 204)
                else -> Color.rgb(192, 24, 34) to Color.rgb(255, 224, 224)
            }

            val text = "Статус: $status"
            val x = MARGIN + 18f
            val top = y
            val width = badgePaint.measureText(text) + 22f
            val rect = RectF(x, top, x + width, top + 24f)

            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = bg
                style = Paint.Style.FILL
            }
            badgePaint.color = fg

            canvas.drawRoundRect(rect, 8f, 8f, bgPaint)
            canvas.drawText(text, x + 11f, top + 16f, badgePaint)
            y += 34f
        }

        private fun drawPhoto(
            context: Context,
            uriString: String,
            x: Float,
            top: Float,
            width: Float,
            height: Float
        ) {
            val bitmap = decodeBitmap(context, uriString)

            if (bitmap == null) {
                drawPhotoPlaceholder(x, top, width, height)
                return
            }

            val frame = RectF(x, top, x + width, top + height)
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(238, 238, 238) }
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(225, 228, 234)
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

            canvas.drawRoundRect(frame, 8f, 8f, bgPaint)

            val bitmapRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val frameRatio = width / height
            val dst = if (bitmapRatio > frameRatio) {
                val scaledHeight = width / bitmapRatio
                val verticalPadding = (height - scaledHeight) / 2f
                RectF(x, top + verticalPadding, x + width, top + verticalPadding + scaledHeight)
            } else {
                val scaledWidth = height * bitmapRatio
                val horizontalPadding = (width - scaledWidth) / 2f
                RectF(x + horizontalPadding, top, x + horizontalPadding + scaledWidth, top + height)
            }

            canvas.drawBitmap(bitmap, null, dst, Paint(Paint.ANTI_ALIAS_FLAG))
            canvas.drawRoundRect(frame, 8f, 8f, borderPaint)
        }

        private fun drawPhotoPlaceholder(x: Float, top: Float, width: Float, height: Float) {
            val rect = RectF(x, top, x + width, top + height)
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(226, 226, 226) }
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(157, 157, 157)
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawRoundRect(rect, 8f, 8f, bgPaint)
            canvas.drawText("400 × 300", rect.centerX(), rect.centerY() + 6f, textPaint)
        }

        private fun estimateBlockHeight(defect: PreferencesManager.DefectRecord): Float {
            val descriptionLines = if (defect.description.isBlank()) 0 else maxOf(1, defect.description.length / 52 + 1)
            val titleLines = maxOf(1, defect.title.length / 38 + 1)
            val stageLines = maxOf(1, defect.stageName.length / 52 + 1)
            val photoCount = if (defect.photoUris.isNotEmpty()) defect.photoUris.take(2).size else defect.photoCount.coerceIn(0, 2)
            val photoPart = when (photoCount) {
                0 -> 0f
                1 -> 291f
                else -> 221f
            }

            // Запас нужен для подписей, бейджа статуса, внутренних отступов и нижнего поля блока.
            return 130f + stageLines * 20f + titleLines * 23f + descriptionLines * 20f + photoPart
        }

        private fun newPage(): PdfDocument.Page {
            pageNumber += 1
            val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            return document.startPage(info)
        }

        private fun finishCurrentPage() {
            document.finishPage(page)
        }
    }

    private fun decodeBitmap(context: Context, uriString: String): Bitmap? {
        return runCatching {
            val uri = Uri.parse(uriString)
            if (uri.scheme == "file") {
                FileInputStream(File(uri.path.orEmpty())).use { BitmapFactory.decodeStream(it) }
            } else {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            }
        }.getOrNull()
    }
}
