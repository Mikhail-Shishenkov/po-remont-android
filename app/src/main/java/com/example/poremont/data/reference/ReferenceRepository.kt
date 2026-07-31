package com.example.poremont.data.reference

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ReferenceRepository(private val context: Context) {
    private val assetPath = "reference/materials.json"
    private val updatedFile: File
        get() = File(context.filesDir, "reference/materials.json")

    fun getCatalog(): ReferenceCatalog {
        val json = if (updatedFile.exists()) {
            updatedFile.readText(Charsets.UTF_8)
        } else {
            context.assets.open(assetPath).bufferedReader(Charsets.UTF_8).use { it.readText() }
        }

        return parseCatalog(JSONObject(json))
    }

    fun getSections(): List<ReferenceNode> = getCatalog().sections

    fun findNode(id: String): ReferenceNode? {
        return flatten(getSections()).firstOrNull { it.id == id }
    }

    fun search(query: String): List<ReferenceSearchResult> {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) return emptyList()

        val results = mutableListOf<ReferenceSearchResult>()

        fun walk(node: ReferenceNode, parentTitle: String) {
            val titleMatch = node.title.lowercase().contains(normalized)
            val descriptionMatch = node.description.lowercase().contains(normalized)
            val blockMatch = node.blocks.any { blockContains(it, normalized) }

            if (titleMatch || descriptionMatch || blockMatch) {
                val level = when {
                    titleMatch -> if (node.isMaterial) "Материал" else "Раздел"
                    descriptionMatch -> "Описание"
                    else -> "Контент"
                }
                results.add(ReferenceSearchResult(node, parentTitle, level))
            }

            node.children.forEach { child -> walk(child, node.title) }
        }

        getSections().forEach { walk(it, "Справочник материалов") }
        return results.distinctBy { it.node.id }
    }

    fun saveUpdatedCatalog(json: String): Boolean {
        return try {
            val parsed = parseCatalog(JSONObject(json))
            if (parsed.sections.isEmpty()) return false

            val dir = File(context.filesDir, "reference")
            if (!dir.exists()) dir.mkdirs()
            updatedFile.writeText(json, Charsets.UTF_8)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun resetUpdatedCatalog() {
        if (updatedFile.exists()) updatedFile.delete()
    }

    private fun parseCatalog(json: JSONObject): ReferenceCatalog {
        return ReferenceCatalog(
            version = json.optInt("version", 1),
            updatedAt = json.optString("updatedAt", ""),
            sections = parseNodes(json.optJSONArray("sections") ?: JSONArray())
        )
    }

    private fun parseNodes(array: JSONArray): List<ReferenceNode> {
        val result = mutableListOf<ReferenceNode>()
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            result.add(parseNode(item))
        }
        return result
    }

    private fun parseNode(json: JSONObject): ReferenceNode {
        val type = if (json.optString("type") == "material") {
            ReferenceNodeType.MATERIAL
        } else {
            ReferenceNodeType.SECTION
        }

        return ReferenceNode(
            id = json.getString("id"),
            title = json.getString("title"),
            description = json.optString("description", ""),
            icon = json.optString("icon", "◆"),
            type = type,
            children = parseNodes(json.optJSONArray("children") ?: JSONArray()),
            blocks = parseBlocks(json.optJSONArray("blocks") ?: JSONArray())
        )
    }

    private fun parseBlocks(array: JSONArray): List<ReferenceBlock> {
        val result = mutableListOf<ReferenceBlock>()
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            result.add(
                ReferenceBlock(
                    title = item.getString("title"),
                    text = item.optString("text", ""),
                    hint = item.optString("hint", ""),
                    table = parseTable(item.optJSONObject("table")),
                    children = parseBlocks(item.optJSONArray("children") ?: JSONArray())
                )
            )
        }
        return result
    }

    private fun parseTable(json: JSONObject?): ReferenceTable? {
        if (json == null) return null
        val columns = json.optJSONArray("columns")?.toStringList().orEmpty()
        val rowsArray = json.optJSONArray("rows") ?: JSONArray()
        val rows = mutableListOf<List<String>>()

        for (index in 0 until rowsArray.length()) {
            rows.add(rowsArray.getJSONArray(index).toStringList())
        }

        return ReferenceTable(columns = columns, rows = rows)
    }

    private fun JSONArray.toStringList(): List<String> {
        val result = mutableListOf<String>()
        for (index in 0 until length()) {
            result.add(optString(index))
        }
        return result
    }

    private fun flatten(nodes: List<ReferenceNode>): List<ReferenceNode> {
        return nodes.flatMap { node -> listOf(node) + flatten(node.children) }
    }

    private fun blockContains(block: ReferenceBlock, query: String): Boolean {
        val ownMatch = block.title.lowercase().contains(query) ||
            block.text.lowercase().contains(query) ||
            block.hint.lowercase().contains(query) ||
            block.table?.columns?.any { it.lowercase().contains(query) } == true ||
            block.table?.rows?.flatten()?.any { it.lowercase().contains(query) } == true

        return ownMatch || block.children.any { blockContains(it, query) }
    }
}
