package com.v2ray.ang.composeui.p2.model

enum class P2SurfaceState {
    Ready,
    Loading,
    Empty,
    Error,
    Unavailable,
}

data class P2SurfaceBanner(
    val state: P2SurfaceState = P2SurfaceState.Ready,
    val title: String = "",
    val message: String = "",
)

fun p2ReadyBanner(): P2SurfaceBanner = P2SurfaceBanner()

fun p2LoadingBanner(
    title: String = "正在加载",
    message: String = "正在从真实服务同步数据...",
): P2SurfaceBanner = P2SurfaceBanner(
    state = P2SurfaceState.Loading,
    title = title,
    message = message,
)

fun p2EmptyBanner(
    title: String,
    message: String,
): P2SurfaceBanner = P2SurfaceBanner(
    state = P2SurfaceState.Empty,
    title = title,
    message = message,
)

fun p2ErrorBanner(
    title: String = "加载失败",
    message: String,
): P2SurfaceBanner = P2SurfaceBanner(
    state = P2SurfaceState.Error,
    title = title,
    message = message,
)

fun p2UnavailableBanner(
    title: String,
    message: String,
): P2SurfaceBanner = P2SurfaceBanner(
    state = P2SurfaceState.Unavailable,
    title = title,
    message = message,
)
