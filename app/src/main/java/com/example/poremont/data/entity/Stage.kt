package com.example.poremont.data.entity

/**
 * A simple representation of a stage in a repair for display purposes.
 *
 * @property id Unique identifier of the stage.
 * @property roomId Identifier of the room this stage belongs to.
 * @property name Human‑readable name of the stage.
 * @property progress Completion ratio of this stage (0f–100f).
 * @property isCompleted Whether the stage is fully completed.
 * @property defectsCount Number of defects associated with this stage.
 */
data class Stage(
    val id: Int,
    val roomId: Int,
    val name: String,
    val progress: Float,
    val isCompleted: Boolean,
    val defectsCount: Int
)