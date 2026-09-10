package com.example.model

enum class ThemeMode {
    DARK, LIGHT, SYSTEM
}

enum class AccentColor {
    CYAN, EMERALD, PURPLE, SUNSET
}

enum class AppLanguage {
    PT, EN, ES
}

enum class CallType {
    VOICE, VIDEO
}

enum class CallDirection {
    INCOMING, OUTGOING, MISSED
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

data class UserProfile(
    val isLoggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val name: String = "",
    val phoneNumber: String = "",
    val avatarId: Int = 0,
    val bannerId: Int = 0,
    val bio: String = "Olá! Estou usando o Nexus Chat com criptografia ponta a ponta."
)

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderName: String,
    val isMe: Boolean,
    val text: String,
    val timestamp: String,
    val isAudio: Boolean = false,
    val audioDurationSeconds: Int = 0,
    val isEncrypted: Boolean = true,
    val status: MessageStatus = MessageStatus.READ
)

data class Chat(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val avatarColor: Long,
    val avatarEmoji: String,
    val isGroup: Boolean = false,
    val isCommunity: Boolean = false,
    val isOnline: Boolean = false,
    val unreadCount: Int = 0,
    val lastMessage: String = "",
    val lastMessageTime: String = "",
    val messages: List<ChatMessage> = emptyList()
)

data class StatusStory(
    val id: String,
    val authorName: String,
    val authorPhone: String,
    val avatarColor: Long,
    val avatarEmoji: String,
    val content: String,
    val bgGradientIndex: Int = 0,
    val timestamp: String,
    val isPublic: Boolean = false,
    val viewsCount: Int = 0,
    val isMine: Boolean = false
)

data class PostComment(
    val id: String,
    val authorName: String,
    val content: String,
    val timestamp: String
)

data class SocialPost(
    val id: String,
    val authorName: String,
    val authorPhone: String,
    val avatarColor: Long,
    val avatarEmoji: String,
    val timestamp: String,
    val content: String,
    val tag: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val commentsCount: Int = 0,
    val comments: List<PostComment> = emptyList()
)

data class CallRecord(
    val id: String,
    val contactName: String,
    val phoneNumber: String,
    val avatarColor: Long,
    val avatarEmoji: String,
    val callType: CallType,
    val callDirection: CallDirection,
    val timestamp: String,
    val durationSeconds: Int = 0
)

data class CommunityItem(
    val id: String,
    val name: String,
    val description: String,
    val membersCount: Int,
    val category: String,
    val avatarEmoji: String,
    val gradientIndex: Int = 0
)

data class NotificationSettings(
    val messageNotifications: Boolean = true,
    val groupNotifications: Boolean = true,
    val callNotifications: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val messagePreview: Boolean = true
)
