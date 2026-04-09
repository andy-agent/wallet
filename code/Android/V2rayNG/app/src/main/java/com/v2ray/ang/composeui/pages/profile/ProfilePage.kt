package com.v2ray.ang.composeui.pages.profile

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.profile.ProfileBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private enum class ProfileScreen {
    SETTINGS,
    SECURITY,
}

private data class ProfileSettingRow(
    val icon: ImageVector,
    val title: String,
    val trailingText: String? = null,
    val onClick: () -> Unit,
)

private data class SupportTile(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit,
)

private data class AvatarSwatch(
    val color: Color,
    val glyph: String,
)

data class UserInfo(
    val userId: String,
    val email: String,
    val nickname: String?,
    val memberLevel: String,
)

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Loaded(val user: UserInfo) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val profileBridgeRepository = ProfileBridgeRepository(application)
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            val user = profileBridgeRepository.getCachedCurrentUser()
            if (user == null) {
                _state.value = ProfileState.Error("当前未登录")
                return@launch
            }

            val orders = profileBridgeRepository.getCachedOrders(user.userId)
            val activeOrder =
                orders.firstOrNull { it.expiredAt != null && it.expiredAt > System.currentTimeMillis() }
            _state.value = ProfileState.Loaded(
                user = UserInfo(
                    userId = user.userId,
                    email = user.email ?: user.username,
                    nickname = user.username.substringBefore("@"),
                    memberLevel = if (activeOrder != null) "已订阅" else "基础用户",
                ),
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            profileBridgeRepository.logout()
            _state.value = ProfileState.Error("已退出登录")
            onLoggedOut()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToOrders: () -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onNavigateToInvite: () -> Unit = {},
    onNavigateToCommission: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToLegal: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    var currentScreen by rememberSaveable { mutableStateOf(ProfileScreen.SETTINGS) }
    var showAvatarSheet by rememberSaveable { mutableStateOf(false) }
    var showLogoutSheet by rememberSaveable { mutableStateOf(false) }
    var selectedAvatarIndex by rememberSaveable { mutableIntStateOf(1) }
    var selectedSwatchIndex by rememberSaveable { mutableIntStateOf(1) }
    val avatarSwatches = remember {
        listOf(
            AvatarSwatch(Color(0xFF8FA59B), "🚀"),
            AvatarSwatch(Color(0xFFA2A8B5), "💾"),
            AvatarSwatch(Color(0xFFD1AEA2), "😎"),
            AvatarSwatch(Color(0xFFD4BE97), "🔥"),
            AvatarSwatch(Color(0xFF9EB39A), "🤖"),
            AvatarSwatch(Color(0xFF95AFB9), "🦄"),
            AvatarSwatch(Color(0xFFAAA0B7), "⭐"),
        )
    }
    val avatarGlyphs = remember {
        listOf(
            "🚀", "💰", "😎", "🔥", "💎", "🤖", "🦄",
            "🦌", "⚡", "👻", "🦁", "🏆", "👀", "⭐",
            "🐬", "🦍", "⛏️", "🔑", "🍕", "➡️", "🐱",
            "🐼", "🐯", "🐶", "🦋", "🦊", "😊", "😁",
            "😄", "😆", "😂", "🤣",
        )
    }

    val loadedUser = (state as? ProfileState.Loaded)?.user

    ProfilePageScaffold(
        topBar = {
            ProfileTopBar(
                title = if (currentScreen == ProfileScreen.SETTINGS) "设置" else "钱包安全",
                onNavigateBack = if (currentScreen == ProfileScreen.SECURITY) {
                    { currentScreen = ProfileScreen.SETTINGS }
                } else {
                    null
                },
            )
        },
    ) { paddingValues ->
        when (val currentState = state) {
            ProfileState.Loading -> Unit
            is ProfileState.Error -> {
                ProfileErrorView(
                    message = currentState.message,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is ProfileState.Loaded -> {
                when (currentScreen) {
                    ProfileScreen.SETTINGS -> {
                        ProfileSettingsContent(
                            user = currentState.user,
                            paddingValues = paddingValues,
                            onOpenSecurity = { currentScreen = ProfileScreen.SECURITY },
                            onNavigateToWallet = onNavigateToWallet,
                            onNavigateToInvite = onNavigateToInvite,
                            onNavigateToCommission = onNavigateToCommission,
                            onNavigateToSettings = onNavigateToSettings,
                            onNavigateToLegal = onNavigateToLegal,
                            onNavigateToSupport = onNavigateToSupport,
                            onNavigateToAbout = onNavigateToAbout,
                            onNavigateToOrders = onNavigateToOrders,
                        )
                    }

                    ProfileScreen.SECURITY -> {
                        ProfileSecurityContent(
                            user = currentState.user,
                            paddingValues = paddingValues,
                            avatar = avatarGlyphs[selectedAvatarIndex],
                            avatarColor = avatarSwatches[selectedSwatchIndex].color,
                            onAvatarClick = { showAvatarSheet = true },
                            onNavigateToWallet = onNavigateToWallet,
                            onNavigateToOrders = onNavigateToOrders,
                            onNavigateToLegal = onNavigateToLegal,
                            onNavigateToSettings = onNavigateToSettings,
                            onLogoutClick = { showLogoutSheet = true },
                        )
                    }
                }
            }
        }
    }

    if (showAvatarSheet && loadedUser != null) {
        ProfileAvatarPickerSheet(
            selectedAvatarIndex = selectedAvatarIndex,
            selectedSwatchIndex = selectedSwatchIndex,
            swatches = avatarSwatches,
            avatars = avatarGlyphs,
            onSelectAvatar = { selectedAvatarIndex = it },
            onSelectSwatch = { selectedSwatchIndex = it },
            onDismiss = { showAvatarSheet = false },
        )
    }

    if (showLogoutSheet) {
        ProfileLogoutSheet(
            onDismiss = { showLogoutSheet = false },
            onConfirm = {
                showLogoutSheet = false
                viewModel.logout(onLoggedOut = onLogout)
            },
        )
    }
}

@Composable
private fun ProfileSettingsContent(
    user: UserInfo,
    paddingValues: PaddingValues,
    onOpenSecurity: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToInvite: () -> Unit,
    onNavigateToCommission: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLegal: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToOrders: () -> Unit,
) {
    val primaryRows = remember(onOpenSecurity, onNavigateToWallet, onNavigateToSettings, onNavigateToInvite, onNavigateToCommission) {
        listOf(
            ProfileSettingRow(Icons.Default.Shield, "钱包安全", onClick = onOpenSecurity),
            ProfileSettingRow(Icons.Default.AccountBalanceWallet, "地址簿", onClick = onNavigateToWallet),
            ProfileSettingRow(Icons.Default.Notifications, "消息推送", onClick = onNavigateToSettings),
            ProfileSettingRow(Icons.Default.Settings, "偏好设置", onClick = onNavigateToSettings),
            ProfileSettingRow(Icons.Default.Route, "节点与线路", onClick = onNavigateToSettings),
            ProfileSettingRow(Icons.Default.Campaign, "邀请中心", onClick = onNavigateToInvite),
            ProfileSettingRow(Icons.AutoMirrored.Filled.ReceiptLong, "佣金账本", onClick = onNavigateToCommission),
        )
    }
    val secondaryRows = remember(onNavigateToLegal, onNavigateToAbout) {
        listOf(
            ProfileSettingRow(Icons.Default.PrivacyTip, "法务文档", onClick = onNavigateToLegal),
            ProfileSettingRow(Icons.Default.Info, "关于 CryptoVPN", trailingText = "v 1.0.0", onClick = onNavigateToAbout),
        )
    }
    val supportTiles = remember(onNavigateToSupport, onNavigateToOrders) {
        listOf(
            SupportTile(Icons.Default.Headphones, "获取帮助", onNavigateToSupport),
            SupportTile(Icons.AutoMirrored.Filled.Help, "订单与订阅", onNavigateToOrders),
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 10.dp,
            bottom = 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ProfileCard {
                ProfileBadge(
                    text = user.memberLevel,
                    containerColor = ProfileAccent.copy(alpha = 0.1f),
                    contentColor = ProfileAccent,
                )
                Text(
                    text = user.nickname ?: user.email.substringBefore("@"),
                    color = ProfileTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = user.email,
                    color = ProfileTextSecondary,
                    fontSize = 14.sp,
                )
                Text(
                    text = "账户、安全与支持入口统一收进浅色卡片，减少在白底页面里的视觉噪声。",
                    color = ProfileTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )
            }
        }

        item {
            ProfileSectionHeading(
                title = "账户与偏好",
                subtitle = "钱包、安全与常用设置保持同一套浅色容器层级。",
            )
        }

        item {
            ProfileCard(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)) {
                primaryRows.forEachIndexed { index, item ->
                    ProfileSettingsRow(item = item)
                    if (index != primaryRows.lastIndex) {
                        ProfileListDivider()
                    }
                }
            }
        }

        item {
            ProfileSectionHeading(
                title = "法务与支持",
                subtitle = "辅助入口收敛成更轻的卡片和说明层级。",
            )
        }

        item {
            ProfileCard(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)) {
                secondaryRows.forEachIndexed { index, item ->
                    ProfileSettingsRow(item = item)
                    if (index != secondaryRows.lastIndex) {
                        ProfileListDivider()
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                supportTiles.forEach { tile ->
                    ProfileSupportTile(
                        tile = tile,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSecurityContent(
    user: UserInfo,
    paddingValues: PaddingValues,
    avatar: String,
    avatarColor: Color,
    onAvatarClick: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToLegal: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val securityRows = remember(onNavigateToSettings, onNavigateToWallet, onNavigateToOrders, onNavigateToLegal) {
        listOf(
            ProfileSettingRow(Icons.Default.Devices, "社交登录介绍", onClick = onNavigateToSettings),
            ProfileSettingRow(Icons.Default.Lock, "登录账号", trailingText = user.email, onClick = onNavigateToSettings),
            ProfileSettingRow(Icons.Default.AccountBalanceWallet, "我的钱包", onClick = onNavigateToWallet),
            ProfileSettingRow(Icons.AutoMirrored.Filled.ReceiptLong, "订单与订阅", onClick = onNavigateToOrders),
            ProfileSettingRow(Icons.Default.PrivacyTip, "法务文档", onClick = onNavigateToLegal),
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ProfileCard {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Surface(
                        modifier = Modifier
                            .size(96.dp)
                            .clickable(onClick = onAvatarClick),
                        shape = CircleShape,
                        color = avatarColor.copy(alpha = 0.18f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            avatarColor.copy(alpha = 0.26f),
                        ),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = avatar,
                                fontSize = 42.sp,
                            )
                        }
                    }
                    Text(
                        text = user.email,
                        color = ProfileTextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "ID: ${maskUserId(user.userId)}",
                        color = ProfileTextSecondary,
                        fontSize = 13.sp,
                    )
                    ProfileBadge(
                        text = "建议补充验证",
                        containerColor = ProfileWarningSurface,
                        contentColor = ProfileWarningText,
                    )
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = ProfileWarningSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    ProfileWarningText.copy(alpha = 0.18f),
                ),
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "安全建议",
                        color = ProfileWarningText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "至少开启一种验证方式，保证资产安全",
                        color = ProfileTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }
        }

        item {
            ProfileSectionHeading(
                title = "账户与验证",
                subtitle = "保持轻量说明，再进入登录、钱包与订阅信息。",
            )
        }

        item {
            ProfileCard(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)) {
                securityRows.forEachIndexed { index, item ->
                    ProfileSettingsRow(item = item)
                    if (index != securityRows.lastIndex) {
                        ProfileListDivider()
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ProfileDanger),
            ) {
                Text(
                    text = "退出钱包",
                    color = ProfileDanger,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun ProfileSettingsRow(item: ProfileSettingRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ProfileSurfaceRaised,
        ) {
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = ProfileTextSecondary,
                )
            }
        }

        Text(
            text = item.title,
            color = ProfileTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )

        item.trailingText?.let {
            Text(
                text = it,
                color = if (it.startsWith("v")) ProfileTextTertiary else ProfileTextSecondary,
                fontSize = 13.sp,
                maxLines = 1,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = ProfileTextSecondary,
        )
    }
}

@Composable
private fun ProfileSupportTile(
    tile: SupportTile,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = tile.onClick),
        shape = RoundedCornerShape(24.dp),
        color = ProfileSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ProfileDivider),
        shadowElevation = 2.dp,
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = ProfileSurfaceRaised,
            ) {
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = tile.icon,
                        contentDescription = null,
                        tint = ProfileTextPrimary,
                    )
                }
            }
            Text(
                text = tile.title,
                color = ProfileTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileAvatarPickerSheet(
    selectedAvatarIndex: Int,
    selectedSwatchIndex: Int,
    swatches: List<AvatarSwatch>,
    avatars: List<String>,
    onSelectAvatar: (Int) -> Unit,
    onSelectSwatch: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ProfileSurface,
        scrimColor = ProfileTextPrimary.copy(alpha = 0.18f),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(44.dp)
                    .height(5.dp),
                color = ProfileDivider,
                shape = RoundedCornerShape(999.dp),
            ) {}
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Surface(
                        modifier = Modifier.size(104.dp),
                        shape = CircleShape,
                        color = swatches[selectedSwatchIndex].color,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = avatars[selectedAvatarIndex], fontSize = 40.sp)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    swatches.forEachIndexed { index, item ->
                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .clickable(onClick = { onSelectSwatch(index) }),
                            shape = CircleShape,
                            color = item.color,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (index == selectedSwatchIndex) 3.dp else 0.dp,
                                color = if (index == selectedSwatchIndex) ProfileTextPrimary else Color.Transparent,
                            ),
                        ) {}
                    }
                }
            }

            item {
                Text(
                    text = "推荐",
                    color = ProfileTextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            item {
                EmojiGrid(
                    avatars = avatars.take(21),
                    selectedIndex = selectedAvatarIndex,
                    onSelectAvatar = onSelectAvatar,
                )
            }

            item {
                Text(
                    text = "笑脸与人物",
                    color = ProfileTextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            item {
                EmojiGrid(
                    avatars = avatars.drop(21),
                    selectedIndex = selectedAvatarIndex - 21,
                    offset = 21,
                    onSelectAvatar = onSelectAvatar,
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = ProfileAccent,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "保存",
                            color = ProfilePageBackground,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmojiGrid(
    avatars: List<String>,
    selectedIndex: Int,
    offset: Int = 0,
    onSelectAvatar: (Int) -> Unit,
) {
    androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        avatars.chunked(7).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                row.forEachIndexed { index, avatar ->
                    val actualIndex = offset + avatars.indexOf(avatar)
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable(onClick = { onSelectAvatar(actualIndex) }),
                        shape = RoundedCornerShape(16.dp),
                        color = if (actualIndex == offset + selectedIndex) ProfileSurfaceRaised else Color.Transparent,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = avatar, fontSize = 24.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileLogoutSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ProfileSurface,
        scrimColor = ProfileTextPrimary.copy(alpha = 0.18f),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(44.dp)
                    .height(5.dp),
                color = ProfileDivider,
                shape = RoundedCornerShape(999.dp),
            ) {}
        },
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "退出钱包",
                color = ProfileTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "退出后会清除当前会话，需要重新登录才能访问钱包和邀请相关页面。",
                color = ProfileTextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ProfileDivider),
                ) {
                    Text("取消", color = ProfileTextPrimary)
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = ProfileDanger,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onConfirm)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "确认退出",
                            color = ProfilePageBackground,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ProfileErrorView(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        ProfileCard(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "无法加载 Profile",
                color = ProfileTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                color = ProfileTextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

private fun maskUserId(userId: String): String {
    if (userId.length <= 10) return userId
    return "${userId.take(4)}...${userId.takeLast(4)}"
}

@Preview(showBackground = true)
@Composable
private fun ProfilePagePreview() {
    MaterialTheme {
        ProfileBitgetBackground {
            ProfileSettingsContent(
                user = UserInfo(
                    userId = "dcce0ed7",
                    email = "zsc***rui@gmail.com",
                    nickname = "zsc",
                    memberLevel = "已订阅",
                ),
                paddingValues = PaddingValues(),
                onOpenSecurity = {},
                onNavigateToWallet = {},
                onNavigateToInvite = {},
                onNavigateToCommission = {},
                onNavigateToSettings = {},
                onNavigateToLegal = {},
                onNavigateToSupport = {},
                onNavigateToAbout = {},
                onNavigateToOrders = {},
            )
        }
    }
}
