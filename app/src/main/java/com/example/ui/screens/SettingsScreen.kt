package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.AvatarView
import com.example.ui.components.LoginDialog
import com.example.ui.strings.AppStrings
import com.example.ui.theme.NexusTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userProfile: UserProfile,
    themeMode: ThemeMode,
    accentColor: AccentColor,
    language: AppLanguage,
    notifications: NotificationSettings,
    onUpdateProfile: (name: String, phone: String, avatarId: Int, bannerId: Int, bio: String) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetAccentColor: (AccentColor) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onUpdateNotifications: (NotificationSettings) -> Unit,
    onLoginClick: () -> Unit
) {
    var showLoginDialog by remember { mutableStateOf(false) }

    // Profile editing states
    var editName by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var editPhone by remember(userProfile.phoneNumber) { mutableStateOf(userProfile.phoneNumber) }
    var editBio by remember(userProfile.bio) { mutableStateOf(userProfile.bio) }
    var selectedAvatarId by remember(userProfile.avatarId) { mutableIntStateOf(userProfile.avatarId) }
    var selectedBannerId by remember(userProfile.bannerId) { mutableIntStateOf(userProfile.bannerId) }
    var profileSavedFeedback by remember { mutableStateOf(false) }

    if (showLoginDialog) {
        LoginDialog(
            language = language,
            onDismiss = { showLoginDialog = false },
            onLogin = { name, phone ->
                showLoginDialog = false
                onUpdateProfile(name, phone, selectedAvatarId, selectedBannerId, editBio)
            }
        )
    }

    val avatarPresets = listOf(
        Triple("🧑‍💻", 0xFF0A9396, "Dev"),
        Triple("👩‍💼", 0xFF4361EE, "Pro"),
        Triple("🚀", 0xFF7209B7, "Nexus"),
        Triple("⚡", 0xFFEE9B00, "Flash"),
        Triple("✨", 0xFF00D2C4, "Star"),
        Triple("🛡️", 0xFF06D6A0, "Shield")
    )

    val bannerGradients = listOf(
        Brush.horizontalGradient(listOf(Color(0xFF0077B6), Color(0xFF00D2C4))),
        Brush.horizontalGradient(listOf(Color(0xFF7209B7), Color(0xFF4361EE))),
        Brush.horizontalGradient(listOf(Color(0xFF0A9396), Color(0xFF06D6A0))),
        Brush.horizontalGradient(listOf(Color(0xFFB5179E), Color(0xFF7209B7)))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.get("settings_title", language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp, vertical = 12.dp)
        ) {
            // SECTION 1: PERFIL (Profile Configuration)
            item {
                Text(
                    text = AppStrings.get("section_profile", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (userProfile.isGuest) {
                    // Specific prompt requirement:
                    // "se apertar 'Entrar sendo visitante' vai entrar no aplicativo só que no perfil não existe é só apenas visitante mas não configuração de perfil vai aparecer texto de 'esse perfil não existe, faça login' e o botão 'Log in' que pode criar uma conta no app social"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("visitor_profile_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NoAccounts,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = AppStrings.get("guest_no_profile", language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.testTag("guest_no_profile_text")
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = AppStrings.get("profile_login_prompt", language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = { showLoginDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_from_guest_profile_button")
                            ) {
                                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = AppStrings.get("btn_login", language),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    // Logged in user profile card with Banner, Avatar, Name, Phone
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column {
                            // Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(bannerGradients[selectedBannerId % bannerGradients.size])
                            ) {
                                if (selectedBannerId == 0) {
                                    Image(
                                        painter = painterResource(id = R.drawable.nexus_banner),
                                        contentDescription = "Banner Nexus",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Banner Ativo",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Avatar + Profile info
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .offset(y = (-36).dp)
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    val currentAvatar = avatarPresets.getOrNull(selectedAvatarId) ?: avatarPresets[0]
                                    AvatarView(
                                        name = editName,
                                        emoji = currentAvatar.first,
                                        backgroundColor = currentAvatar.second,
                                        size = 72.dp,
                                        hasStatusRing = true
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.padding(bottom = 6.dp)) {
                                        Text(
                                            text = editName.ifBlank { "Usuário Nexus" },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = editPhone.ifBlank { "Sem número" },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Banner Selector ("mudar banner de perfil")
                                Text(
                                    text = AppStrings.get("profile_banner", language),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(bannerGradients.size) { index ->
                                        Box(
                                            modifier = Modifier
                                                .size(width = 64.dp, height = 34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(bannerGradients[index])
                                                .clickable { selectedBannerId = index }
                                                .border(
                                                    width = if (selectedBannerId == index) 2.dp else 0.dp,
                                                    color = if (selectedBannerId == index) Color.White else Color.Transparent,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Avatar Selector ("mudar foto de perfil")
                                Text(
                                    text = AppStrings.get("profile_photo", language),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(avatarPresets.size) { index ->
                                        val (emoji, color, _) = avatarPresets[index]
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(Color(color))
                                                .clickable { selectedAvatarId = index }
                                                .border(
                                                    width = if (selectedAvatarId == index) 2.5.dp else 0.dp,
                                                    color = if (selectedAvatarId == index) NexusTeal else Color.Transparent,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(emoji, fontSize = 20.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Edit Profile Name ("mudar o nome de perfil")
                                OutlinedTextField(
                                    value = editName,
                                    onValueChange = { editName = it },
                                    label = { Text(AppStrings.get("profile_name", language)) },
                                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Edit Phone Number ("mudar o número de telefone no app social")
                                OutlinedTextField(
                                    value = editPhone,
                                    onValueChange = { editPhone = it },
                                    label = { Text(AppStrings.get("profile_phone", language)) },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_phone")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = editBio,
                                    onValueChange = { editBio = it },
                                    label = { Text("Recado / Bio") },
                                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                    maxLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        onUpdateProfile(editName, editPhone, selectedAvatarId, selectedBannerId, editBio)
                                        profileSavedFeedback = true
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (profileSavedFeedback) "Alterações Salvas!" else AppStrings.get("btn_save", language)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 2: TEMA (Theme & Appearance)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = AppStrings.get("section_theme", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Modo de Exibição",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = themeMode == ThemeMode.DARK,
                                onClick = { onSetThemeMode(ThemeMode.DARK) },
                                label = { Text(AppStrings.get("theme_dark", language)) },
                                leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                            FilterChip(
                                selected = themeMode == ThemeMode.LIGHT,
                                onClick = { onSetThemeMode(ThemeMode.LIGHT) },
                                label = { Text(AppStrings.get("theme_light", language)) },
                                leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                            FilterChip(
                                selected = themeMode == ThemeMode.SYSTEM,
                                onClick = { onSetThemeMode(ThemeMode.SYSTEM) },
                                label = { Text(AppStrings.get("theme_system", language)) },
                                leadingIcon = { Icon(Icons.Default.SettingsBrightness, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Cor de Destaque",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            AccentOption(color = 0xFF00D2C4, label = "Cyan", selected = accentColor == AccentColor.CYAN) {
                                onSetAccentColor(AccentColor.CYAN)
                            }
                            AccentOption(color = 0xFF06D6A0, label = "Emerald", selected = accentColor == AccentColor.EMERALD) {
                                onSetAccentColor(AccentColor.EMERALD)
                            }
                            AccentOption(color = 0xFF7209B7, label = "Purple", selected = accentColor == AccentColor.PURPLE) {
                                onSetAccentColor(AccentColor.PURPLE)
                            }
                            AccentOption(color = 0xFFEE9B00, label = "Sunset", selected = accentColor == AccentColor.SUNSET) {
                                onSetAccentColor(AccentColor.SUNSET)
                            }
                        }
                    }
                }
            }

            // SECTION 3: IDIOMA (App Language)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = AppStrings.get("section_language", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        LanguageItem(
                            title = "Português (Brasil)",
                            flag = "🇧🇷",
                            selected = language == AppLanguage.PT,
                            onClick = { onSetLanguage(AppLanguage.PT) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        LanguageItem(
                            title = "English",
                            flag = "🇺🇸",
                            selected = language == AppLanguage.EN,
                            onClick = { onSetLanguage(AppLanguage.EN) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        LanguageItem(
                            title = "Español",
                            flag = "🇪🇸",
                            selected = language == AppLanguage.ES,
                            onClick = { onSetLanguage(AppLanguage.ES) }
                        )
                    }
                }
            }

            // SECTION 4: NOTIFICAÇÕES (Notification Settings)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = AppStrings.get("section_notifications", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        NotificationToggleRow(
                            title = AppStrings.get("notif_messages", language),
                            checked = notifications.messageNotifications,
                            onCheckedChange = { onUpdateNotifications(notifications.copy(messageNotifications = it)) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        NotificationToggleRow(
                            title = AppStrings.get("notif_groups", language),
                            checked = notifications.groupNotifications,
                            onCheckedChange = { onUpdateNotifications(notifications.copy(groupNotifications = it)) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        NotificationToggleRow(
                            title = AppStrings.get("notif_calls", language),
                            checked = notifications.callNotifications,
                            onCheckedChange = { onUpdateNotifications(notifications.copy(callNotifications = it)) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        NotificationToggleRow(
                            title = AppStrings.get("notif_sound", language),
                            checked = notifications.soundEnabled,
                            onCheckedChange = { onUpdateNotifications(notifications.copy(soundEnabled = it)) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        NotificationToggleRow(
                            title = AppStrings.get("notif_vibrate", language),
                            checked = notifications.vibrationEnabled,
                            onCheckedChange = { onUpdateNotifications(notifications.copy(vibrationEnabled = it)) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        NotificationToggleRow(
                            title = AppStrings.get("notif_preview", language),
                            checked = notifications.messagePreview,
                            onCheckedChange = { onUpdateNotifications(notifications.copy(messagePreview = it)) }
                        )
                    }
                }
            }

            // Security footer
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nexus Protocol E2EE 256-bit",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Todas as comunicações são protegidas com criptografia de ponta a ponta avançada.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun AccentOption(
    color: Long,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(color))
                .border(
                    width = if (selected) 2.5.dp else 0.dp,
                    color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LanguageItem(
    title: String,
    flag: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(flag, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selecionado",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
