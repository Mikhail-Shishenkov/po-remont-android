package com.example.poremont.data.entity

/**
 * Represents a defect discovered during inspection of a particular stage. A
 * defect has a free‑form title and description provided by the inspector, a
 * status indicating whether it has been corrected and a reference back to
 * the stage it belongs to. Defects are not currently persisted in a database
 * but instead live in memory within the UI. In a more complete
 * implementation these would be stored via Room and included in
 * [AppDatabase].
 *
 * @property id Unique identifier for this defect.
 * @property stageId Identifier of the stage that this defect relates to.
 * @property title Short summary of the defect.
 * @property description Optional longer description.
 * @property status One of "Дефект", "Исправлено" or "Пропущен".
 */
data class Defect(
    val id: Int,
    val stageId: Int,
    val title: String,
    val description: String,
    val status: String
)