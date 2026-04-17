package com.app.core.ui.effects

enum class EffectToggle(
    val title: String,
    val description: String,
) {
    ParticleDrift("粒子漂浮", "小粒子缓慢漂浮，形成基础科技氛围。"),
    ParticleLinks("粒子连线", "粒子之间增加细线连接，强化网络感。"),
    EnergyOrb("右上能量球", "右上角呼吸感光球和冷色光晕。"),
    GridScan("流动网格", "让背景网格出现轻微位移和扫描感。"),
    CardEntrance("卡片入场", "卡片淡入和上浮，避免页面太平。"),
    ButtonPulse("按钮脉冲", "主按钮出现轻微呼吸放大和外发光。"),
    BottomBarMotion("底栏动效", "预览紧凑底栏高度和点击动态反馈。"),
    ChartDraw("图表绘制", "线图从左到右渐进绘制。"),
}

enum class EffectLabPreset(
    val label: String,
    val summary: String,
    val particleCount: Int,
    val orbitDurationMs: Int,
    val enabled: Set<EffectToggle>,
) {
    P1(
        label = "P1 轻科技",
        summary = "干净克制，偏高级感。",
        particleCount = 10,
        orbitDurationMs = 18000,
        enabled = setOf(
            EffectToggle.ParticleDrift,
            EffectToggle.EnergyOrb,
            EffectToggle.CardEntrance,
            EffectToggle.BottomBarMotion,
        ),
    ),
    P2(
        label = "P2 标准科技",
        summary = "推荐默认组合，兼顾产品感和科技感。",
        particleCount = 24,
        orbitDurationMs = 13000,
        enabled = setOf(
            EffectToggle.ParticleDrift,
            EffectToggle.ParticleLinks,
            EffectToggle.EnergyOrb,
            EffectToggle.CardEntrance,
            EffectToggle.BottomBarMotion,
            EffectToggle.ChartDraw,
        ),
    ),
    P3(
        label = "P3 强科技",
        summary = "密度更高，更偏赛博科技风。",
        particleCount = 40,
        orbitDurationMs = 8500,
        enabled = setOf(
            EffectToggle.ParticleDrift,
            EffectToggle.ParticleLinks,
            EffectToggle.EnergyOrb,
            EffectToggle.GridScan,
            EffectToggle.CardEntrance,
            EffectToggle.ButtonPulse,
            EffectToggle.BottomBarMotion,
            EffectToggle.ChartDraw,
        ),
    ),
    P4(
        label = "P4 高级极简",
        summary = "弱化粒子，突出玻璃层次和精致结构。",
        particleCount = 6,
        orbitDurationMs = 21000,
        enabled = setOf(
            EffectToggle.EnergyOrb,
            EffectToggle.CardEntrance,
            EffectToggle.BottomBarMotion,
        ),
    ),
}
