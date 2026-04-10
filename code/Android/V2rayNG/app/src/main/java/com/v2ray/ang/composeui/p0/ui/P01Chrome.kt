package com.v2ray.ang.composeui.p0.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

private val P01BgTop = Color(0xFFF8FBFF)
private val P01BgBottom = Color(0xFFEEF6FF)
private val P01Surface = Color(0xDFFFFFFF)
private val P01SurfaceStrong = Color(0xF5FFFFFF)
private val P01SurfaceMuted = Color(0xFFF5FAFF)
private val P01Border = Color(0x246880DB)
private val P01BorderStrong = Color(0x386880DB)
private val P01TextStrong = Color(0xFF132748)
private val P01TextBody = Color(0xFF4D6287)
private val P01TextSoft = Color(0xFF7B8DB0)
private val P01AccentBlue = Color(0xFF4276FF)
private val P01AccentCyan = Color(0xFF20C4F4)
private val P01AccentMint = Color(0xFF49D89B)
private val P01AccentGold = Color(0xFFF6B155)
private val P01AccentLilac = Color(0xFFB58DFF)
private val P01AccentDeep = Color(0xFF3454D2)
private val P01AccentNavy = Color(0xFF243A8F)

private val P01OuterBrush = Brush.verticalGradient(listOf(P01BgTop, P01BgBottom))
private val P01InnerBrush = Brush.verticalGradient(
    colors = listOf(Color(0xF2FFFFFF), Color(0xEBF3FAFF)),
)
private val P01PrimaryBrush = Brush.horizontalGradient(listOf(Color(0xFF4874FF), Color(0xFF1FD0F4)))

enum class P01BottomIconKind {
    OVERVIEW,
    VPN,
    WALLET,
    GROWTH,
    PROFILE,
}

data class P01BottomDestination(
    val route: String,
    val label: String,
    val iconKind: P01BottomIconKind,
)

data class P01MetricCell(
    val label: String,
    val value: String,
    val valueColor: Color = P01TextStrong,
)

@Composable
fun P01PhoneScaffold(
    statusTime: String,
    currentRoute: String,
    onBottomNav: (String) -> Unit = {},
    destinations: List<P01BottomDestination> = defaultP01Destinations(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(P01OuterBrush)
            .drawBehind {
                drawDecorativeDots()
            }
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(34.dp))
                .background(P01InnerBrush)
                .border(1.dp, P01Border, RoundedCornerShape(34.dp))
                .drawBehind {
                    drawScreenTexture()
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            ) {
                P01StatusBar(time = statusTime)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    content = content,
                )
                Spacer(modifier = Modifier.height(18.dp))
                P01BottomNav(
                    currentRoute = currentRoute,
                    destinations = destinations,
                    onNavigate = onBottomNav,
                )
            }
        }
    }
}

@Composable
fun P01Header(
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    chips: List<String> = emptyList(),
    backLabel: String? = null,
    onBack: (() -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (backLabel != null) {
            Text(
                text = backLabel,
                modifier = Modifier.clickable(enabled = onBack != null) { onBack?.invoke() },
                color = P01TextSoft,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = eyebrow,
            style = TextStyle(
                color = P01TextSoft,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.4.sp,
            ),
        )
        if (chips.isNotEmpty()) {
            P01FlowRow(horizontalGap = 8.dp, verticalGap = 8.dp) {
                chips.forEach { label ->
                    P01Chip(text = label)
                }
            }
        }
        Text(
            text = title,
            style = TextStyle(
                color = P01TextStrong,
                fontSize = 34.sp,
                lineHeight = 35.sp,
                fontWeight = FontWeight.ExtraBold,
            ),
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                color = P01TextBody,
                fontSize = 14.sp,
                lineHeight = 24.sp,
            )
        }
    }
}

@Composable
fun P01Card(
    modifier: Modifier = Modifier,
    centered: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(P01Surface)
            .border(1.dp, P01Border, RoundedCornerShape(26.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
        content = content,
    )
}

@Composable
fun P01CardHeader(
    title: String,
    trailing: @Composable (() -> Unit)? = null,
    subtitle: String? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(if (subtitle.isNullOrBlank()) 0.dp else 6.dp),
        ) {
            Text(
                text = title,
                color = P01TextStrong,
                fontSize = 22.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = P01TextBody,
                    fontSize = 13.sp,
                    lineHeight = 21.sp,
                )
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailing()
        }
    }
}

@Composable
fun P01CardCopy(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = P01TextBody,
        fontSize = 13.sp,
        lineHeight = 21.sp,
    )
}

@Composable
fun P01Chip(text: String, highlighted: Boolean = true) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (highlighted) P01AccentBlue.copy(alpha = 0.08f) else P01SurfaceStrong)
            .then(
                if (highlighted) Modifier else Modifier.border(
                    1.dp,
                    P01Border,
                    RoundedCornerShape(999.dp),
                )
            )
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(
            text = text,
            color = if (highlighted) P01AccentBlue else P01TextSoft,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun P01Tab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) P01AccentBlue.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.82f))
            .border(1.dp, if (selected) Color.Transparent else P01Border, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(
            text = text,
            color = if (selected) P01AccentBlue else P01TextSoft,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun P01MetricGrid(
    items: List<P01MetricCell>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { item ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(P01SurfaceMuted)
                            .border(1.dp, P01Border, RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = item.label,
                            color = P01TextSoft,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.8.sp,
                        )
                        Text(
                            text = item.value,
                            color = item.valueColor,
                            fontSize = 24.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun P01List(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        content = content,
    )
}

@Composable
fun P01ListRow(
    title: String,
    value: String? = null,
    copy: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = onClick != null,
                interactionSource = interaction,
                indication = null,
            ) { onClick?.invoke() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(if (copy.isNullOrBlank()) 0.dp else 4.dp),
        ) {
            Text(
                text = title,
                color = P01TextStrong,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!copy.isNullOrBlank()) {
                Text(
                    text = copy,
                    color = P01TextBody,
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                )
            }
        }
        if (!value.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = value,
                color = P01TextStrong,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun P01InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    password: Boolean = false,
    trailingText: String? = null,
    onTrailingClick: (() -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = P01TextSoft,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!trailingText.isNullOrBlank()) {
                Text(
                    text = trailingText,
                    modifier = Modifier.clickable(enabled = onTrailingClick != null) { onTrailingClick?.invoke() },
                    color = P01AccentBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(P01SurfaceStrong)
                .border(1.dp, P01Border, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 15.dp),
        ) {
            CompositionLocalProvider(
                androidx.compose.material3.LocalTextStyle provides TextStyle(
                    color = P01TextBody,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                ),
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (value.isBlank()) {
                            Text(
                                text = label,
                                color = P01TextSoft.copy(alpha = 0.65f),
                                fontSize = 14.sp,
                            )
                        }
                        innerTextField()
                    },
                )
            }
        }
    }
}

@Composable
fun P01SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(P01SurfaceStrong)
            .border(1.dp, P01Border, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = P01TextBody,
                fontSize = 14.sp,
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                        drawCircle(
                            color = P01TextSoft,
                            radius = size.minDimension * 0.34f,
                            center = center.copy(x = size.width * 0.42f, y = size.height * 0.42f),
                            style = stroke,
                        )
                        drawLine(
                            color = P01TextSoft,
                            start = Offset(size.width * 0.62f, size.height * 0.62f),
                            end = Offset(size.width * 0.88f, size.height * 0.88f),
                            strokeWidth = 1.8.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    Box {
                        if (value.isBlank()) {
                            Text(
                                text = placeholder,
                                color = P01TextSoft,
                                fontSize = 14.sp,
                            )
                        }
                        innerTextField()
                    }
                }
            },
        )
    }
}

@Composable
fun P01PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(P01PrimaryBrush)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun P01SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(P01AccentBlue.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = P01AccentBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun P01ButtonRow(
    primaryLabel: String,
    onPrimaryClick: () -> Unit,
    secondaryLabel: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
) {
    if (secondaryLabel.isNullOrBlank()) {
        P01PrimaryButton(
            text = primaryLabel,
            onClick = onPrimaryClick,
            modifier = Modifier.fillMaxWidth(),
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            P01SecondaryButton(
                text = secondaryLabel,
                onClick = { onSecondaryClick?.invoke() },
                modifier = Modifier.weight(1f),
            )
            P01PrimaryButton(
                text = primaryLabel,
                onClick = onPrimaryClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun P01SuccessBadge(
    symbol: String,
    tint: Color = P01AccentMint,
) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(tint.copy(alpha = 0.22f), P01SurfaceStrong),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            color = tint,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
fun P01Orb(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.size(172.dp),
    ) {
        val center = center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.96f), P01AccentBlue.copy(alpha = 0.16f)),
            ),
            radius = size.minDimension / 2f,
            center = center,
        )
        drawCircle(
            color = P01AccentBlue.copy(alpha = 0.12f),
            radius = size.minDimension * 0.34f,
            center = center,
            style = Stroke(width = 18.dp.toPx()),
        )
        drawCircle(
            color = P01AccentCyan.copy(alpha = 0.16f),
            radius = size.minDimension * 0.24f,
            center = center,
            style = Stroke(width = 12.dp.toPx()),
        )
        drawCircle(
            color = P01AccentLilac.copy(alpha = 0.14f),
            radius = size.minDimension * 0.45f,
            center = center,
            style = Stroke(width = 6.dp.toPx()),
        )
    }
}

@Composable
fun P01QrArt(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(180.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White),
    ) {
        drawRect(color = Color.White)
        drawQrCorner(Offset(18.dp.toPx(), 18.dp.toPx()))
        drawQrCorner(Offset(size.width - 62.dp.toPx(), 18.dp.toPx()))
        drawQrCorner(Offset(18.dp.toPx(), size.height - 62.dp.toPx()))
        val unit = size.width / 12f
        val cells = listOf(
            4 to 3, 5 to 3, 7 to 3,
            4 to 4, 6 to 4, 7 to 4,
            3 to 5, 4 to 5, 6 to 5, 8 to 5,
            3 to 6, 5 to 6, 6 to 6, 8 to 6,
            4 to 7, 5 to 7, 7 to 7,
            3 to 8, 4 to 8, 6 to 8, 7 to 8,
        )
        cells.forEach { (x, y) ->
            drawRoundRect(
                color = P01AccentDeep,
                topLeft = Offset(x * unit, y * unit),
                size = Size(unit * 0.72f, unit * 0.72f),
                cornerRadius = CornerRadius(unit * 0.12f, unit * 0.12f),
            )
        }
    }
}

@Composable
fun P01BottomNav(
    currentRoute: String,
    destinations: List<P01BottomDestination>,
    onNavigate: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(P01Surface)
            .border(1.dp, P01Border, RoundedCornerShape(24.dp))
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        destinations.forEach { item ->
            val active = currentRoute == item.route
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (active) P01AccentBlue.copy(alpha = 0.08f) else Color.Transparent)
                    .clickable { onNavigate(item.route) }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(P01AccentBlue.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center,
                ) {
                    P01BottomIcon(
                        kind = item.iconKind,
                        tint = if (active) P01AccentBlue else P01TextSoft,
                    )
                }
                Text(
                    text = item.label,
                    color = if (active) P01AccentBlue else P01TextSoft,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun P01BottomIcon(
    kind: P01BottomIconKind,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(18.dp)) {
        val stroke = Stroke(
            width = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        when (kind) {
            P01BottomIconKind.OVERVIEW -> {
                val path = Path().apply {
                    moveTo(size.width / 2f, size.height * 0.12f)
                    lineTo(size.width * 0.64f, size.height * 0.4f)
                    lineTo(size.width * 0.92f, size.height * 0.48f)
                    lineTo(size.width * 0.7f, size.height * 0.64f)
                    lineTo(size.width * 0.76f, size.height * 0.92f)
                    lineTo(size.width / 2f, size.height * 0.78f)
                    lineTo(size.width * 0.24f, size.height * 0.92f)
                    lineTo(size.width * 0.3f, size.height * 0.64f)
                    lineTo(size.width * 0.08f, size.height * 0.48f)
                    lineTo(size.width * 0.36f, size.height * 0.4f)
                    close()
                }
                drawPath(path, tint, style = stroke)
            }

            P01BottomIconKind.VPN -> {
                drawCircle(color = tint, radius = size.minDimension * 0.38f, style = stroke)
                drawLine(tint, Offset(size.width * 0.16f, size.height / 2f), Offset(size.width * 0.84f, size.height / 2f), stroke.width, cap = StrokeCap.Round)
                drawOval(
                    color = tint,
                    topLeft = Offset(size.width * 0.3f, size.height * 0.14f),
                    size = Size(size.width * 0.4f, size.height * 0.72f),
                    style = stroke,
                )
            }

            P01BottomIconKind.WALLET -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(size.width * 0.1f, size.height * 0.22f),
                    size = Size(size.width * 0.8f, size.height * 0.56f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = stroke,
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(size.width * 0.56f, size.height * 0.34f),
                    size = Size(size.width * 0.28f, size.height * 0.24f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = stroke,
                )
                drawCircle(
                    color = tint,
                    radius = 1.8.dp.toPx(),
                    center = Offset(size.width * 0.66f, size.height * 0.46f),
                    style = Fill,
                )
            }

            P01BottomIconKind.GROWTH -> {
                drawLine(tint, Offset(size.width * 0.16f, size.height * 0.84f), Offset(size.width * 0.16f, size.height * 0.18f), stroke.width, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.16f, size.height * 0.84f), Offset(size.width * 0.86f, size.height * 0.84f), stroke.width, cap = StrokeCap.Round)
                val path = Path().apply {
                    moveTo(size.width * 0.24f, size.height * 0.68f)
                    lineTo(size.width * 0.42f, size.height * 0.48f)
                    lineTo(size.width * 0.58f, size.height * 0.58f)
                    lineTo(size.width * 0.8f, size.height * 0.28f)
                }
                drawPath(path, tint, style = stroke)
            }

            P01BottomIconKind.PROFILE -> {
                drawCircle(
                    color = tint,
                    radius = size.minDimension * 0.2f,
                    center = Offset(size.width / 2f, size.height * 0.32f),
                    style = stroke,
                )
                drawArc(
                    color = tint,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.18f, size.height * 0.42f),
                    size = Size(size.width * 0.64f, size.height * 0.44f),
                    style = stroke,
                )
            }
        }
    }
}

@Composable
fun P01FlowRow(
    horizontalGap: Dp,
    verticalGap: Dp,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(horizontalGap),
            verticalArrangement = Arrangement.spacedBy(verticalGap),
            content = { content() },
        )
    }
}

fun defaultP01Destinations(): List<P01BottomDestination> = listOf(
    P01BottomDestination("vpn_home", "总览", P01BottomIconKind.OVERVIEW),
    P01BottomDestination("plans", "VPN", P01BottomIconKind.VPN),
    P01BottomDestination("wallet_home", "钱包", P01BottomIconKind.WALLET),
    P01BottomDestination("invite_center", "增长", P01BottomIconKind.GROWTH),
    P01BottomDestination("profile", "我的", P01BottomIconKind.PROFILE),
)

private fun DrawScope.drawDecorativeDots() {
    val dots = listOf(
        Triple(0.16f, 0.18f, P01AccentBlue.copy(alpha = 0.18f)),
        Triple(0.72f, 0.3f, P01AccentLilac.copy(alpha = 0.18f)),
        Triple(0.84f, 0.74f, P01AccentCyan.copy(alpha = 0.18f)),
        Triple(0.32f, 0.88f, P01AccentBlue.copy(alpha = 0.14f)),
    )
    dots.forEach { (x, y, color) ->
        drawCircle(
            color = color,
            radius = min(size.width, size.height) * 0.0042f,
            center = Offset(size.width * x, size.height * y),
        )
    }
}

private fun DrawScope.drawScreenTexture() {
    val glowRadius = size.minDimension * 0.3f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(P01AccentLilac.copy(alpha = 0.16f), Color.Transparent),
            center = Offset(size.width * 0.82f, size.height * 0.12f),
            radius = glowRadius,
        ),
        radius = glowRadius,
        center = Offset(size.width * 0.82f, size.height * 0.12f),
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(P01AccentCyan.copy(alpha = 0.16f), Color.Transparent),
            center = Offset(size.width * 0.64f, size.height * 1.02f),
            radius = glowRadius,
        ),
        radius = glowRadius,
        center = Offset(size.width * 0.64f, size.height * 1.02f),
    )
    val pattern = listOf(
        Offset(size.width * 0.18f, size.height * 0.18f),
        Offset(size.width * 0.72f, size.height * 0.34f),
        Offset(size.width * 0.42f, size.height * 0.82f),
    )
    pattern.forEachIndexed { index, offset ->
        drawCircle(
            color = listOf(P01AccentBlue, P01AccentCyan, P01AccentLilac)[index].copy(alpha = 0.22f),
            radius = 1.6.dp.toPx(),
            center = offset,
        )
    }
}

@Composable
private fun P01StatusBar(time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = time,
            color = P01TextSoft,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(modifier = Modifier.size(width = 16.dp, height = 10.dp)) {
                val barWidth = size.width / 6f
                listOf(0.55f, 0.7f, 0.85f, 1f).forEachIndexed { index, fraction ->
                    drawRoundRect(
                        color = P01TextSoft,
                        topLeft = Offset(index * (barWidth + 2.dp.toPx()), size.height * (1f - fraction)),
                        size = Size(barWidth, size.height * fraction),
                        cornerRadius = CornerRadius(barWidth, barWidth),
                    )
                }
            }
            Canvas(modifier = Modifier.size(width = 18.dp, height = 12.dp)) {
                val stroke = Stroke(width = 1.8.dp.toPx())
                drawArc(
                    color = P01TextSoft,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset.Zero,
                    size = size,
                    style = stroke,
                )
                drawArc(
                    color = P01TextSoft,
                    startAngle = 210f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.16f, size.height * 0.18f),
                    size = Size(size.width * 0.68f, size.height * 0.64f),
                    style = stroke,
                )
            }
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.8.dp, P01TextSoft, RoundedCornerShape(6.dp))
                    .padding(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF57C76F), Color(0xFF9CE26F)))),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 4.dp)
                        .width(2.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(P01TextSoft),
                )
            }
        }
    }
}

private fun DrawScope.drawQrCorner(topLeft: Offset) {
    val outer = 44.dp.toPx()
    val inner = 22.dp.toPx()
    drawRoundRect(
        color = P01AccentNavy,
        topLeft = topLeft,
        size = Size(outer, outer),
        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
        style = Stroke(width = 8.dp.toPx()),
    )
    drawRoundRect(
        color = P01AccentNavy,
        topLeft = topLeft + Offset((outer - inner) / 2f, (outer - inner) / 2f),
        size = Size(inner, inner),
        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
        style = Fill,
    )
}
