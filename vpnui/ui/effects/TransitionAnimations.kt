package com.cryptovpn.ui.effects

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 页面过渡动画合集
 * 
 * 包含：
 * 1. FadeTransition - 淡入淡出过渡
 * 2. SlideTransition - 滑动过渡
 * 3. ScaleTransition - 缩放过渡
 * 4. SharedElementTransition - 共享元素过渡
 * 5. CombinedTransition - 组合过渡
 */

/**
 * 淡入淡出过渡
 * 
 * @param visible 是否可见
 * @param durationMillis 动画时长
 * @param content 内容
 */
@Composable
fun FadeTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = 300,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(durationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis))
    ) {
        content()
    }
}

/**
 * 滑动过渡
 * 
 * @param visible 是否可见
 * @param direction 滑动方向
 * @param durationMillis 动画时长
 * @param content 内容
 */
@Composable
fun SlideTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    direction: SlideDirection = SlideDirection.Up,
    durationMillis: Int = 300,
    content: @Composable () -> Unit
) {
    val (enter, exit) = when (direction) {
        SlideDirection.Up -> Pair(
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis)
            ),
            slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis)
            )
        )
        SlideDirection.Down -> Pair(
            slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis)
            ),
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis)
            )
        )
        SlideDirection.Left -> Pair(
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis)
            ),
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis)
            )
        )
        SlideDirection.Right -> Pair(
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis)
            ),
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis)
            )
        )
    }
    
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit
    ) {
        content()
    }
}

/**
 * 滑动方向枚举
 */
enum class SlideDirection {
    Up, Down, Left, Right
}

/**
 * 缩放过渡
 * 
 * @param visible 是否可见
 * @param durationMillis 动画时长
 * @param content 内容
 */
@Composable
fun ScaleTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = 300,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        ),
        exit = scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        )
    ) {
        content()
    }
}

/**
 * 组合过渡 - 淡入+缩放+滑动
 * 
 * @param visible 是否可见
 * @param direction 滑动方向
 * @param durationMillis 动画时长
 * @param content 内容
 */
@Composable
fun CombinedTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    direction: SlideDirection = SlideDirection.Up,
    durationMillis: Int = 400,
    content: @Composable () -> Unit
) {
    val (slideEnter, slideExit) = when (direction) {
        SlideDirection.Up -> Pair(
            slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(durationMillis)
            ),
            slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = tween(durationMillis)
            )
        )
        SlideDirection.Down -> Pair(
            slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(durationMillis)
            ),
            slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(durationMillis)
            )
        )
        SlideDirection.Left -> Pair(
            slideInHorizontally(
                initialOffsetX = { it / 2 },
                animationSpec = tween(durationMillis)
            ),
            slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(durationMillis)
            )
        )
        SlideDirection.Right -> Pair(
            slideInHorizontally(
                initialOffsetX = { -it / 2 },
                animationSpec = tween(durationMillis)
            ),
            slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(durationMillis)
            )
        )
    }
    
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideEnter + fadeIn(tween(durationMillis)) + 
                scaleIn(initialScale = 0.9f, animationSpec = tween(durationMillis)),
        exit = slideExit + fadeOut(tween(durationMillis)) + 
               scaleOut(targetScale = 0.9f, animationSpec = tween(durationMillis))
    ) {
        content()
    }
}

/**
 * 页面切换容器
 * 
 * @param currentPage 当前页面索引
 * @param pageCount 页面数量
 * @param transitionType 过渡类型
 * @param pageContent 页面内容
 */
@Composable
fun PageTransitionContainer(
    modifier: Modifier = Modifier,
    currentPage: Int,
    pageCount: Int,
    transitionType: TransitionType = TransitionType.Slide,
    pageContent: @Composable (page: Int) -> Unit
) {
    Box(modifier = modifier) {
        for (page in 0 until pageCount) {
            val isVisible = page == currentPage
            
            when (transitionType) {
                TransitionType.Fade -> {
                    FadeTransition(visible = isVisible) {
                        pageContent(page)
                    }
                }
                TransitionType.Slide -> {
                    val direction = if (page < currentPage) SlideDirection.Left else SlideDirection.Right
                    SlideTransition(
                        visible = isVisible,
                        direction = direction
                    ) {
                        pageContent(page)
                    }
                }
                TransitionType.Scale -> {
                    ScaleTransition(visible = isVisible) {
                        pageContent(page)
                    }
                }
                TransitionType.Combined -> {
                    val direction = if (page < currentPage) SlideDirection.Left else SlideDirection.Right
                    CombinedTransition(
                        visible = isVisible,
                        direction = direction
                    ) {
                        pageContent(page)
                    }
                }
            }
        }
    }
}

/**
 * 过渡类型枚举
 */
enum class TransitionType {
    Fade, Slide, Scale, Combined
}

/**
 * 交叉淡入淡出切换器
 * 
 * 用于在两个内容之间平滑切换
 */
@Composable
fun <T> CrossfadeSwitcher(
    targetState: T,
    modifier: Modifier = Modifier,
    durationMillis: Int = 300,
    content: @Composable (T) -> Unit
) {
    Crossfade(
        targetState = targetState,
        modifier = modifier,
        animationSpec = tween(durationMillis)
    ) { state ->
        content(state)
    }
}

/**
 * 内容展开/收起动画
 * 
 * @param expanded 是否展开
 * @param content 内容
 */
@Composable
fun ExpandableContent(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        modifier = modifier,
        enter = expandVertically(
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(300)),
        exit = shrinkVertically(
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(300))
    ) {
        content()
    }
}

/**
 * 列表项进入动画
 * 
 * @param index 列表项索引
 * @param content 内容
 */
@Composable
fun AnimatedListItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(300, delayMillis = index * 50)
        ) + fadeIn(tween(300, delayMillis = index * 50))
    ) {
        content()
    }
}

// ==================== 预览 ====================

@Preview(device = "id:pixel_5")
@Composable
private fun FadeTransitionPreview() {
    var visible by remember { mutableStateOf(true) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FadeTransition(visible = visible) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color(0xFF1D4ED8), MaterialTheme.shapes.medium)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(onClick = { visible = !visible }) {
                Text(if (visible) "Hide" else "Show")
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun SlideTransitionPreview() {
    var visible by remember { mutableStateOf(true) }
    var direction by remember { mutableStateOf(SlideDirection.Right) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SlideTransition(
                visible = visible,
                direction = direction
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color(0xFF22C55E), MaterialTheme.shapes.medium)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { 
                    direction = SlideDirection.Left
                    visible = !visible 
                }) {
                    Text("Left")
                }
                Button(onClick = { 
                    direction = SlideDirection.Right
                    visible = !visible 
                }) {
                    Text("Right")
                }
                Button(onClick = { 
                    direction = SlideDirection.Up
                    visible = !visible 
                }) {
                    Text("Up")
                }
                Button(onClick = { 
                    direction = SlideDirection.Down
                    visible = !visible 
                }) {
                    Text("Down")
                }
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun ScaleTransitionPreview() {
    var visible by remember { mutableStateOf(true) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ScaleTransition(visible = visible) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color(0xFFF59E0B), MaterialTheme.shapes.medium)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(onClick = { visible = !visible }) {
                Text(if (visible) "Hide" else "Show")
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun PageTransitionContainerPreview() {
    var currentPage by remember { mutableStateOf(0) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PageTransitionContainer(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(200.dp),
                currentPage = currentPage,
                pageCount = 3,
                transitionType = TransitionType.Combined
            ) { page ->
                val colors = listOf(Color(0xFF1D4ED8), Color(0xFF22C55E), Color(0xFFF59E0B))
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors[page], MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Page ${page + 1}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { if (currentPage > 0) currentPage-- },
                    enabled = currentPage > 0
                ) {
                    Text("Previous")
                }
                Button(
                    onClick = { if (currentPage < 2) currentPage++ },
                    enabled = currentPage < 2
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun CrossfadeSwitcherPreview() {
    var state by remember { mutableStateOf(0) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CrossfadeSwitcher(targetState = state) { currentState ->
                val colors = listOf(Color(0xFF1D4ED8), Color(0xFF22C55E), Color(0xFFF59E0B))
                val texts = listOf("Home", "Settings", "Profile")
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(colors[currentState], MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = texts[currentState],
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    Button(onClick = { state = index }) {
                        Text("${index + 1}")
                    }
                }
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun AnimatedListPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
            .padding(16.dp)
    ) {
        Column {
            repeat(5) { index ->
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
                            text = "Item ${index + 1}",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * 使用示例：
 * 
 * ```kotlin
 * // 淡入淡出
 * FadeTransition(visible = isVisible) {
 *     Content()
 * }
 * 
 * // 滑动过渡
 * SlideTransition(
 *     visible = isVisible,
 *     direction = SlideDirection.Up
 * ) {
 *     Content()
 * }
 * 
 * // 页面切换
 * PageTransitionContainer(
 *     currentPage = currentPage,
 *     pageCount = 3,
 *     transitionType = TransitionType.Combined
 * ) { page ->
 *     PageContent(page)
 * }
 * 
 * // 交叉淡入淡出
 * CrossfadeSwitcher(targetState = currentTab) { tab ->
 *     TabContent(tab)
 * }
 * ```
 */