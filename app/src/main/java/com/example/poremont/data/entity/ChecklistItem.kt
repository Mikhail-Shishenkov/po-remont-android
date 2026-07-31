package com.example.poremont.data.entity

/**
 * A single item within a stage checklist. Each checklist item has a unique
 * identifier within its stage, a textual question to ask the inspector and
 * the current status of the answer. Valid statuses are typically one of
 * "not_checked", "ok", "skipped" or "defect". The status field is stored as
 * plain text so that the UI can display and edit it freely without
 * depending on a particular enum implementation. See [StagesListScreen] for
 * how statuses are manipulated in the UI.
 *
 * @property id Unique identifier of this checklist item.
 * @property stageId Identifier of the stage this item belongs to.
 * @property question The question to present to the user.
 * @property status The current status of this item (e.g. ok, defect).
 */
data class ChecklistItem(
    val id: Int,
    val stageId: Int,
    val question: String,
    val status: String
)