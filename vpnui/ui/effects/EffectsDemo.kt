package com.cryptovpn.ui.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * CryptoVPN 视觉效果演示
 * 
 * 展示所有视觉效果组件的使用方法和效果预览
 */

@Composable
fun EffectsDemo() {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 标题
        DemoHeader()
        
        // 1. 粒子效果
        ParticleEffectsSection()
        
        // 2. 发光效果
        GlowEffectsSection()
        
        // 3. 动画效果
        AnimationEffectsSection()
        
        // 4. VPN连接状态
        ConnectionSection()
        
        // 5. 过渡动画
        TransitionSection()
        
        // 6. 特殊效果
        SpecialEffectsSection()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DemoHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CryptoVPN",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Visual Effects Demo",
            color = Color(0xFF06B6D4),
            fontSize = 18.sp
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(vertical = 16.dp),
            color = Color(0xFF374151)
        )
    }
}

// ==================== 1. 粒子效果 ====================

@Composable
private fun ParticleEffectsSection() {
    DemoSection(title = "1. Particle Effects") {
        // 星空粒子
        DemoCard(title = "Starfield Particles") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0B1020))
            ) {
                StarfieldParticles(starCount = 100)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Splash Screen",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // 数据流粒子
        DemoCard(title = "Data Flow Particles") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF111827))
            ) {
                DataFlowParticles(
                    particleCount = 30,
                    primaryColor = Color(0xFF22C55E),
                    secondaryColor = Color(0xFF10B981),
                    isConnected = true
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ConnectionStatusIndicator(
                        state = ConnectionState.CONNECTED,
                        serverName = "USA - New York"
                    )
                }
            }
        }
        
        // 能量粒子
        DemoCard(title = "Energy Particles") {
            var isActive by remember { mutableStateOf(true) }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF111827)),
                contentAlignment = Alignment.Center
            ) {
                EnergyParticles(
                    isActive = isActive,
                    energyColor = Color(0xFF1D4ED8),
                    particleCount = 20
                )
                
                Button(
                    onClick = { isActive = !isActive },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D4ED8)
                    )
                ) {
                    Text(if (isActive) "Stop" else "Start")
                }
            }
        }
        
        // 网络节点
        DemoCard(title = "Network Nodes") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF111827))
            ) {
                NetworkParticles(
                    nodeCount = 12,
                    connectionDensity = 0.35f,
                    selectedNode = 2
                )
            }
        }
    }
}

// ==================== 2. 发光效果 ====================

@Composable
private fun GlowEffectsSection() {
    DemoSection(title = "2. Glow Effects") {
        // 霓虹光晕
        DemoCard(title = "Neon Glow") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GlowEffect(
                    glowColor = Color(0xFF1D4ED8),
                    glowRadius = 25.dp,
                    intensity = 0.7f
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF1D4ED8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                
                GlowEffect(
                    glowColor = Color(0xFF22C55E),
                    glowRadius = 30.dp,
                    intensity = 0.8f
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF22C55E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                
                GlowEffect(
                    glowColor = Color(0xFFF59E0B),
                    glowRadius = 25.dp,
                    intensity = 0.7f
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFF59E0B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
        
        // 脉冲发光
        DemoCard(title = "Pulse Glow") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PulseGlowEffect(
                    glowColor = Color(0xFF22C55E),
                    minRadius = 15.dp,
                    maxRadius = 40.dp
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFF22C55E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
        
        // 边缘发光
        DemoCard(title = "Edge Glow") {
            EdgeGlowEffect(
                glowColor = Color(0xFF06B6D4),
                borderWidth = 2.dp,
                glowWidth = 12.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color(0xFF1F2937), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Premium Server",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ==================== 3. 动画效果 ====================

@Composable
private fun AnimationEffectsSection() {
    DemoSection(title = "3. Animation Effects") {
        // 呼吸动画
        DemoCard(title = "Breathing Animation") {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BreathingAnimation(
                    minScale = 0.9f,
                    maxScale = 1.1f
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF1D4ED8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VPN",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        // 旋转动画
        DemoCard(title = "Rotating Animation") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RotatingAnimation(duration = 2000) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                MultiLayerRotation(
                    size = 80f,
                    primaryColor = Color(0xFF1D4ED8),
                    secondaryColor = Color(0xFF06B6D4)
                )
            }
        }
        
        // 波纹动画
        DemoCard(title = "Ripple Animation") {
            var trigger by remember { mutableStateOf(false) }
            
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                RippleAnimation(
                    trigger = trigger,
                    rippleColor = Color(0xFF22C55E),
                    onComplete = { trigger = false }
                )
                
                Button(
                    onClick = { trigger = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22C55E)
                    )
                ) {
                    Text("Click for Ripple")
                }
            }
        }
        
        // 闪光动画
        DemoCard(title = "Shimmer Animation") {
            SkeletonShimmer(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ==================== 4. VPN连接状态 ====================

@Composable
private fun ConnectionSection() {
    var connectionState by remember { mutableStateOf(ConnectionState.DISCONNECTED) }
    
    DemoSection(title = "4. VPN Connection") {
        // 连接可视化
        DemoCard(title = "Connection Visualizer") {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ConnectionVisualizer(
                    state = connectionState,
                    size = 150f
                )
            }
        }
        
        // 连接按钮
        DemoCard(title = "Connection Button") {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ConnectionButtonAnimation(
                    state = connectionState,
                    onClick = {
                        connectionState = when (connectionState) {
                            ConnectionState.DISCONNECTED -> ConnectionState.CONNECTING
                            ConnectionState.CONNECTING -> ConnectionState.CONNECTED
                            ConnectionState.CONNECTED -> ConnectionState.DISCONNECTED
                        }
                    }
                )
            }
        }
        
        // 状态指示器
        DemoCard(title = "Status Indicator") {
            ConnectionStatusIndicator(
                state = connectionState,
                serverName = if (connectionState == ConnectionState.CONNECTED) "USA - New York" else "",
                duration = if (connectionState == ConnectionState.CONNECTED) "00:05:32" else ""
            )
        }
        
        // 状态切换按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { connectionState = ConnectionState.DISCONNECTED },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B7280)
                )
            ) {
                Text("Disconnect")
            }
            Button(
                onClick = { connectionState = ConnectionState.CONNECTING },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF59E0B)
                )
            ) {
                Text("Connecting")
            }
            Button(
                onClick = { connectionState = ConnectionState.CONNECTED },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E)
                )
            ) {
                Text("Connect")
            }
        }
    }
}

// ==================== 5. 过渡动画 ====================

@Composable
private fun TransitionSection() {
    DemoSection(title = "5. Transition Animations") {
        // 淡入淡出
        DemoCard(title = "Fade Transition") {
            var visible by remember { mutableStateOf(true) }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FadeTransition(visible = visible) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF1D4ED8), RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { visible = !visible }) {
                    Text(if (visible) "Hide" else "Show")
                }
            }
        }
        
        // 滑动过渡
        DemoCard(title = "Slide Transition") {
            var visible by remember { mutableStateOf(true) }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SlideTransition(
                    visible = visible,
                    direction = SlideDirection.Right
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF22C55E), RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { visible = !visible }) {
                    Text(if (visible) "Hide" else "Show")
                }
            }
        }
        
        // 列表动画
        DemoCard(title = "List Animation") {
            Column {
                repeat(3) { index ->
                    AnimatedListItem(index = index) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1F2937)
                            )
                        ) {
                            Text(
                                text = "Server ${index + 1}",
                                modifier = Modifier.padding(12.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== 6. 特殊效果 ====================

@Composable
private fun SpecialEffectsSection() {
    DemoSection(title = "6. Special Effects") {
        // 渐变背景
        DemoCard(title = "Gradient Background") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                GradientBackground(
                    gradientType = GradientType.AnimatedLinear,
                    colors = listOf(
                        Color(0xFF0B1020),
                        Color(0xFF1D4ED8),
                        Color(0xFF0B1020)
                    )
                )
            }
        }
        
        // 进度动画
        DemoCard(title = "Progress Animation") {
            var progress by remember { mutableStateOf(0.5f) }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressAnimation(
                    progress = progress,
                    color = Color(0xFF22C55E),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressAnimation(
                    progress = progress,
                    color = Color(0xFF1D4ED8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { progress = (progress - 0.2f).coerceIn(0f, 1f) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("-20%")
                    }
                    Button(
                        onClick = { progress = (progress + 0.2f).coerceIn(0f, 1f) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+20%")
                    }
                }
            }
        }
        
        // 波浪动画
        DemoCard(title = "Wave Animation") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0B1020))
            ) {
                WaveAnimation(
                    waveCount = 2,
                    waveColor = Color(0xFF1D4ED8),
                    amplitude = 20f
                )
            }
        }
    }
}

// ==================== 辅助组件 ====================

@Composable
private fun DemoSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = Color(0xFF06B6D4),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = Color(0xFF374151)
        )
    }
}

@Composable
private fun DemoCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

// ==================== 预览 ====================

@Preview(device = "id:pixel_5")
@Composable
private fun EffectsDemoPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF1D4ED8),
            secondary = Color(0xFF06B6D4),
            background = Color(0xFF0B1020),
            surface = Color(0xFF1F2937)
        )
    ) {
        EffectsDemo()
    }
}

/**
 * 使用说明：
 * 
 * 1. 粒子效果
 *    - StarfieldParticles: 启动页星空背景
 *    - DataFlowParticles: VPN数据流动画
 *    - EnergyParticles: 按钮能量效果
 *    - NetworkParticles: 服务器节点选择
 * 
 * 2. 发光效果
 *    - GlowEffect: 基础霓虹光晕
 *    - PulseGlowEffect: 脉冲呼吸光效
 *    - EdgeGlowEffect: 卡片边缘发光
 * 
 * 3. 动画效果
 *    - BreathingAnimation: Logo呼吸动画
 *    - RotatingAnimation: 加载旋转
 *    - WaveAnimation: 波浪背景
 *    - RippleAnimation: 点击波纹
 *    - ShimmerAnimation: 骨架屏闪光
 * 
 * 4. VPN连接
 *    - ConnectionVisualizer: 地球连接可视化
 *    - ConnectionButtonAnimation: 连接按钮动画
 *    - ConnectionStatusIndicator: 状态指示器
 * 
 * 5. 过渡动画
 *    - FadeTransition: 淡入淡出
 *    - SlideTransition: 滑动过渡
 *    - ScaleTransition: 缩放过渡
 *    - AnimatedListItem: 列表项动画
 * 
 * 6. 特殊效果
 *    - GradientBackground: 渐变背景
 *    - AnimatedBackground: 动态背景
 *    - CircularProgressAnimation: 圆形进度
 *    - LinearProgressAnimation: 线性进度
 */