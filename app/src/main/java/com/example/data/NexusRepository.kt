package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object NexusRepository {

    private val _userProfile = MutableStateFlow(
        UserProfile(
            isLoggedIn = false,
            isGuest = false,
            name = "",
            phoneNumber = "",
            avatarId = 0,
            bannerId = 0,
            bio = "Olá! Estou usando o Nexus Chat com criptografia ponta a ponta."
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _accentColor = MutableStateFlow(AccentColor.CYAN)
    val accentColor: StateFlow<AccentColor> = _accentColor.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.PT)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _notifications = MutableStateFlow(NotificationSettings())
    val notifications: StateFlow<NotificationSettings> = _notifications.asStateFlow()

    private val _chats = MutableStateFlow<List<Chat>>(initialChats())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _statusStories = MutableStateFlow<List<StatusStory>>(initialStatuses())
    val statusStories: StateFlow<List<StatusStory>> = _statusStories.asStateFlow()

    private val _socialPosts = MutableStateFlow<List<SocialPost>>(initialPosts())
    val socialPosts: StateFlow<List<SocialPost>> = _socialPosts.asStateFlow()

    private val _callRecords = MutableStateFlow<List<CallRecord>>(initialCalls())
    val callRecords: StateFlow<List<CallRecord>> = _callRecords.asStateFlow()

    private val _communities = MutableStateFlow<List<CommunityItem>>(initialCommunities())
    val communities: StateFlow<List<CommunityItem>> = _communities.asStateFlow()

    private val _nexusAiMessages = MutableStateFlow<List<ChatMessage>>(initialAiMessages())
    val nexusAiMessages: StateFlow<List<ChatMessage>> = _nexusAiMessages.asStateFlow()

    private val _activeChatId = MutableStateFlow<String?>(null)
    val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

    fun setActiveChat(id: String?) {
        _activeChatId.value = id
    }

    fun setLoggedIn(loggedIn: Boolean) {
        _userProfile.update {
            it.copy(isLoggedIn = loggedIn, isGuest = false)
        }
    }

    fun addPerson(phone: String, name: String) = addPersonChat(phone, name)
    fun addStatusStory(content: String, isPublic: Boolean, gradientIndex: Int) = addStatus(content, isPublic, gradientIndex)
    fun toggleLikePost(postId: String) = toggleLike(postId)
    fun addCommentToPost(postId: String, text: String) = addComment(postId, text)
    fun sendChatMessage(chatId: String, text: String) = sendMessage(chatId, text)
    fun sendAudioMessage(chatId: String, duration: Int) = sendMessage(chatId, "Mensagem de voz", isAudio = true, audioDuration = duration)
    fun loginWithPhone(name: String, phone: String) = login(name, phone)

    private fun currentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    fun login(name: String, phone: String) {
        _userProfile.update {
            it.copy(
                isLoggedIn = true,
                isGuest = false,
                name = name.ifBlank { "Usuário Nexus" },
                phoneNumber = phone.ifBlank { "+55 (11) 98765-4321" },
                avatarId = 1,
                bannerId = 0
            )
        }
    }

    fun enterAsGuest() {
        _userProfile.update {
            it.copy(
                isLoggedIn = false,
                isGuest = true,
                name = "Visitante",
                phoneNumber = ""
            )
        }
    }

    fun updateProfile(name: String, phone: String, avatarId: Int, bannerId: Int, bio: String) {
        _userProfile.update {
            it.copy(
                name = name,
                phoneNumber = phone,
                avatarId = avatarId,
                bannerId = bannerId,
                bio = bio
            )
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun setAccentColor(accent: AccentColor) {
        _accentColor.value = accent
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun updateNotifications(update: NotificationSettings) {
        _notifications.value = update
    }

    fun addPersonChat(phoneNumber: String, name: String? = null): Chat {
        val contactName = if (!name.isNullOrBlank()) name else "Contato $phoneNumber"
        val existing = _chats.value.find { it.phoneNumber == phoneNumber }
        if (existing != null) {
            return existing
        }
        val newChat = Chat(
            id = UUID.randomUUID().toString(),
            name = contactName,
            phoneNumber = phoneNumber,
            avatarColor = 0xFF0A9396,
            avatarEmoji = "👤",
            isGroup = false,
            isCommunity = false,
            isOnline = true,
            unreadCount = 0,
            lastMessage = "Conversa iniciada com criptografia de ponta a ponta.",
            lastMessageTime = currentTime(),
            messages = listOf(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    chatId = "",
                    senderName = "Nexus Security",
                    isMe = false,
                    text = "🔒 As mensagens e chamadas são protegidas com criptografia de ponta a ponta avançada.",
                    timestamp = currentTime(),
                    isEncrypted = true
                )
            )
        )
        _chats.update { listOf(newChat) + it }
        return newChat
    }

    fun createGroup(name: String, description: String, topic: String): Chat {
        val newGroup = Chat(
            id = UUID.randomUUID().toString(),
            name = name,
            phoneNumber = "Grupo (${topic.ifBlank { "Geral" }})",
            avatarColor = 0xFF7209B7,
            avatarEmoji = "👥",
            isGroup = true,
            isCommunity = false,
            isOnline = true,
            unreadCount = 0,
            lastMessage = "Grupo criado: $description",
            lastMessageTime = currentTime(),
            messages = listOf(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    chatId = "",
                    senderName = "Nexus System",
                    isMe = false,
                    text = "👥 Grupo \"$name\" criado. As conversas possuem criptografia de ponta a ponta.",
                    timestamp = currentTime(),
                    isEncrypted = true
                )
            )
        )
        _chats.update { listOf(newGroup) + it }
        return newGroup
    }

    fun createCommunity(name: String, description: String, category: String): CommunityItem {
        val newCommunity = CommunityItem(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            membersCount = 1,
            category = category.ifBlank { "Geral" },
            avatarEmoji = "🌐",
            gradientIndex = (_communities.value.size % 4)
        )
        _communities.update { listOf(newCommunity) + it }

        // Also add an announcement channel chat for this community
        val communityChat = Chat(
            id = UUID.randomUUID().toString(),
            name = "Avisos - $name",
            phoneNumber = "Comunidade Oficial",
            avatarColor = 0xFF00B4D8,
            avatarEmoji = "📢",
            isGroup = true,
            isCommunity = true,
            isOnline = true,
            unreadCount = 0,
            lastMessage = "Canal oficial da comunidade $name",
            lastMessageTime = currentTime(),
            messages = listOf(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    chatId = "",
                    senderName = "Nexus Comunidades",
                    isMe = false,
                    text = "📢 Bem-vindo à comunidade $name! $description",
                    timestamp = currentTime(),
                    isEncrypted = true
                )
            )
        )
        _chats.update { listOf(communityChat) + it }
        return newCommunity
    }

    fun sendMessage(chatId: String, text: String, isAudio: Boolean = false, audioDuration: Int = 0) {
        val sender = _userProfile.value.name.ifBlank { "Você" }
        val newMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderName = sender,
            isMe = true,
            text = text,
            timestamp = currentTime(),
            isAudio = isAudio,
            audioDurationSeconds = audioDuration,
            isEncrypted = true,
            status = MessageStatus.READ
        )

        _chats.update { list ->
            list.map { chat ->
                if (chat.id == chatId) {
                    val updatedMsgs = chat.messages + newMsg
                    chat.copy(
                        messages = updatedMsgs,
                        lastMessage = if (isAudio) "🎤 Mensagem de voz ($audioDuration s)" else text,
                        lastMessageTime = currentTime()
                    )
                } else chat
            }
        }
    }

    fun addStatus(content: String, isPublic: Boolean, gradientIndex: Int) {
        val author = if (_userProfile.value.isGuest) "Visitante" else _userProfile.value.name.ifBlank { "Eu" }
        val phone = _userProfile.value.phoneNumber.ifBlank { "Privado" }
        val newStatus = StatusStory(
            id = UUID.randomUUID().toString(),
            authorName = author,
            authorPhone = phone,
            avatarColor = 0xFF00D2C4,
            avatarEmoji = "✨",
            content = content,
            bgGradientIndex = gradientIndex,
            timestamp = "Agora",
            isPublic = isPublic,
            viewsCount = 1,
            isMine = true
        )
        _statusStories.update { listOf(newStatus) + it }
    }

    fun createPost(content: String, tag: String) {
        val author = if (_userProfile.value.isGuest) "Visitante Nexus" else _userProfile.value.name.ifBlank { "Usuário Nexus" }
        val phone = _userProfile.value.phoneNumber.ifBlank { "+55 11 9****-****" }
        val newPost = SocialPost(
            id = UUID.randomUUID().toString(),
            authorName = author,
            authorPhone = phone,
            avatarColor = 0xFF00B4D8,
            avatarEmoji = "🚀",
            timestamp = "Agora",
            content = content,
            tag = if (tag.startsWith("#")) tag else "#$tag",
            likesCount = 0,
            isLiked = false,
            commentsCount = 0,
            comments = emptyList()
        )
        _socialPosts.update { listOf(newPost) + it }
    }

    fun toggleLike(postId: String) {
        _socialPosts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val liked = !post.isLiked
                    val newCount = if (liked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                    post.copy(isLiked = liked, likesCount = newCount)
                } else post
            }
        }
    }

    fun addComment(postId: String, text: String) {
        val author = if (_userProfile.value.isGuest) "Visitante" else _userProfile.value.name.ifBlank { "Você" }
        val newComment = PostComment(
            id = UUID.randomUUID().toString(),
            authorName = author,
            content = text,
            timestamp = "Agora"
        )
        _socialPosts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(
                        comments = post.comments + newComment,
                        commentsCount = post.commentsCount + 1
                    )
                } else post
            }
        }
    }

    fun addCallRecord(
        contactName: String,
        phone: String,
        avatarColor: Long,
        avatarEmoji: String,
        callType: CallType,
        callDirection: CallDirection,
        duration: Int
    ) {
        val call = CallRecord(
            id = UUID.randomUUID().toString(),
            contactName = contactName,
            phoneNumber = phone,
            avatarColor = avatarColor,
            avatarEmoji = avatarEmoji,
            callType = callType,
            callDirection = callDirection,
            timestamp = currentTime(),
            durationSeconds = duration
        )
        _callRecords.update { listOf(call) + it }
    }

    fun sendAiMessage(userText: String, aiReplyText: String) {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatId = "nexus_ia",
            senderName = "Você",
            isMe = true,
            text = userText,
            timestamp = currentTime(),
            isEncrypted = true
        )
        val aiMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatId = "nexus_ia",
            senderName = "Nexus IA",
            isMe = false,
            text = aiReplyText,
            timestamp = currentTime(),
            isEncrypted = true
        )
        _nexusAiMessages.update { it + userMsg + aiMsg }
    }

    // Pre-populated data
    private fun initialChats(): List<Chat> = listOf(
        Chat(
            id = "chat_1",
            name = "Alice Ferreira",
            phoneNumber = "+55 11 98412-3001",
            avatarColor = 0xFF4361EE,
            avatarEmoji = "👩‍💻",
            isOnline = true,
            unreadCount = 2,
            lastMessage = "🎤 Mensagem de voz (0:14)",
            lastMessageTime = "10:42",
            messages = listOf(
                ChatMessage("m1", "chat_1", "Nexus Security", false, "🔒 As mensagens e chamadas deste chat estão protegidas com criptografia de ponta a ponta avançada.", "10:30"),
                ChatMessage("m2", "chat_1", "Alice Ferreira", false, "Oi! Viu a nova atualização com status públicos e Nexus IA?", "10:38"),
                ChatMessage("m3", "chat_1", "Você", true, "Ficou incrível! As chamadas criptografadas também estão muito rápidas.", "10:40"),
                ChatMessage("m4", "chat_1", "Alice Ferreira", false, "Gravei um áudio testando a qualidade do som!", "10:41"),
                ChatMessage("m5", "chat_1", "Alice Ferreira", false, "Áudio de voz", "10:42", isAudio = true, audioDurationSeconds = 14)
            )
        ),
        Chat(
            id = "chat_2",
            name = "Segurança & Criptografia Brasil",
            phoneNumber = "Grupo (5 membros)",
            avatarColor = 0xFF7209B7,
            avatarEmoji = "🛡️",
            isGroup = true,
            isOnline = true,
            unreadCount = 5,
            lastMessage = "Lucas: A chave assimétrica ECDH de 256 bits está ativa.",
            lastMessageTime = "10:15",
            messages = listOf(
                ChatMessage("m2_1", "chat_2", "Nexus Security", false, "🔒 Criptografia ponta a ponta verificada com chaves de 256 bits.", "09:00"),
                ChatMessage("m2_2", "chat_2", "Lucas", false, "A chave assimétrica ECDH de 256 bits está ativa.", "10:15")
            )
        ),
        Chat(
            id = "chat_3",
            name = "Carlos Mendes",
            phoneNumber = "+55 21 99345-6789",
            avatarColor = 0xFF0A9396,
            avatarEmoji = "👨‍🔬",
            isOnline = false,
            unreadCount = 0,
            lastMessage = "Pode me ligar quando terminar a reunião?",
            lastMessageTime = "Ontem",
            messages = listOf(
                ChatMessage("m3_1", "chat_3", "Nexus Security", false, "🔒 Criptografia ponta a ponta ativa.", "Ontem"),
                ChatMessage("m3_2", "chat_3", "Carlos Mendes", false, "Pode me ligar quando terminar a reunião?", "Ontem")
            )
        ),
        Chat(
            id = "chat_4",
            name = "Comunidade Desenvolvedores Nexus",
            phoneNumber = "Comunidade Oficial",
            avatarColor = 0xFF00B4D8,
            avatarEmoji = "🌐",
            isGroup = true,
            isCommunity = true,
            isOnline = true,
            unreadCount = 1,
            lastMessage = "Novos canais de suporte e novidades no ar!",
            lastMessageTime = "Ontem",
            messages = listOf(
                ChatMessage("m4_1", "chat_4", "Nexus Admin", false, "Bem-vindos à comunidade de desenvolvedores Nexus! Compartilhem suas ideias.", "Ontem")
            )
        )
    )

    private fun initialStatuses(): List<StatusStory> = listOf(
        StatusStory(
            id = "st_1",
            authorName = "Alice Ferreira",
            authorPhone = "+55 11 98412-3001",
            avatarColor = 0xFF4361EE,
            avatarEmoji = "👩‍💻",
            content = "Lançando nosso novo projeto com segurança ponta a ponta! 🚀🔒",
            bgGradientIndex = 0,
            timestamp = "Há 15 min",
            isPublic = false,
            viewsCount = 38
        ),
        StatusStory(
            id = "st_2",
            authorName = "Lucas Dev",
            authorPhone = "+55 11 97722-1100",
            avatarColor = 0xFF7209B7,
            avatarEmoji = "⚡",
            content = "Café + código limpo + Nexus Chat = produtividade máxima ☕💻",
            bgGradientIndex = 1,
            timestamp = "Há 1 hora",
            isPublic = false,
            viewsCount = 54
        ),
        // Public statuses
        StatusStory(
            id = "st_pub_1",
            authorName = "Nexus Notícias Oficiais",
            authorPhone = "+55 0800-NEXUS",
            avatarColor = 0xFF00D2C4,
            avatarEmoji = "📢",
            content = "✨ Novidade: Agora você pode explorar Status Públicos no Nexus Chat! Conecte-se com criadores globais.",
            bgGradientIndex = 2,
            timestamp = "Há 2 horas",
            isPublic = true,
            viewsCount = 1420
        ),
        StatusStory(
            id = "st_pub_2",
            authorName = "Tech Future Lab",
            authorPhone = "Canal Público",
            avatarColor = 0xFFEE9B00,
            avatarEmoji = "🔮",
            content = "Qual será o futuro da inteligência artificial assistiva nos apps sociais? Veja nosso estudo.",
            bgGradientIndex = 3,
            timestamp = "Há 4 horas",
            isPublic = true,
            viewsCount = 890
        )
    )

    private fun initialPosts(): List<SocialPost> = listOf(
        SocialPost(
            id = "post_1",
            authorName = "Guilherme Santos",
            authorPhone = "+55 11 99123-4567",
            avatarColor = 0xFF0A9396,
            avatarEmoji = "👨‍💻",
            timestamp = "Há 25 min",
            content = "A privacidade não deveria ser um luxo, mas um padrão. Adorando a velocidade do Nexus Chat e as chamadas totalmente criptografadas! 🛡️🔐",
            tag = "#SegurançaDigital",
            likesCount = 24,
            isLiked = true,
            commentsCount = 3,
            comments = listOf(
                PostComment("c1", "Alice Ferreira", "Totalmente de acordo! Segurança em primeiro lugar.", "Há 20 min"),
                PostComment("c2", "Carlos Mendes", "Interface muito elegante também!", "Há 10 min")
            )
        ),
        SocialPost(
            id = "post_2",
            authorName = "Comunidade TechNexus",
            authorPhone = "Comunidade Aberta",
            avatarColor = 0xFF7209B7,
            avatarEmoji = "🌐",
            timestamp = "Há 2 horas",
            content = "Como a inteligência artificial Nexus IA tem ajudado você hoje? Ela responde dúvidas sobre navegação, atalhos de grupos e segurança.",
            tag = "#NexusIA",
            likesCount = 57,
            isLiked = false,
            commentsCount = 5,
            comments = listOf(
                PostComment("c3", "Marina Silva", "Me ensinou a criar comunidades e filtrar chamadas rapidinho!", "Há 1 hora")
            )
        ),
        SocialPost(
            id = "post_3",
            authorName = "Beatriz Lima",
            authorPhone = "+55 21 98877-6655",
            avatarColor = 0xFFEE9B00,
            avatarEmoji = "🎨",
            timestamp = "Há 4 horas",
            content = "Status efêmeros de 24h e status públicos no mesmo app social. Dá pra manter o círculo íntimo nos efêmeros e compartilhar ideias no público!",
            tag = "#SocialNexus",
            likesCount = 42,
            isLiked = false,
            commentsCount = 2,
            comments = emptyList()
        )
    )

    private fun initialCalls(): List<CallRecord> = listOf(
        CallRecord("call_1", "Alice Ferreira", "+55 11 98412-3001", 0xFF4361EE, "👩‍💻", CallType.VOICE, CallDirection.INCOMING, "Hoje, 10:15", 142),
        CallRecord("call_2", "Carlos Mendes", "+55 21 99345-6789", 0xFF0A9396, "👨‍🔬", CallType.VIDEO, CallDirection.OUTGOING, "Ontem, 16:40", 310),
        CallRecord("call_3", "Beatriz Lima", "+55 21 98877-6655", 0xFFEE9B00, "🎨", CallType.VOICE, CallDirection.MISSED, "2 de set", 0)
    )

    private fun initialCommunities(): List<CommunityItem> = listOf(
        CommunityItem("com_1", "Desenvolvedores Nexus", "Espaço para criadores e devs compartilharem dicas, APIs e ideias.", 1240, "Tecnologia", "💻", 0),
        CommunityItem("com_2", "Criptografia & Privacidade", "Debates sobre segurança de dados, privacidade e protocolos descentralizados.", 890, "Segurança", "🔐", 1),
        CommunityItem("com_3", "Criadores de Conteúdo", "Comunidade pública para status, artigos e postagens sociais no Nexus.", 2150, "Cultura & Mídia", "🎨", 2)
    )

    private fun initialAiMessages(): List<ChatMessage> = listOf(
        ChatMessage(
            id = "ai_welcome",
            chatId = "nexus_ia",
            senderName = "Nexus IA",
            isMe = false,
            text = "Olá! Eu sou o Nexus IA, seu assistente pessoal e guia de navegação no Nexus Chat. Como posso ajudar você hoje?\n\nVocê pode me pedir ajuda para adicionar contatos, criar grupos, explorar status públicos ou entender nossa criptografia avançada!",
            timestamp = "Agora",
            isEncrypted = true
        )
    )
}
