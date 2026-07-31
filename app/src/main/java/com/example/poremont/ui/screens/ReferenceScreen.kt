package com.example.poremont.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.poremont.data.reference.ReferenceBlock
import com.example.poremont.data.reference.ReferenceNode
import com.example.poremont.data.reference.ReferenceRepository
import com.example.poremont.data.reference.ReferenceSearchResult
import com.example.poremont.data.reference.ReferenceTable

private val PurpleStart = Color(0xFF4A16E8)
private val PurpleEnd = Color(0xFF7048C7)
private val TextPrimary = Color(0xFF202633)
private val TextSecondary = Color(0xFF737B8C)
private val BorderColor = Color(0xFFE1E4EA)
private val ScreenBackground = Color(0xFFF7F8FA)

private val HeaderButtonSize = 56.dp
private val HeaderButtonRadius = 18.dp
private val CardIconSize = 52.dp
private val CardIconRadius = 14.dp

@Composable
fun ReferenceScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { ReferenceRepository(context) }
    val sections = remember { repository.getSections() }
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(query) { repository.search(query) }

    ReferenceScaffold(
        title = "Справочник материалов",
        subtitle = null,
        query = query,
        onQueryChange = { query = it },
        onBack = { navController.popBackStack() }
    ) {
        if (query.isNotBlank()) {
            SearchResults(
                results = results,
                onResultClick = { result -> openReferenceNode(navController, result.node) }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(sections) { section ->
                    ReferenceListCard(
                        node = section,
                        subtitle = "${section.children.size} материала",
                        onClick = { navController.navigate("reference_list/${section.id}") }
                    )
                }
            }
        }
    }
}

@Composable
fun ReferenceListScreen(
    navController: NavController,
    nodeId: String
) {
    val context = LocalContext.current
    val repository = remember { ReferenceRepository(context) }
    val node = remember(nodeId) { repository.findNode(nodeId) }
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(query) { repository.search(query) }

    if (node == null) {
        ReferenceNotFound(onBack = { navController.popBackStack() })
        return
    }

    ReferenceScaffold(
        title = node.title,
        subtitle = node.description.ifBlank { "Выберите тип материала" },
        query = query,
        onQueryChange = { query = it },
        onBack = { navController.popBackStack() },
        showSearch = query.isNotBlank()
    ) {
        if (query.isNotBlank()) {
            SearchResults(
                results = results,
                onResultClick = { result -> openReferenceNode(navController, result.node) }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(node.children) { child ->
                    val subtitle = when {
                        child.isMaterial -> child.description
                        child.children.isNotEmpty() -> "${child.children.size} материала"
                        else -> child.description
                    }

                    ReferenceListCard(
                        node = child,
                        subtitle = subtitle,
                        onClick = { openReferenceNode(navController, child) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReferenceMaterialScreen(
    navController: NavController,
    materialId: String
) {
    val context = LocalContext.current
    val repository = remember { ReferenceRepository(context) }
    val material = remember(materialId) { repository.findNode(materialId) }
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(query) { repository.search(query) }

    if (material == null) {
        ReferenceNotFound(onBack = { navController.popBackStack() })
        return
    }

    ReferenceScaffold(
        title = material.title,
        subtitle = material.description,
        query = query,
        onQueryChange = { query = it },
        onBack = { navController.popBackStack() }
    ) {
        if (query.isNotBlank()) {
            SearchResults(
                results = results,
                onResultClick = { result -> openReferenceNode(navController, result.node) }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    MaterialDescriptionCard(material.description)
                }

                items(material.blocks) { block ->
                    ReferenceBlockCard(
                        block = block,
                        defaultExpanded = material.blocks.firstOrNull() == block
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun ReferenceScaffold(
    title: String,
    subtitle: String?,
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    showSearch: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        ReferenceHeader(
            title = title,
            subtitle = subtitle,
            query = query,
            onQueryChange = onQueryChange,
            onBack = onBack,
            showSearch = showSearch
        )

        content()
    }
}

@Composable
private fun ReferenceHeader(
    title: String,
    subtitle: String?,
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    showSearch: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(PurpleStart, PurpleEnd)
                )
            )
            .padding(start = 24.dp, end = 24.dp, top = 34.dp, bottom = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(
                text = "‹",
                onClick = onBack
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (showSearch) {
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Поиск материалов...",
                        color = Color(0xFF8D94A3)
                    )
                },
                trailingIcon = {
                    Text(
                        text = if (query.isBlank()) "⌕" else "×",
                        fontSize = 30.sp,
                        color = Color(0xFF707888),
                        modifier = Modifier.clickable {
                            if (query.isNotBlank()) onQueryChange("")
                        }
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = PurpleStart,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
        }
    }
}

@Composable
private fun HeaderIconButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(HeaderButtonSize)
            .clickable { onClick() },
        shape = RoundedCornerShape(HeaderButtonRadius),
        color = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = Color.Black,
                fontSize = 34.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ReferenceListCard(
    node: ReferenceNode,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(CardIconSize)
                    .clip(RoundedCornerShape(CardIconRadius))
                    .background(Color(0xFFF0F1F5)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = node.icon,
                    fontSize = 23.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "›",
                color = Color.Black,
                fontSize = 36.sp
            )
        }
    }
}

@Composable
private fun SearchResults(
    results: List<ReferenceSearchResult>,
    onResultClick: (ReferenceSearchResult) -> Unit
) {
    if (results.isEmpty()) {
        EmptySearchState()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(results) { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onResultClick(result) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(CardIconSize)
                                .clip(RoundedCornerShape(CardIconRadius))
                                .background(if (result.node.isMaterial) PurpleStart else Color(0xFFF0F1F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = result.node.icon,
                                fontSize = 22.sp,
                                color = if (result.node.isMaterial) Color.White else Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = result.node.title,
                                color = TextPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = searchResultSubtitle(result),
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(text = "›", fontSize = 34.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(48.dp))
                    .background(Color(0xFFEDEEF2)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⌕", fontSize = 56.sp, color = Color(0xFF98A0AD))
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Ничего не найдено",
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Попробуйте изменить запрос или использовать другие ключевые слова",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun MaterialDescriptionCard(description: String) {
    if (description.isBlank()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = description,
            modifier = Modifier.padding(16.dp),
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ReferenceBlockCard(
    block: ReferenceBlock,
    defaultExpanded: Boolean = false
) {
    var expanded by rememberSaveable(block.title) { mutableStateOf(defaultExpanded) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (expanded) Color.White else Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF0F1F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "◆", color = PurpleStart, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = block.title,
                    modifier = Modifier.weight(1f),
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (expanded) "⌄" else "›",
                    color = Color.Black,
                    fontSize = 28.sp
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                if (block.text.isNotBlank()) {
                    Text(
                        text = block.text,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 21.sp
                    )
                }

                if (block.hint.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HintBox(block.hint)
                }

                block.table?.let { table ->
                    Spacer(modifier = Modifier.height(10.dp))
                    AdaptiveTable(table)
                }

                block.children.forEach { child ->
                    Spacer(modifier = Modifier.height(10.dp))
                    ReferenceBlockCard(block = child, defaultExpanded = false)
                }
            }
        }
    }
}

@Composable
private fun HintBox(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0EDFF))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "💡", fontSize = 18.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = TextPrimary,
            style = MaterialTheme.typography.bodySmall,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun AdaptiveTable(table: ReferenceTable) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        table.rows.forEach { row ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7F8FA))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEachIndexed { index, value ->
                    val columnName = table.columns.getOrNull(index).orEmpty()
                    Column {
                        if (columnName.isNotBlank()) {
                            Text(
                                text = columnName,
                                color = TextSecondary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = value,
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReferenceNotFound(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        ReferenceHeader(
            title = "Справочник",
            subtitle = null,
            query = "",
            onQueryChange = {},
            onBack = onBack,
            showSearch = false
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Раздел не найден",
                color = TextSecondary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private fun searchResultSubtitle(result: ReferenceSearchResult): String {
    val typeLabel = when {
        result.node.isMaterial -> "Материал"
        result.parentTitle == "Справочник материалов" -> "Раздел"
        result.matchLevel == "Контент" -> "Блок справочника"
        else -> "Раздел"
    }

    return if (result.parentTitle.isBlank() || result.parentTitle == "Справочник материалов") {
        typeLabel
    } else {
        "$typeLabel · ${result.parentTitle}"
    }
}

private fun openReferenceNode(navController: NavController, node: ReferenceNode) {
    if (node.isMaterial) {
        navController.navigate("reference_material/${node.id}")
    } else {
        navController.navigate("reference_list/${node.id}")
    }
}
