package com.app.core.ui.effects

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Security
import androidx.compose.ui.graphics.vector.ImageVector

enum class HeaderRingLayer(
    val title: String,
    val description: String,
) {
    CoreGlow("核心光晕", "中间玻璃核与柔光底盘。"),
    InnerRing("内圈", "稳定的内层实体环。"),
    OrbitSweep("轨道弧", "一段持续旋转的高亮轨道。"),
    DashedOuter("外圈刻度", "带断点的外层刻度环。"),
    TickMarks("雷达刻线", "环周的短刻线，偏扫描控制台。"),
    SatelliteDots("卫星节点", "环周小节点绕行，偏网络/节点感。"),
    ScanArc("扫描扇面", "一段更窄的扫描弧，强调雷达感。"),
    PulseHalo("脉冲外晕", "最外层轻呼吸脉冲。"),
}

enum class HeaderRingGlyph(
    val title: String,
    val icon: ImageVector,
) {
    Wallet("钱包", Icons.Rounded.AccountBalanceWallet),
    Shield("安全", Icons.Rounded.Security),
    Network("网络", Icons.Rounded.Language),
    Market("市场", Icons.AutoMirrored.Rounded.ShowChart),
}

enum class HeaderRingPreset(
    val label: String,
    val summary: String,
    val orbitDurationMs: Int,
    val nodeCount: Int,
    val sweepAngle: Float,
    val glyph: HeaderRingGlyph,
    val layers: Set<HeaderRingLayer>,
) {
    R1(
        label = "R1 轻玻璃徽章",
        summary = "极简干净，适合多数普通标题栏。",
        orbitDurationMs = 13000,
        nodeCount = 0,
        sweepAngle = 72f,
        glyph = HeaderRingGlyph.Wallet,
        layers = setOf(
            HeaderRingLayer.CoreGlow,
            HeaderRingLayer.InnerRing,
        ),
    ),
    R2(
        label = "R2 轻轨道",
        summary = "保留精致感，同时有明确动态轨道。",
        orbitDurationMs = 9800,
        nodeCount = 0,
        sweepAngle = 92f,
        glyph = HeaderRingGlyph.Wallet,
        layers = setOf(
            HeaderRingLayer.CoreGlow,
            HeaderRingLayer.InnerRing,
            HeaderRingLayer.OrbitSweep,
            HeaderRingLayer.PulseHalo,
        ),
    ),
    R3(
        label = "R3 双轨能量环",
        summary = "更接近你现在项目想要的科技感上限。",
        orbitDurationMs = 8200,
        nodeCount = 3,
        sweepAngle = 110f,
        glyph = HeaderRingGlyph.Network,
        layers = setOf(
            HeaderRingLayer.CoreGlow,
            HeaderRingLayer.InnerRing,
            HeaderRingLayer.OrbitSweep,
            HeaderRingLayer.DashedOuter,
            HeaderRingLayer.SatelliteDots,
            HeaderRingLayer.PulseHalo,
        ),
    ),
    R4(
        label = "R4 扫描雷达",
        summary = "偏控制台/监控风，适合 VPN 或市场监控标题。",
        orbitDurationMs = 7600,
        nodeCount = 2,
        sweepAngle = 54f,
        glyph = HeaderRingGlyph.Network,
        layers = setOf(
            HeaderRingLayer.CoreGlow,
            HeaderRingLayer.InnerRing,
            HeaderRingLayer.ScanArc,
            HeaderRingLayer.TickMarks,
            HeaderRingLayer.PulseHalo,
        ),
    ),
    R5(
        label = "R5 节点矩阵",
        summary = "更像节点网络和链路状态，可读性强。",
        orbitDurationMs = 7200,
        nodeCount = 5,
        sweepAngle = 86f,
        glyph = HeaderRingGlyph.Shield,
        layers = setOf(
            HeaderRingLayer.CoreGlow,
            HeaderRingLayer.OrbitSweep,
            HeaderRingLayer.DashedOuter,
            HeaderRingLayer.TickMarks,
            HeaderRingLayer.SatelliteDots,
        ),
    ),
    R6(
        label = "R6 强科技控制环",
        summary = "层级最多，冲击力最强，适合重点页面头部。",
        orbitDurationMs = 6400,
        nodeCount = 6,
        sweepAngle = 120f,
        glyph = HeaderRingGlyph.Market,
        layers = HeaderRingLayer.entries.toSet(),
    ),
}
