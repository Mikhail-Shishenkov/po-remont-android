package com.example.poremont.data.reference

data class ReferenceCatalog(
    val version: Int,
    val updatedAt: String,
    val sections: List<ReferenceNode>
)

data class ReferenceNode(
    val id: String,
    val title: String,
    val description: String = "",
    val icon: String = "◆",
    val type: ReferenceNodeType = ReferenceNodeType.SECTION,
    val children: List<ReferenceNode> = emptyList(),
    val blocks: List<ReferenceBlock> = emptyList()
) {
    val isMaterial: Boolean
        get() = type == ReferenceNodeType.MATERIAL
}

enum class ReferenceNodeType {
    SECTION,
    MATERIAL
}

data class ReferenceBlock(
    val title: String,
    val text: String = "",
    val hint: String = "",
    val table: ReferenceTable? = null,
    val children: List<ReferenceBlock> = emptyList()
)

data class ReferenceTable(
    val columns: List<String>,
    val rows: List<List<String>>
)

data class ReferenceSearchResult(
    val node: ReferenceNode,
    val parentTitle: String,
    val matchLevel: String
)
