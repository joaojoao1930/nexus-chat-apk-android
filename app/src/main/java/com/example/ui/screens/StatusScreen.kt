package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.StatusStory
import com.example.model.UserProfile
import com.example.ui.components.AvatarView
import com.example.ui.components.CreateStatusDialog
import com.example.ui.components.StoryViewerDialog
import com.example.ui.strings.AppStrings
import com.example.ui.theme.NexusTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(
    userProfile: UserProfile,
    statusStories: List<StatusStory>,
    language: AppLanguage,
    onAddStatus: (content: String, isPublic: Boolean, gradientIndex: Int) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedStoryForView by remember { mutableStateOf<StatusStory?>(null) }

    if (showCreateDialog) {
        CreateStatusDialog(
            language = language,
            onDismiss = { showCreateDialog = false },
            onPublish = { content, isPublic, gradientIndex ->
                showCreateDialog = false
                onAddStatus(content, isPublic, gradientIndex)
            }
        )
    }

    selectedStoryForView?.let { story ->
        StoryViewerDialog(
            story = story,
            onDismiss = { selectedStoryForView = null }
        )
    }

    val ephemeralStories = statusStories.filter { !it.isPublic }
    val publicStories = statusStories.filter { it.isPublic }
    val myStatus = statusStories.firstOrNull { it.isMine }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = AppStrings.get("tab_status", language),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Efêmeros e Públicos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.testTag("add_status_icon_button")
                    ) {
                        Icon(
                            Icons.Default.AddCircleOutline,
                            contentDescription = "Adicionar status",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                text = { Text(AppStrings.get("status_add_title", language)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("create_status_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // My Status Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (myStatus != null) {
                                selectedStoryForView = myStatus
                            } else {
                                showCreateDialog = true
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        AvatarView(
                            name = if (userProfile.isGuest) "Visitante" else userProfile.name.ifBlank { "Eu" },
                            emoji = "✨",
                            backgroundColor = 0xFF00D2C4,
                            hasStatusRing = myStatus != null,
                            size = 56.dp
                        )
                        if (myStatus == null) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.get("status_my_status", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (myStatus != null) "${myStatus.timestamp} • ${myStatus.viewsCount} visualizações" else AppStrings.get("status_tap_to_add", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Ephemeral stories carousel
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = AppStrings.get("status_ephemeral", language),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(ephemeralStories, key = { it.id }) { story ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(76.dp)
                                    .clickable { selectedStoryForView = story }
                            ) {
                                AvatarView(
                                    name = story.authorName,
                                    emoji = story.avatarEmoji,
                                    backgroundColor = story.avatarColor,
                                    hasStatusRing = true,
                                    size = 62.dp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = story.authorName.split(" ").firstOrNull() ?: story.authorName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = story.timestamp,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Public Status Section ("status públicas que dá para ver os status no público")
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Public,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("status_public", language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Descubra",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Status compartilhados publicamente por criadores e canais oficiais da comunidade Nexus.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(publicStories, key = { it.id }) { story ->
                val gradients = listOf(
                    Brush.horizontalGradient(listOf(Color(0xFF0077B6), Color(0xFF00D2C4))),
                    Brush.horizontalGradient(listOf(Color(0xFF7209B7), Color(0xFF4361EE))),
                    Brush.horizontalGradient(listOf(Color(0xFFEE9B00), Color(0xFFF72585))),
                    Brush.horizontalGradient(listOf(Color(0xFF0A9396), Color(0xFF06D6A0)))
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { selectedStoryForView = story },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column {
                        // Card Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarView(
                                name = story.authorName,
                                emoji = story.avatarEmoji,
                                backgroundColor = story.avatarColor,
                                size = 44.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = story.authorName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Verificado",
                                        tint = NexusTeal,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "${story.timestamp} • Público",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { selectedStoryForView = story }) {
                                Icon(Icons.Default.Fullscreen, contentDescription = "Ver status")
                            }
                        }

                        // Gradient Banner Content
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(gradients[story.bgGradientIndex % gradients.size])
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = story.content,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Footer with views count
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${story.viewsCount} visualizações",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            TextButton(onClick = { selectedStoryForView = story }) {
                                Text("Abrir Status")
                            }
                        }
                    }
                }
            }
        }
    }
}
