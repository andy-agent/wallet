package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.v2ray.ang.composeui.bridge.order.VpnOrderBridge

/**
 * 支付方式
 */
enum class PaymentMethod {
    WALLET,     // 钱包支付
    CRYPTO      // 加密货币支付
}

/**
 * 订单收银台状态
 */
sealed class OrderCheckoutState {
    object Idle : OrderCheckoutState()
    object Loading : OrderCheckoutState()
    data class Loaded(
        val orderId: String,
        val planName: String,
        val duration: String,
        val amount: String,
        val discount: String?,
        val totalAmount: String,
        val selectedPaymentMethod: PaymentMethod,
        val walletAddress: String?,
        val expiresIn: Int  // 过期时间（秒）
    ) : OrderCheckoutState()
    data class Error(val message: String) : OrderCheckoutState()
}

/**
 * 订单收银台ViewModel
 */
class OrderCheckoutViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<OrderCheckoutState>(OrderCheckoutState.Idle)
    val state: StateFlow<OrderCheckoutState> = _state
    private val bridge = VpnOrderBridge(application)
    private var currentPlanCode: String = ""

    fun loadOrderDetails(planCode: String) {
        if (planCode.isBlank()) {
            _state.value = OrderCheckoutState.Error("缺少套餐参数")
            return
        }
        currentPlanCode = planCode
        createOrderWithCurrentMethod()
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        val currentState = _state.value
        if (currentState is OrderCheckoutState.Loaded) {
            _state.value = currentState.copy(selectedPaymentMethod = method)
            createOrderWithCurrentMethod()
        }
    }

    private fun createOrderWithCurrentMethod() {
        val selected = (state.value as? OrderCheckoutState.Loaded)?.selectedPaymentMethod
            ?: PaymentMethod.WALLET
        _state.value = OrderCheckoutState.Loading
        viewModelScope.launch {
            bridge.createOrder(
                planCode = currentPlanCode,
                useWalletPath = selected == PaymentMethod.WALLET,
            ).onSuccess { data ->
                _state.value = OrderCheckoutState.Loaded(
                    orderId = data.orderNo,
                    planName = data.planName,
                    duration = data.duration,
                    amount = data.amount,
                    discount = null,
                    totalAmount = data.totalAmount,
                    selectedPaymentMethod = selected,
                    walletAddress = data.receiveAddress,
                    expiresIn = data.expiresInSeconds,
                )
            }.onFailure { error ->
                _state.value = OrderCheckoutState.Error(error.message ?: "创建订单失败")
            }
        }
    }
}

/**
 * 生成二维码Bitmap
 */
fun generateQRCode(content: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

/**
 * 订单收银台页
 * 显示订单信息和支付二维码
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCheckoutPage(
    viewModel: OrderCheckoutViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    planId: String = "",
    onNavigateBack: () -> Unit = {},
    onPayWithWallet: () -> Unit = {},
    onPayWithCrypto: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(planId) {
        viewModel.loadOrderDetails(planId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单确认") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (state is OrderCheckoutState.Loaded) {
                CheckoutBottomBar(
                    totalAmount = (state as OrderCheckoutState.Loaded).totalAmount,
                    onPay = {
                        val paymentMethod = (state as OrderCheckoutState.Loaded).selectedPaymentMethod
                        when (paymentMethod) {
                            PaymentMethod.WALLET -> onPayWithWallet()
                            PaymentMethod.CRYPTO -> onPayWithCrypto()
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            when (state) {
                is OrderCheckoutState.Loaded -> {
                    val loadedState = state as OrderCheckoutState.Loaded
                    
                    // 订单信息卡片
                    OrderInfoCard(state = loadedState)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 支付方式选择
                    PaymentMethodSelector(
                        selectedMethod = loadedState.selectedPaymentMethod,
                        onMethodSelected = { viewModel.selectPaymentMethod(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 支付详情
                    when (loadedState.selectedPaymentMethod) {
                        PaymentMethod.WALLET -> {
                            WalletPaymentDetail(
                                walletAddress = loadedState.walletAddress ?: "",
                                amount = loadedState.totalAmount
                            )
                        }
                        PaymentMethod.CRYPTO -> {
                            CryptoPaymentDetail(
                                walletAddress = loadedState.walletAddress ?: "",
                                amount = loadedState.totalAmount
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
                is OrderCheckoutState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is OrderCheckoutState.Error -> {
                    Text(
                        text = (state as OrderCheckoutState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun OrderInfoCard(state: OrderCheckoutState.Loaded) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 订单号
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "订单号",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = state.orderId,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            
            // 套餐信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = state.planName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = state.duration,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = state.amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // 优惠
            state.discount?.let { discount ->
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "优惠",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = discount,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            
            // 总计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "应付总额",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = state.totalAmount,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "选择支付方式",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 钱包支付
        PaymentMethodItem(
            icon = Icons.Default.AccountBalanceWallet,
            title = "钱包支付",
            subtitle = "使用CryptoVPN钱包",
            isSelected = selectedMethod == PaymentMethod.WALLET,
            onClick = { onMethodSelected(PaymentMethod.WALLET) }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 加密货币支付
        PaymentMethodItem(
            icon = Icons.Default.CurrencyBitcoin,
            title = "加密货币",
            subtitle = "USDT / ETH / BTC",
            isSelected = selectedMethod == PaymentMethod.CRYPTO,
            onClick = { onMethodSelected(PaymentMethod.CRYPTO) }
        )
    }
}

@Composable
private fun PaymentMethodItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp, 
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun WalletPaymentDetail(
    walletAddress: String,
    amount: String
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "钱包支付",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "使用您的CryptoVPN钱包支付",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = amount,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CryptoPaymentDetail(
    walletAddress: String,
    amount: String
) {
    val qrBitmap = remember(walletAddress) {
        generateQRCode(walletAddress)
    }
    
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "扫码支付",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 二维码
                qrBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 地址
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = walletAddress.take(20) + "...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "请支付 $amount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    totalAmount: String,
    onPay: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "应付总额",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = totalAmount,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Button(
                onClick = onPay,
                modifier = Modifier.height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "立即支付",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderCheckoutPagePreview() {
    MaterialTheme {
        OrderCheckoutPage()
    }
}
