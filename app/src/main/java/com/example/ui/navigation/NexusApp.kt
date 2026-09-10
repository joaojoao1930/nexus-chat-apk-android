package com.example.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.NexusRepository
import com.example.model.CallType
import com.example.ui.components.AddPersonDialog
import com.example.ui.components.CreateCommunityDialog
import com.example.ui.components.CreateGroupDialog
import com.example.ui.screens.*
import com.example.ui.strings.AppStrings
import com.example.ui.theme.NexusChatTheme

enum class MainTab {
    CHATS, STATUS, SOCIAL, NEXUS_IA, SETTINGS
}

data class ActiveCallState(
    val contactName: String,
    val phoneNumber: String,
    val type: CallType
)

@Composable
fun NexusApp() {
    val userProfile by NexusRepository.userProfile.collectAsState()
    val themeMode by NexusRepository.themeMode.collectAsState()
    val accentColor by NexusRepository.accentColor.collectAsState()
    val language by NexusRepository.language.collectAsState()
    val notifications by NexusRepository.notifications.collectAsState()

    val chats by NexusRepository.chats.collectAsState()
    val activeChatId by NexusRepository.activeChatId.collectAsState()
    val callRecords by NexusRepository.callRecords.collectAsState()
    val communities by NexusRepository.communities.collectAsState()
    val statusStories by NexusRepository.statusStories.collectAsState()
    val socialPosts by NexusRepository.socialPosts.collectAsState()
    val aiMessages by NexusRepository.nexusAiMessages.collectAsState()

    val hasEnteredApp = userProfile.isLoggedIn || userProfile.isGuest

    var currentTab by remember { mutableStateOf(MainTab.CHATS) }
    var activeCall by remember { mutableStateOf<ActiveCallState?>(null) }

    // Dialog shortcuts triggered from Nexus IA
    var showAddPersonDialog by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }

    if (showAddPersonDialog) {
        AddPersonDialog(
            language = language,
            onDismiss = { showAddPersonDialog = false },
            onConfirm = { phone, name ->
                showAddPersonDialog = false
                NexusRepository.addPerson(phone, name)
            }
        )
    }

    if (showCreateGroupDialog) {
        CreateGroupDialog(
            language = language,
            onDismiss = { showCreateGroupDialog = false },
            onCreate = { name, desc, topic ->
                showCreateGroupDialog = false
                NexusRepository.createGroup(name, desc, topic)
            }
        )
    }

    NexusChatTheme(
        themeMode = themeMode,
        accent = accentColor
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                // Active Call Fullscreen Overlay
                activeCall != null -> {
                    val call = activeCall!!
                    CallScreen(
                        contactName = call.contactName,
                        phoneNumber = call.phoneNumber,
                        callType = call.type,
                        language = language,
                        onEndCall = { activeCall = null }
                    )
                }

                // Welcome / Initial Login Screen
                !hasEnteredApp -> {
                    WelcomeScreen(
                        language = language,
                        onLoginSuccess = { name, phone ->
                            NexusRepository.loginWithPhone(name, phone)
                        },
                        onEnterAsGuest = {
                            NexusRepository.enterAsGuest()
                        }
                    )
                }

                // Active Chat Detail Screen
                activeChatId != null -> {
                    val currentChat = chats.find { it.id == activeChatId }
                    if (currentChat != null) {
                        ChatDetailScreen(
                            chat = currentChat,
                            language = language,
                            onBack = { NexusRepository.setActiveChat(null) },
                            onStartCall = { type ->
                                activeCall = ActiveCallState(
                                    contactName = currentChat.name,
                                    phoneNumber = currentChat.phoneNumber,
                                    type = type
                                )
                            },
                            onSendMessage = { text ->
                                NexusRepository.sendChatMessage(currentChat.id, text)
                            },
                            onSendAudio = { duration ->
                                NexusRepository.sendAudioMessage(currentChat.id, duration)
                            }
                        )
                    } else {
                        NexusRepository.setActiveChat(null)
                    }
                }

                // Main App with Bottom Navigation
                else -> {
                    val totalUnread = chats.sumOf { it.unreadCount }

                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 4.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == MainTab.CHATS,
                                    onClick = { currentTab = MainTab.CHATS },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (totalUnread > 0) {
                                                    Badge { Text(totalUnread.toString()) }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (currentTab == MainTab.CHATS) Icons.Default.Chat else Icons.Default.ChatBubbleOutline,
                                                contentDescription = AppStrings.get("tab_chats", language)
                                            )
                                        }
                                    },
                                    label = { Text(AppStrings.get("tab_chats", language), fontWeight = if (currentTab == MainTab.CHATS) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_tab_chats")
                                )

                                NavigationBarItem(
                                    selected = currentTab == MainTab.STATUS,
                                    onClick = { currentTab = MainTab.STATUS },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Adjust,
                                            contentDescription = AppStrings.get("tab_status", language)
                                        )
                                    },
                                    label = { Text(AppStrings.get("tab_status", language), fontWeight = if (currentTab == MainTab.STATUS) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_tab_status")
                                )

                                NavigationBarItem(
                                    selected = currentTab == MainTab.SOCIAL,
                                    onClick = { currentTab = MainTab.SOCIAL },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == MainTab.SOCIAL) Icons.Default.DynamicFeed else Icons.Default.Public,
                                            contentDescription = AppStrings.get("tab_social", language)
                                        )
                                    },
                                    label = { Text(AppStrings.get("tab_social", language), fontWeight = if (currentTab == MainTab.SOCIAL) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_tab_social")
                                )

                                NavigationBarItem(
                                    selected = currentTab == MainTab.NEXUS_IA,
                                    onClick = { currentTab = MainTab.NEXUS_IA },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = AppStrings.get("tab_nexus_ia", language)
                                        )
                                    },
                                    label = { Text(AppStrings.get("tab_nexus_ia", language), fontWeight = if (currentTab == MainTab.NEXUS_IA) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_tab_nexus_ia")
                                )

                                NavigationBarItem(
                                    selected = currentTab == MainTab.SETTINGS,
                                    onClick = { currentTab = MainTab.SETTINGS },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == MainTab.SETTINGS) Icons.Default.Settings else Icons.Default.SettingsSuggest,
                                            contentDescription = AppStrings.get("tab_settings", language)
                                        )
                                    },
                                    label = { Text(AppStrings.get("tab_settings", language), fontWeight = if (currentTab == MainTab.SETTINGS) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.testTag("nav_tab_settings")
                                )
                            }
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            when (currentTab) {
                                MainTab.CHATS -> {
                                    ChatsScreen(
                                        chats = chats,
                                        callRecords = callRecords,
                                        communities = communities,
                                        language = language,
                                        onChatClick = { chat -> NexusRepository.setActiveChat(chat.id) },
                                        onStartCall = { name, phone, type ->
                                            activeCall = ActiveCallState(name, phone, type)
                                        },
                                        onAddPerson = { phone, name -> NexusRepository.addPerson(phone, name) },
                                        onCreateGroup = { name, desc, topic -> NexusRepository.createGroup(name, desc, topic) },
                                        onCreateCommunity = { name, desc, cat -> NexusRepository.createCommunity(name, desc, cat) }
                                    )
                                }
                                MainTab.STATUS -> {
                                    StatusScreen(
                                        userProfile = userProfile,
                                        statusStories = statusStories,
                                        language = language,
                                        onAddStatus = { content, isPublic, gradientIndex ->
                                            NexusRepository.addStatusStory(content, isPublic, gradientIndex)
                                        }
                                    )
                                }
                                MainTab.SOCIAL -> {
                                    FeedScreen(
                                        posts = socialPosts,
                                        language = language,
                                        onLikePost = { postId -> NexusRepository.toggleLikePost(postId) },
                                        onAddComment = { postId, text -> NexusRepository.addCommentToPost(postId, text) },
                                        onCreatePost = { content, tag -> NexusRepository.createPost(content, tag) }
                                    )
                                }
                                MainTab.NEXUS_IA -> {
                                    NexusAiScreen(
                                        messages = aiMessages,
                                        language = language,
                                        onSendMessage = { userText, aiReply ->
                                            NexusRepository.sendAiMessage(userText, aiReply)
                                        },
                                        onNavigateAction = { actionTag ->
                                            when (actionTag) {
                                                "nav_chats" -> currentTab = MainTab.CHATS
                                                "nav_status" -> currentTab = MainTab.STATUS
                                                "nav_feed" -> currentTab = MainTab.SOCIAL
                                                "nav_settings" -> currentTab = MainTab.SETTINGS
                                                "action_add_person" -> {
                                                    currentTab = MainTab.CHATS
                                                    showAddPersonDialog = true
                                                }
                                                "action_create_group" -> {
                                                    currentTab = MainTab.CHATS
                                                    showCreateGroupDialog = true
                                                }
                                                "action_create_community" -> currentTab = MainTab.CHATS
                                            }
                                        }
                                    )
                                }
                                MainTab.SETTINGS -> {
                                    SettingsScreen(
                                        userProfile = userProfile,
                                        themeMode = themeMode,
                                        accentColor = accentColor,
                                        language = language,
                                        notifications = notifications,
                                        onUpdateProfile = { name, phone, avatarId, bannerId, bio ->
                                            NexusRepository.updateProfile(name, phone, avatarId, bannerId, bio)
                                        },
                                        onSetThemeMode = { NexusRepository.setThemeMode(it) },
                                        onSetAccentColor = { NexusRepository.setAccentColor(it) },
                                        onSetLanguage = { NexusRepository.setLanguage(it) },
                                        onUpdateNotifications = { NexusRepository.updateNotifications(it) },
                                        onLoginClick = {
                                            NexusRepository.setLoggedIn(false)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
