package com.v2ray.ang.composeui.pages.growth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val GrowthPageBackground = Color(0xFF0B0E11)
internal val GrowthSurface = Color(0xFF14191F)
internal val GrowthSurfaceRaised = Color(0xFF1A212A)
internal val GrowthSurfaceSubtle = Color(0xFF202833)
internal val GrowthAccent = Color(0xFFF3BA2F)
internal val GrowthTextPrimary = Color(0xFFF5F7FA)
internal val GrowthTextSecondary = Color(0xFF9AA4B2)
internal val GrowthPositive = Color(0xFF2EBD85)
internal val GrowthNegative = Color(0xFFF6465D)
internal val GrowthBorder = Color(0xFF26303C)
internal val GrowthHeroGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFF7C84B), Color(0xFFE4A72A))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GrowthPageScaffold(
    title: String,
    onNavigateBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = GrowthPageBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        color = GrowthTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GrowthTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GrowthPageBackground,
                    titleContentColor = GrowthTextPrimary,
                    navigationIconContentColor = GrowthTextPrimary
                )
            )
        },
        content = content
    )
}

@Composable
internal fun GrowthSectionCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GrowthBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = GrowthSurface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
internal fun GrowthSectionTitle(
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = GrowthTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    color = GrowthTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
internal fun GrowthBadge(text: String) {
    Surface(
        color = GrowthAccent.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = GrowthAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
internal fun GrowthMetricBlock(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = value,
            color = GrowthTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = GrowthTextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
internal fun GrowthStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(GrowthSurfaceSubtle)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(text = label, color = GrowthTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            color = GrowthTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
internal fun GrowthInfoRow(label: String, value: String, emphasize: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = GrowthTextSecondary, fontSize = 13.sp)
        Text(
            text = value,
            color = if (emphasize) GrowthTextPrimary else GrowthTextSecondary,
            fontSize = 13.sp,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
internal fun GrowthListDivider() {
    HorizontalDivider(color = GrowthBorder)
}

@Composable
internal fun GrowthBulletItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(GrowthAccent)
        )
        Text(
            text = text,
            color = GrowthTextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
internal fun GrowthStatusView(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GrowthSurfaceRaised),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.take(1),
                    color = GrowthAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = title,
                color = GrowthTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = GrowthTextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
