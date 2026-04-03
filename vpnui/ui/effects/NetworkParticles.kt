package com.cryptovpn.ui.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random

/**
 * 网络节点粒子效果 - 用于区域选择页面
 * 
 * 特点：
 * - 模拟全球网络节点分布
 * - 节点之间有连接线表示网络连接
 * - 数据包在节点间传输
 * - 节点有呼吸发光效果
 * - 支持选中状态高亮
 * 
 * @param nodeCount 节点数量，默认15
 * @param connectionDensity 连接密度 0-1，默认0.3
 * @param primaryColor 主色调
 * @param isActive 是否激活动画
 * @param selectedNode 选中节点索引，-1表示无选中
 */
@Composable
fun NetworkParticles(
    modifier: Modifier = Modifier,
    nodeCount: Int = 15,
    connectionDensity: Float = 0.3f,
    primaryColor: Color = Color(0xFF1D4ED8),
    secondaryColor: Color = Color(0xFF06B6D4),
    isActive: Boolean = true,
    selectedNode: Int = -1
) {
    val infiniteTransition = rememberInfiniteTransition(label = "network")
    
    // 呼吸动画
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    
    // 数据流动画
    val dataFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dataFlow"
    )
    
    // 生成网络节点
    val nodes = remember {
        List(nodeCount) { index ->
            NetworkNode(
                id = index,
                x = Random.nextFloat() * 0.8f + 0.1f,
                y = Random.nextFloat() * 0.8f + 0.1f,
                size = Random.nextFloat() * 8f + 6f,
                pulseSpeed = Random.nextFloat() * 1.5f + 0.5f,
                pulsePhase = Random.nextFloat() * 360f,
                connections = mutableListOf()
            )
        }
    }
    
    // 生成节点连接
    remember {
        nodes.forEachIndexed { index, node ->
            val connectionCount = (nodes.size * connectionDensity).toInt()
            val potentialConnections = nodes.filter { it.id != node.id }
                .sortedBy { distance(node, it) }
                .take(connectionCount)
            
            potentialConnections.forEach { target ->
                if (Random.nextFloat() < connectionDensity) {
                    node.connections.add(target.id)
                }
            }
        }
    }
    
    // 生成数据包
    var dataPackets by remember { mutableStateOf<List<DataPacket>>(emptyList()) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            while (true) {
                // 随机生成数据包
                val sourceNode = nodes.random()
                val targetNode = nodes.filter { it.id != sourceNode.id }.random()
                
                dataPackets = dataPackets + DataPacket(
                    sourceId = sourceNode.id,
                    targetId = targetNode.id,
                    progress = 0f,
                    speed = Random.nextFloat() * 0.02f + 0.01f,
                    size = Random.nextFloat() * 3f + 2f
                )
                
                delay(Random.nextLong(300, 800))
            }
        }
    }
    
    // 更新数据包位置
    LaunchedEffect(dataPackets, isActive) {
        while (isActive && dataPackets.isNotEmpty()) {
            delay(16)
            dataPackets = dataPackets.mapNotNull { packet ->
                val newProgress = packet.progress + packet.speed
                if (newProgress < 1f) {
                    packet.copy(progress = newProgress)
                } else null
            }
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // 绘制连接线
        nodes.forEach { node ->
            node.connections.forEach { targetId ->
                val target = nodes.find { it.id == targetId }
                target?.let {
                    drawConnection(
                        node, it, width, height, primaryColor, breathe
                    )
                }
            }
        }
        
        // 绘制数据包
        dataPackets.forEach { packet ->
            val source = nodes.find { it.id == packet.sourceId }
            val target = nodes.find { it.id == packet.targetId }
            if (source != null && target != null) {
                drawDataPacket(packet, source, target, width, height, secondaryColor)
            }
        }
        
        // 绘制节点
        nodes.forEachIndexed { index, node ->
            val isSelected = index == selectedNode
            drawNetworkNode(
                node = node,
                width = width,
                height = height,
                primaryColor = if (isSelected) secondaryColor else primaryColor,
                breathe = breathe,
                isSelected = isSelected
            )
        }
    }
}

/**
 * 网络节点数据类
 */
private data class NetworkNode(
    val id: Int,
    val x: Float,
    val y: Float,
    val size: Float,
    val pulseSpeed: Float,
    val pulsePhase: Float,
    val connections: MutableList<Int>
)

/**
 * 数据包数据类
 */
private data class DataPacket(
    val sourceId: Int,
    val targetId: Int,
    val progress: Float,
    val speed: Float,
    val size: Float
)

/**
 * 计算两个节点间距离
 */
private fun distance(node1: NetworkNode, node2: NetworkNode): Float {
    return sqrt((node1.x - node2.x).pow(2) + (node1.y - node2.y).pow(2))
}

private fun Float.pow(n: Int): Float = this.toDouble().pow(n).toFloat()

/**
 * 绘制节点连接
 */
private fun DrawScope.drawConnection(
    node1: NetworkNode,
    node2: NetworkNode,
    width: Float,
    height: Float,
    color: Color,
    breathe: Float
) {
    val x1 = node1.x * width
    val y1 = node1.y * height
    val x2 = node2.x * width
    val y2 = node2.y * height
    
    val alpha = 0.2f + 0.1f * breathe
    
    drawLine(
        color = color.copy(alpha = alpha),
        start = Offset(x1, y1),
        end = Offset(x2, y2),
        strokeWidth = 1f
    )
}

/**
 * 绘制数据包
 */
private fun DrawScope.drawDataPacket(
    packet: DataPacket,
    source: NetworkNode,
    target: NetworkNode,
    width: Float,
    height: Float,
    color: Color
) {
    val x1 = source.x * width
    val y1 = source.y * height
    val x2 = target.x * width
    val y2 = target.y * height
    
    val x = x1 + (x2 - x1) * packet.progress
    val y = y1 + (y2 - y1) * packet.progress
    
    // 数据包主体
    drawCircle(
        color = color.copy(alpha = 0.9f),
        radius = packet.size,
        center = Offset(x, y)
    )
    
    // 光晕
    drawCircle(
        color = color.copy(alpha = 0.3f),
        radius = packet.size * 2f,
        center = Offset(x, y)
    )
}

/**
 * 绘制网络节点
 */
private fun DrawScope.drawNetworkNode(
    node: NetworkNode,
    width: Float,
    height: Float,
    primaryColor: Color,
    breathe: Float,
    isSelected: Boolean
) {
    val x = node.x * width
    val y = node.y * height
    
    // 计算脉冲
    val pulse = 0.7f + 0.3f * sin((breathe * 360f + node.pulsePhase) * Math.PI / 180f).toFloat()
    val glowRadius = if (isSelected) node.size * 4f else node.size * 2.5f * pulse
    
    // 外层光晕
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = if (isSelected) 0.5f else 0.3f * pulse),
                primaryColor.copy(alpha = 0.1f * pulse),
                Color.Transparent
            ),
            center = Offset(x, y),
            radius = glowRadius
        ),
        radius = glowRadius,
        center = Offset(x, y)
    )
    
    // 节点主体
    drawCircle(
        color = primaryColor.copy(alpha = 0.9f),
        radius = node.size,
        center = Offset(x, y)
    )
    
    // 内高光
    drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = node.size * 0.4f,
        center = Offset(x - node.size * 0.2f, y - node.size * 0.2f)
    )
    
    // 选中状态指示器
    if (isSelected) {
        drawCircle(
            color = primaryColor.copy(alpha = 0.8f),
            radius = node.size + 8f,
            center = Offset(x, y),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
}

/**
 * 预览 - 网络节点效果
 */
@Preview(device = "id:pixel_5")
@Composable
private fun NetworkParticlesPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
    ) {
        NetworkParticles(
            nodeCount = 15,
            connectionDensity = 0.4f,
            primaryColor = Color(0xFF1D4ED8),
            secondaryColor = Color(0xFF06B6D4),
            isActive = true,
            selectedNode = 3
        )
        
        // 覆盖层文字
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Select Server Region",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 导入需要的组件
private val Int.sp: androidx.compose.ui.unit.TextUnit
    get() = androidx.compose.ui.unit.sp(this.toFloat())

private val Int.dp: androidx.compose.ui.unit.Dp
    get() = androidx.compose.ui.unit.Dp(this.toFloat())

/**
 * 使用示例：
 * 
 * ```kotlin
 * // 区域选择页面
 * Box(modifier = Modifier.fillMaxSize()) {
 *     // 背景
 *     BackgroundPrimary()
 *     
 *     // 网络节点效果
 *     NetworkParticles(
 *         nodeCount = 20,
 *         connectionDensity = 0.35f,
 *         primaryColor = Color(0xFF1D4ED8),
 *         selectedNode = selectedRegionIndex
 *     )
 *     
 *     // 区域列表
 *     RegionList(
 *         regions = regions,
 *         selectedIndex = selectedRegionIndex,
 *         onSelect = { selectedRegionIndex = it }
 *     )
 * }
 * ```
 */