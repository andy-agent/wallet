# WebSocket 客户端 SDK 文档

## 概述

WebSocket 实时推送用于替代轮询机制，向客户端推送订单状态变更。

## 连接地址

```
ws://{server}/client/v1/ws/orders/{order_id}?client_token={token}
```

### 参数

- `order_id` (路径参数): 订单 ID
- `client_token` (查询参数, 可选): 客户端认证 Token

## 消息格式

### 服务器推送

#### 1. 连接成功
```json
{
  "event": "connected",
  "order_id": "xxx",
  "timestamp": "2026-04-01T12:00:00Z",
  "message": "WebSocket 连接成功"
}
```

#### 2. 订单状态变更
```json
{
  "event": "order_status_changed",
  "order_id": "xxx",
  "status": "paid_success",
  "timestamp": "2026-04-01T12:00:00Z",
  "data": {
    "tx_hash": "xxx",
    "confirm_count": 12,
    "paid_at": "2026-04-01T11:55:00Z"
  }
}
```

状态列表：
- `pending_payment`: 待支付
- `seen_onchain`: 已发现交易
- `confirming`: 确认中
- `paid_success`: 已确认且金额匹配
- `fulfilled`: 已完成（已开通账号）
- `expired`: 超时
- `underpaid`: 少付
- `overpaid`: 多付
- `failed`: 支付校验失败

#### 3. 服务器心跳
```json
{
  "event": "ping",
  "timestamp": "2026-04-01T12:00:00Z"
}
```

#### 4. 错误消息
```json
{
  "event": "error",
  "error_code": "INVALID_JSON",
  "message": "无效的 JSON 格式",
  "timestamp": "2026-04-01T12:00:00Z"
}
```

### 客户端发送

#### 1. 心跳响应
```json
{"event": "pong"}
```

或主动发送心跳：
```json
{"event": "ping"}
```

## Android 客户端示例

### Kotlin 实现

```kotlin
import okhttp3.*
import kotlinx.coroutines.*
import org.json.JSONObject

class OrderWebSocketClient(
    private val serverUrl: String,
    private val orderId: String,
    private val clientToken: String? = null
) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS) // 自动心跳
        .build()
    
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private var isActive = false
    
    // 回调接口
    interface Listener {
        fun onConnected()
        fun onOrderStatusChanged(status: String, data: JSONObject)
        fun onDisconnected()
        fun onError(error: String)
    }
    
    var listener: Listener? = null
    
    fun connect() {
        isActive = true
        val wsUrl = buildWebSocketUrl()
        val request = Request.Builder().url(wsUrl).build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                reconnectAttempts = 0
                listener?.onConnected()
            }
            
            override fun onMessage(ws: WebSocket, text: String) {
                handleMessage(text)
            }
            
            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                ws.close(1000, null)
            }
            
            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                if (isActive) {
                    scheduleReconnect()
                }
                listener?.onDisconnected()
            }
            
            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                listener?.onError(t.message ?: "Unknown error")
                if (isActive) {
                    scheduleReconnect()
                }
            }
        })
    }
    
    private fun buildWebSocketUrl(): String {
        val base = if (serverUrl.startsWith("https")) {
            serverUrl.replace("https", "wss")
        } else {
            serverUrl.replace("http", "ws")
        }
        val tokenParam = clientToken?.let { "?client_token=$it" } ?: ""
        return "$base/client/v1/ws/orders/$orderId$tokenParam"
    }
    
    private fun handleMessage(text: String) {
        try {
            val json = JSONObject(text)
            when (json.optString("event")) {
                "ping" -> {
                    // 回复 pong
                    webSocket?.send("""{"event":"pong"}""")
                }
                "order_status_changed" -> {
                    val status = json.getString("status")
                    val data = json.optJSONObject("data") ?: JSONObject()
                    listener?.onOrderStatusChanged(status, data)
                }
                "connected" -> {
                    // 连接成功确认
                }
                "error" -> {
                    listener?.onError(json.optString("message"))
                }
            }
        } catch (e: Exception) {
            listener?.onError("Parse error: ${e.message}")
        }
    }
    
    private fun scheduleReconnect() {
        if (reconnectAttempts >= maxReconnectAttempts) {
            listener?.onError("Max reconnect attempts reached")
            return
        }
        
        reconnectAttempts++
        val delay = minOf(reconnectAttempts * 2000L, 10000L) // 指数退避
        
        GlobalScope.launch(Dispatchers.Main) {
            delay(delay)
            if (isActive) {
                connect()
            }
        }
    }
    
    fun disconnect() {
        isActive = false
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
    }
    
    // 主动发送心跳
    fun sendPing() {
        webSocket?.send("""{"event":"ping"}""")
    }
}
```

### 使用示例

```kotlin
class PaymentActivity : AppCompatActivity() {
    private var wsClient: OrderWebSocketClient? = null
    
    fun startWatchingOrder(orderId: String, clientToken: String) {
        wsClient = OrderWebSocketClient(
            serverUrl = "https://api.example.com",
            orderId = orderId,
            clientToken = clientToken
        ).apply {
            listener = object : OrderWebSocketClient.Listener {
                override fun onConnected() {
                    runOnUiThread {
                        Toast.makeText(this@PaymentActivity, 
                            "实时监控已开启", Toast.LENGTH_SHORT).show()
                    }
                }
                
                override fun onOrderStatusChanged(status: String, data: JSONObject) {
                    runOnUiThread {
                        when (status) {
                            "seen_onchain" -> {
                                showPaymentDetected()
                            }
                            "confirming" -> {
                                val confirmCount = data.optInt("confirm_count", 0)
                                updateConfirmProgress(confirmCount)
                            }
                            "paid_success" -> {
                                showPaymentSuccess()
                                // 可以继续等待 fulfilled
                            }
                            "fulfilled" -> {
                                val subscriptionUrl = data.optString("subscription_url")
                                showOrderComplete(subscriptionUrl)
                                wsClient?.disconnect()
                            }
                            "expired" -> {
                                showOrderExpired()
                                wsClient?.disconnect()
                            }
                            "underpaid", "overpaid" -> {
                                showPaymentError(status)
                            }
                        }
                    }
                }
                
                override fun onDisconnected() {
                    // 自动重连中或已断开
                }
                
                override fun onError(error: String) {
                    runOnUiThread {
                        // 降级到轮询
                        startPollingFallback()
                    }
                }
            }
            connect()
        }
    }
    
    private fun startPollingFallback() {
        // 启动轮询作为降级方案
        // 定期调用 GET /client/v1/orders/{order_id}/status
    }
    
    override fun onDestroy() {
        super.onDestroy()
        wsClient?.disconnect()
    }
}
```

## 降级策略

当 WebSocket 连接失败时，客户端应该自动回退到轮询机制：

```kotlin
// 轮询实现示例
fun startPolling(orderId: String, token: String) {
    val handler = Handler(Looper.getMainLooper())
    val pollRunnable = object : Runnable {
        override fun run() {
            lifecycleScope.launch {
                try {
                    val response = api.getOrderStatus(orderId)
                    handleStatusUpdate(response.status)
                    
                    // 继续轮询直到终态
                    if (!isTerminalStatus(response.status)) {
                        handler.postDelayed(this, 5000) // 5秒间隔
                    }
                } catch (e: Exception) {
                    handler.postDelayed(this, 10000) // 错误时延长间隔
                }
            }
        }
    }
    handler.post(pollRunnable)
}
```

## 最佳实践

1. **连接时机**: 创建订单后立即连接 WebSocket
2. **重连策略**: 使用指数退避，最多重试 5 次
3. **心跳间隔**: 建议 30-60 秒发送一次 ping
4. **降级方案**: WebSocket 失败后自动切换到轮询
5. **终态处理**: 订单到达 `fulfilled`, `expired`, `underpaid`, `overpaid`, `failed` 后断开连接
