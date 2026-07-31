package com.example.poremont.data

import android.content.Context
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Локальное хранилище MVP.
 *
 * Сейчас храним один активный ремонт:
 * - выбранные комнаты;
 * - выбранные этапы по каждой комнате;
 * - статусы пунктов чек-листа по связке комната + этап;
 * - дефекты по связке комната + этап + номер пункта чек-листа;
 * - URI фотографий дефектов, выбранных из галереи/документов.
 */
class PreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences("po_remont_prefs", Context.MODE_PRIVATE)

    data class DefectRecord(
        val roomName: String,
        val stageName: String,
        val questionIndex: Int,
        val question: String,
        val title: String,
        val description: String,
        val status: String,
        val photoCount: Int,
        val photoUris: List<String> = emptyList()
    )

    fun hasActiveProject(): Boolean {
        return loadRooms().isNotEmpty()
    }

    fun createProject(rooms: List<String>) {
        val cleanRooms = rooms.distinct()
        val emptyStages = cleanRooms.associateWith { emptyList<String>() }
        saveProject(cleanRooms, emptyStages)
    }

    fun saveRooms(rooms: List<String>) {
        createProject(rooms)
    }

    fun saveProject(rooms: List<String>, stages: Map<String, List<String>>) {
        val roomsStr = rooms.distinct().joinToString(separator = "|")
        val stagesStr = stages.entries.joinToString(separator = ";") { (room, stageList) ->
            room + ":" + stageList.distinct().joinToString(separator = ",")
        }

        prefs.edit()
            .putString(KEY_ROOMS, roomsStr)
            .putString(KEY_STAGES, stagesStr)
            .apply()
    }

    fun loadProject(): Pair<List<String>, Map<String, List<String>>>? {
        val rooms = loadRooms()
        if (rooms.isEmpty()) return null
        return rooms to loadStages()
    }

    fun loadRooms(): List<String> {
        val roomsStr = prefs.getString(KEY_ROOMS, "").orEmpty()
        return roomsStr
            .split("|")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    fun loadStages(): Map<String, List<String>> {
        val rooms = loadRooms()
        val stagesStr = prefs.getString(KEY_STAGES, "").orEmpty()
        val result = mutableMapOf<String, List<String>>()

        if (stagesStr.isNotBlank()) {
            stagesStr.split(";").forEach { pair ->
                val index = pair.indexOf(":")
                if (index > 0) {
                    val room = pair.substring(0, index)
                    val stagesPart = pair.substring(index + 1)
                    val stages = stagesPart
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    result[room] = stages
                }
            }
        }

        rooms.forEach { room ->
            result.putIfAbsent(room, emptyList())
        }

        return result
    }

    fun saveStagesForRoom(roomName: String, stages: List<String>) {
        val rooms = loadRooms()
        if (rooms.isEmpty()) return

        val currentStages = loadStages().toMutableMap()
        currentStages[roomName] = stages.distinct()
        saveProject(rooms, currentStages)
    }

    fun isSetupCompleted(): Boolean {
        val rooms = loadRooms()
        if (rooms.isEmpty()) return false

        val stages = loadStages()
        return rooms.all { room -> stages[room].orEmpty().isNotEmpty() }
    }

    fun saveChecklistStatus(roomName: String, stageName: String, questionIndex: Int, status: String) {
        val key = checklistKey(roomName, stageName)
        val statuses = loadChecklistStatuses(roomName, stageName).toMutableMap()
        statuses[questionIndex] = status
        val value = statuses.entries
            .sortedBy { it.key }
            .joinToString(separator = ";") { (index, itemStatus) ->
                "$index=$itemStatus"
            }

        prefs.edit()
            .putString(key, value)
            .apply()
    }

    fun loadChecklistStatuses(roomName: String, stageName: String): Map<Int, String> {
        val value = prefs.getString(checklistKey(roomName, stageName), "").orEmpty()
        if (value.isBlank()) return emptyMap()

        return value.split(";")
            .mapNotNull { item ->
                val parts = item.split("=", limit = 2)
                if (parts.size != 2) return@mapNotNull null
                val index = parts[0].toIntOrNull() ?: return@mapNotNull null
                index to parts[1]
            }
            .toMap()
    }

    fun clearChecklistForStage(roomName: String, stageName: String) {
        prefs.edit().remove(checklistKey(roomName, stageName)).apply()
    }

    fun saveDefect(defect: DefectRecord) {
        val cleanPhotoUris = defect.photoUris.distinct().take(2)
        val cleanDefect = defect.copy(
            photoUris = cleanPhotoUris,
            photoCount = if (cleanPhotoUris.isNotEmpty()) cleanPhotoUris.size else defect.photoCount.coerceIn(0, 2)
        )

        prefs.edit()
            .putString(defectKey(cleanDefect.roomName, cleanDefect.stageName, cleanDefect.questionIndex), serializeDefect(cleanDefect))
            .apply()

        val checklistStatus = when (cleanDefect.status) {
            STATUS_FIXED -> STATUS_FIXED
            STATUS_SKIPPED -> STATUS_SKIPPED
            else -> STATUS_DEFECT
        }
        saveChecklistStatus(cleanDefect.roomName, cleanDefect.stageName, cleanDefect.questionIndex, checklistStatus)
    }

    fun loadDefect(roomName: String, stageName: String, questionIndex: Int): DefectRecord? {
        val value = prefs.getString(defectKey(roomName, stageName, questionIndex), null) ?: return null
        return deserializeDefect(value)
    }

    fun loadDefects(roomName: String? = null): List<DefectRecord> {
        return prefs.all
            .filterKeys { it.startsWith(KEY_DEFECT_PREFIX) }
            .values
            .mapNotNull { deserializeDefect(it.toString()) }
            .filter { defect -> roomName == null || defect.roomName == roomName }
            .sortedWith(
                compareBy<DefectRecord> {
                    when (it.status) {
                        STATUS_DEFECT -> 0
                        STATUS_FIXED -> 1
                        STATUS_SKIPPED -> 2
                        else -> 3
                    }
                }.thenBy { it.stageName }.thenBy { it.title }
            )
    }

    fun hasAnyDefects(): Boolean {
        return loadDefects().isNotEmpty()
    }

    fun countDefectsForRoom(roomName: String): Int {
        return loadDefects(roomName).count { it.status == STATUS_DEFECT }
    }

    fun clearProject() {
        val editor = prefs.edit()
        prefs.all.keys.forEach { key ->
            if (
                key == KEY_ROOMS ||
                key == KEY_STAGES ||
                key.startsWith(KEY_CHECKLIST_PREFIX) ||
                key.startsWith(KEY_DEFECT_PREFIX)
            ) {
                editor.remove(key)
            }
        }
        editor.apply()
    }

    private fun checklistKey(roomName: String, stageName: String): String {
        return "$KEY_CHECKLIST_PREFIX::$roomName::$stageName"
    }

    private fun defectKey(roomName: String, stageName: String, questionIndex: Int): String {
        return "$KEY_DEFECT_PREFIX::$roomName::$stageName::$questionIndex"
    }

    private fun serializeDefect(defect: DefectRecord): String {
        return listOf(
            defect.roomName,
            defect.stageName,
            defect.questionIndex.toString(),
            defect.question,
            defect.title,
            defect.description,
            defect.status,
            defect.photoCount.toString(),
            defect.photoUris.joinToString(separator = PHOTO_SEPARATOR)
        ).joinToString(separator = FIELD_SEPARATOR) { encode(it) }
    }

    private fun deserializeDefect(value: String): DefectRecord? {
        val parts = value.split(FIELD_SEPARATOR)
        if (parts.size < 8) return null

        val photoCount = decode(parts[7]).toIntOrNull()?.coerceIn(0, 2) ?: 0
        val photoUris = if (parts.size >= 9) {
            decode(parts[8])
                .split(PHOTO_SEPARATOR)
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .take(2)
        } else {
            emptyList()
        }

        return DefectRecord(
            roomName = decode(parts[0]),
            stageName = decode(parts[1]),
            questionIndex = decode(parts[2]).toIntOrNull() ?: return null,
            question = decode(parts[3]),
            title = decode(parts[4]),
            description = decode(parts[5]),
            status = decode(parts[6]),
            photoCount = if (photoUris.isNotEmpty()) photoUris.size else photoCount,
            photoUris = photoUris
        )
    }

    private fun encode(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
    }

    private fun decode(value: String): String {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
    }

    companion object {
        const val STATUS_NOT_CHECKED = "not_checked"
        const val STATUS_GOOD = "good"
        const val STATUS_DEFECT = "defect"
        const val STATUS_SKIPPED = "skipped"
        const val STATUS_FIXED = "fixed"

        private const val KEY_ROOMS = "rooms"
        private const val KEY_STAGES = "stages"
        private const val KEY_CHECKLIST_PREFIX = "checklist_status"
        private const val KEY_DEFECT_PREFIX = "defect_item"
        private const val FIELD_SEPARATOR = "|"
        private const val PHOTO_SEPARATOR = "§"
    }
}
