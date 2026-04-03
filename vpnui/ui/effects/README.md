# CryptoVPN 视觉效果库

电影级科幻视觉效果组件库，专为 CryptoVPN Android App 设计。

## 色彩系统

```kotlin
// 背景色
Background Deepest:   #0B1020  // 启动页、最深背景
Background Primary:   #111827  // 页面主背景
Background Secondary: #1F2937  // 卡片背景

// 功能色
Primary:  #1D4ED8  // 主色（蓝色）
Success:  #22C55E  // VPN已连接（绿色）
Warning:  #F59E0B  // 连接中（橙色）
Error:    #EF4444  // 错误（红色）
```

## 文件列表

### 1. 粒子效果系统

| 文件名 | 功能 | 使用场景 |
|--------|------|----------|
| `StarfieldParticles.kt` | 星空粒子背景 | 启动页、Splash Screen |
| `DataFlowParticles.kt` | 数据流粒子 | VPN连接状态页面 |
| `EnergyParticles.kt` | 能量粒子爆发 | 按钮点击、连接成功 |
| `NetworkParticles.kt` | 网络节点粒子 | 服务器区域选择页 |

### 2. 发光效果

| 文件名 | 功能 | 使用场景 |
|--------|------|----------|
| `GlowEffects.kt` | 霓虹光晕、脉冲发光、边缘发光 | 按钮、卡片、重要元素 |

### 3. 动画效果

| 文件名 | 功能 | 使用场景 |
|--------|------|----------|
| `AnimationEffects.kt` | 呼吸、旋转、波浪、波纹、闪光 | Logo、加载、骨架屏 |

### 4. VPN连接状态

| 文件名 | 功能 | 使用场景 |
|--------|------|----------|
| `ConnectionVisualizer.kt` | 地球可视化、连接按钮、状态指示 | 主连接页面 |

### 5. 过渡动画

| 文件名 | 功能 | 使用场景 |
|--------|------|----------|
| `TransitionAnimations.kt` | 淡入淡出、滑动、缩放、组合过渡 | 页面切换、列表动画 |

### 6. 特殊效果

| 文件名 | 功能 | 使用场景 |
|--------|------|----------|
| `SpecialEffects.kt` | 渐变背景、动态背景、进度、倒计时 | 全局背景、加载进度 |

### 7. 演示

| 文件名 | 功能 |
|--------|------|
| `EffectsDemo.kt` | 所有效果的综合演示 |

## 快速开始

### 1. 星空粒子背景（启动页）

```kotlin
@Composable
fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // 深色背景
        GradientBackground(
            gradientType = GradientType.Radial,
            colors = listOf(Color(0xFF111827), Color(0xFF0B1020))
        )
        
        // 星空粒子
        StarfieldParticles(
            starCount = 200,
            shootingStarEnabled = true
        )
        
        // Logo
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BreathingAnimation {
                Text(
                    text = "CryptoVPN",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
```

### 2. VPN连接页面

```kotlin
@Composable
fun ConnectionScreen() {
    val connectionState by viewModel.connectionState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 动态背景
        AnimatedBackground(
            primaryColor = when (connectionState) {
                ConnectionState.CONNECTED -> Color(0xFF22C55E)
                ConnectionState.CONNECTING -> Color(0xFFF59E0B)
                else -> Color(0xFF1D4ED8)
            }
        )
        
        // 数据流粒子
        if (connectionState != ConnectionState.DISCONNECTED) {
            DataFlowParticles(
                particleCount = 40,
                isConnected = connectionState == ConnectionState.CONNECTED
            )
        }
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 连接可视化
            ConnectionVisualizer(
                state = connectionState,
                size = 200f
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 连接按钮
            ConnectionButtonAnimation(
                state = connectionState,
                onClick = { viewModel.toggleConnection() }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 状态指示
            ConnectionStatusIndicator(
                state = connectionState,
                serverName = "USA - New York",
                duration = "00:05:32"
            )
        }
    }
}
```

### 3. 服务器选择页面

```kotlin
@Composable
fun ServerSelectionScreen() {
    var selectedRegion by remember { mutableStateOf(-1) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 网络节点背景
        NetworkParticles(
            nodeCount = 15,
            connectionDensity = 0.35f,
            selectedNode = selectedRegion
        )
        
        // 区域列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(regions) { index, region ->
                AnimatedListItem(index = index) {
                    EdgeGlowEffect(
                        glowColor = if (selectedRegion == index) 
                            Color(0xFF06B6D4) else Color.Transparent
                    ) {
                        ServerRegionCard(
                            region = region,
                            isSelected = selectedRegion == index,
                            onClick = { selectedRegion = index }
                        )
                    }
                }
            }
        }
    }
}
```

## 性能优化建议

### 1. 粒子效果优化

```kotlin
// 根据设备性能调整粒子数量
val particleCount = if (isLowEndDevice) 50 else 200

StarfieldParticles(starCount = particleCount)
```

### 2. 动画性能

```kotlin
// 使用 rememberInfiniteTransition 避免重复创建
val infiniteTransition = rememberInfiniteTransition(label = "animation")

// 使用 graphicsLayer 进行变换
modifier = Modifier.graphicsLayer {
    rotationZ = rotation
    scaleX = scale
    scaleY = scale
}
```

### 3. 避免过度重绘

```kotlin
// 使用 drawBehind 在背景层绘制
defaultModifier = Modifier.drawBehind {
    // 绘制发光效果
}

// 使用 Canvas 的 clip 限制绘制区域
Canvas(
    modifier = Modifier.clip(RoundedCornerShape(12.dp))
) {
    // 绘制内容
}
```

### 4. 条件渲染

```kotlin
// 仅在需要时渲染复杂效果
if (connectionState != ConnectionState.DISCONNECTED) {
    DataFlowParticles(...)
}
```

## 最佳实践

### 1. 状态管理

```kotlin
// 使用枚举管理连接状态
enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED
}

// 根据状态自动切换颜色和动画
val (color, animation) = when (state) {
    ConnectionState.DISCONNECTED -> Pair(Color.Gray, null)
    ConnectionState.CONNECTING -> Pair(Color(0xFFF59E0B), rotatingAnimation)
    ConnectionState.CONNECTED -> Pair(Color(0xFF22C55E), pulseAnimation)
}
```

### 2. 可访问性

```kotlin
// 为动画添加有意义的描述
Canvas(
    modifier = Modifier.semantics {
        contentDescription = "VPN connection visualization"
    }
)
```

### 3. 主题适配

```kotlin
// 使用 MaterialTheme 颜色
val primaryColor = MaterialTheme.colorScheme.primary
val successColor = MaterialTheme.colorScheme.tertiary
```

## 文件依赖关系

```
EffectsDemo.kt
    ├── StarfieldParticles.kt
    ├── DataFlowParticles.kt
    ├── EnergyParticles.kt
    ├── NetworkParticles.kt
    ├── GlowEffects.kt
    ├── AnimationEffects.kt
    ├── ConnectionVisualizer.kt
    ├── TransitionAnimations.kt
    └── SpecialEffects.kt
```

## 版本信息

- **Version**: 1.0.0
- **Compose Version**: 1.5.0+
- **Kotlin Version**: 1.9.0+
- **Min SDK**: 24
- **Target SDK**: 34

## 许可证

Copyright © 2024 CryptoVPN. All rights reserved.