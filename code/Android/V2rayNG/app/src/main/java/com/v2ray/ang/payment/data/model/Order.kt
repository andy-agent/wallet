package com.v2ray.ang.payment.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("orderNo")
    val orderNo: String,
    @SerializedName("planCode")
    val planCode: String,
    @SerializedName("planName")
    val planName: String,
    @SerializedName("orderType")
    val orderType: String,
    @SerializedName("quoteAssetCode")
    val quoteAssetCode: String,
    @SerializedName("quoteNetworkCode")
    val quoteNetworkCode: String,
    @SerializedName("quoteUsdAmount")
    val quoteUsdAmount: String,
    @SerializedName("baseAmount")
    val baseAmount: String? = null,
    @SerializedName("uniqueAmountDelta")
    val uniqueAmountDelta: String? = null,
    @SerializedName("payableAmount")
    val payableAmount: String,
    val status: String,
    @SerializedName("expiresAt")
    val expiresAt: String,
    @SerializedName("confirmedAt")
    val confirmedAt: String? = null,
    @SerializedName("completedAt")
    val completedAt: String? = null,
    @SerializedName("failureReason")
    val failureReason: String? = null,
    @SerializedName("submittedClientTxHash")
    val submittedClientTxHash: String? = null,
    @SerializedName("matchedOnchainTxHash")
    val matchedOnchainTxHash: String? = null,
    @SerializedName("paymentMatchedAt")
    val paymentMatchedAt: String? = null,
    @SerializedName("createdAt")
    val createdAt: String = expiresAt,
    val paymentTarget: PaymentTarget? = null,
    val subscriptionUrl: String? = null
) {
    val statusText: String
        get() = when (status) {
            "AWAITING_PAYMENT" -> "待支付"
            "PAYMENT_DETECTED" -> "已发现支付"
            "CONFIRMING" -> "确认中"
            "PAID" -> "已支付"
            "PROVISIONING" -> "开通中"
            "COMPLETED" -> "已完成"
            "EXPIRED" -> "已过期"
            "UNDERPAID_REVIEW" -> "少付待审核"
            "OVERPAID_REVIEW" -> "多付待审核"
            "FAILED" -> "失败"
            "CANCELED" -> "已取消"
            else -> status
        }

    val plan: PlanInfo
        get() = PlanInfo(planCode, planName)

    val payment: PaymentInfo
        get() = PaymentInfo(
            assetCode = quoteAssetCode,
            amountCrypto = paymentTarget?.payableAmount ?: payableAmount,
            receiveAddress = paymentTarget?.collectionAddress.orEmpty(),
            qrText = paymentTarget?.qrText.orEmpty(),
            txHash = matchedOnchainTxHash ?: submittedClientTxHash,
            confirmedAt = paymentMatchedAt ?: confirmedAt
        )

    val fulfillment: FulfillmentInfo?
        get() = if (subscriptionUrl.isNullOrBlank()) {
            null
        } else {
            FulfillmentInfo(
                marzbanUsername = "",
                clientToken = "",
                tokenExpiresAt = expiresAt,
                subscriptionUrl = subscriptionUrl,
                expiredAt = expiresAt
            )
        }
}

data class PlanInfo(
    val id: String,
    val name: String
)

data class PaymentInfo(
    val assetCode: String,
    val amountCrypto: String,
    val receiveAddress: String,
    val qrText: String,
    val txHash: String? = null,
    val confirmedAt: String? = null
)

data class FulfillmentInfo(
    val marzbanUsername: String,
    val clientToken: String,
    val tokenExpiresAt: String,
    val subscriptionUrl: String,
    val expiredAt: String
)

data class PaymentTarget(
    @SerializedName("orderNo")
    val orderNo: String,
    @SerializedName("networkCode")
    val networkCode: String,
    @SerializedName("assetCode")
    val assetCode: String,
    @SerializedName("collectionAddress")
    val collectionAddress: String,
    @SerializedName("payableAmount")
    val payableAmount: String,
    @SerializedName("baseAmount")
    val baseAmount: String? = null,
    @SerializedName("uniqueAmountDelta")
    val uniqueAmountDelta: String,
    @SerializedName("serviceEnabled")
    val serviceEnabled: Boolean? = null,
    @SerializedName("qrText")
    val qrText: String,
    @SerializedName("expiresAt")
    val expiresAt: String
)

data class CreateOrderRequest(
    @SerializedName("planCode")
    val planCode: String,
    @SerializedName("orderType")
    val orderType: String,
    @SerializedName("quoteAssetCode")
    val quoteAssetCode: String,
    @SerializedName("quoteNetworkCode")
    val quoteNetworkCode: String
)

data class CreateOrderResponse(
    val code: String,
    val message: String,
    val data: Order?
)

data class GetOrderResponse(
    val code: String,
    val message: String,
    val data: Order?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val code: String,
    val password: String,
    val installationId: String? = null
)

data class AuthData(
    @SerializedName("accountId")
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    @SerializedName("accessTokenExpiresAt")
    val expiresAt: String,
    @SerializedName("accountStatus")
    val accountStatus: String
) {
    val username: String
        get() = userId
}

data class LoginResponse(
    val code: String,
    val message: String,
    val data: AuthData?
)

data class RegisterResponse(
    val code: String,
    val message: String,
    val data: AuthData?
)

data class RefreshTokenRequest(
    val refreshToken: String,
    val installationId: String? = null
)

data class RefreshTokenData(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("accessTokenExpiresAt")
    val expiresAt: String
)

data class RefreshTokenResponse(
    val code: String,
    val message: String,
    val data: RefreshTokenData?
)
