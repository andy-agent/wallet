package com.v2ray.ang.payment.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityUserProfileBinding
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.local.entity.UserEntity
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.payment.service.SubscriptionReminderWorker
import com.v2ray.ang.ui.BaseActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 用户中心页面
 * 显示用户信息、到期时间、订单历史
 */
class UserProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var orderAdapter: OrderHistoryAdapter

    private var currentUser: UserEntity? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentViewWithToolbar(binding.root, showHomeAsUp = true, title = getString(R.string.user_profile))

        paymentRepository = PaymentRepository(this)

        setupRecyclerView()
        setupClickListeners()
        loadUserData()
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderHistoryAdapter { order ->
            // 点击订单项可以查看详情
            showOrderDetail(order)
        }

        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(this@UserProfileActivity)
            adapter = orderAdapter
        }
    }

    private fun setupClickListeners() {
        binding.buttonLogout.setOnClickListener {
            showLogoutConfirmDialog()
        }

        binding.buttonRefresh.setOnClickListener {
            refreshData()
        }

        binding.buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 加载用户数据
     */
    private fun loadUserData() {
        lifecycleScope.launch {
            showLoading()

            // 加载用户信息
            currentUser = paymentRepository.getCachedCurrentUser()

            if (currentUser != null) {
                displayUserInfo(currentUser!!)
                loadOrderHistory(currentUser!!.userId)
                binding.layoutUserInfo.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
            } else {
                binding.layoutUserInfo.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            }

            hideLoading()
        }
    }

    /**
     * 显示用户信息
     */
    private fun displayUserInfo(user: UserEntity) {
        binding.textUsername.text = user.username
        binding.textEmail.text = user.email ?: getString(R.string.not_set)
        binding.textLoginTime.text = dateFormat.format(Date(user.loginAt))
    }

    /**
     * 加载订单历史
     */
    private fun loadOrderHistory(userId: String) {
        lifecycleScope.launch {
            val orders = paymentRepository.getCachedOrders(userId)
            orderAdapter.submitList(orders)

            // 显示最新订单的到期时间
            val activeOrders = orders.filter { it.expiredAt != null && it.expiredAt > System.currentTimeMillis() }
                .sortedBy { it.expiredAt }

            if (activeOrders.isNotEmpty()) {
                val nearestExpiry = activeOrders.first().expiredAt!!
                binding.textExpiryTime.text = dateFormat.format(Date(nearestExpiry))

                // 计算剩余天数
                val daysRemaining = (nearestExpiry - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)
                binding.textDaysRemaining.text = getString(R.string.days_remaining, daysRemaining)
                binding.textDaysRemaining.visibility = View.VISIBLE

                // 根据剩余天数设置颜色
                when {
                    daysRemaining <= 1 -> binding.textDaysRemaining.setTextColor(getColor(R.color.md_theme_error))
                    daysRemaining <= 3 -> binding.textDaysRemaining.setTextColor(getColor(R.color.md_theme_tertiary))
                    else -> binding.textDaysRemaining.setTextColor(getColor(R.color.md_theme_primary))
                }
            } else {
                binding.textExpiryTime.text = getString(R.string.no_active_subscription)
                binding.textDaysRemaining.visibility = View.GONE
            }

            // 显示空状态
            if (orders.isEmpty()) {
                binding.recyclerViewOrders.visibility = View.GONE
                binding.textEmptyOrders.visibility = View.VISIBLE
            } else {
                binding.recyclerViewOrders.visibility = View.VISIBLE
                binding.textEmptyOrders.visibility = View.GONE
            }
        }
    }

    /**
     * 刷新数据（从服务器同步）
     */
    private fun refreshData() {
        lifecycleScope.launch {
            showLoading()

            val result = paymentRepository.getSubscription()
            result.onSuccess { data ->
                // 刷新用户信息
                paymentRepository.getClientToken()?.let { token ->
                    paymentRepository.cacheUserInfo(data.user, token)
                }
                loadUserData()
            }.onFailure { error ->
                showError(error.message ?: getString(R.string.refresh_failed))
            }

            hideLoading()
        }
    }

    /**
     * 显示退出登录确认对话框
     */
    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_logout)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.confirm) { _, _ ->
                performLogout()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    /**
     * 执行退出登录
     */
    private fun performLogout() {
        lifecycleScope.launch {
            showLoading()

            // 停止到期提醒服务
            SubscriptionReminderWorker.stopReminderWork(this@UserProfileActivity)

            // 清除本地数据
            paymentRepository.logout()

            hideLoading()

            // 跳转到登录页面
            val intent = Intent(this@UserProfileActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    /**
     * 显示订单详情
     */
    private fun showOrderDetail(order: OrderEntity) {
        val message = StringBuilder().apply {
            appendLine("${getString(R.string.order_number)}: ${order.orderNo}")
            appendLine("${getString(R.string.plan_name)}: ${order.planName}")
            appendLine("${getString(R.string.amount)}: ${order.amount} ${order.assetCode}")
            appendLine("${getString(R.string.status)}: ${getStatusText(order.status)}")
            appendLine("${getString(R.string.created_at)}: ${dateFormat.format(Date(order.createdAt))}")
            order.paidAt?.let {
                appendLine("${getString(R.string.paid_at)}: ${dateFormat.format(Date(it))}")
            }
            order.expiredAt?.let {
                appendLine("${getString(R.string.expired_at)}: ${dateFormat.format(Date(it))}")
            }
            // txHash removed from entity
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.order_detail)
            .setMessage(message.toString())
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun getStatusText(status: String): String {
        return when (status) {
            "PENDING" -> getString(R.string.status_pending)
            "PAID" -> getString(R.string.status_paid)
            "FULFILLED" -> getString(R.string.status_fulfilled)
            "CANCELLED" -> getString(R.string.status_cancelled)
            else -> status
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 订单历史适配器
     */
    inner class OrderHistoryAdapter(
        private val onItemClick: (OrderEntity) -> Unit
    ) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

        private var orders: List<OrderEntity> = emptyList()

        fun submitList(newOrders: List<OrderEntity>) {
            orders = newOrders
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_history, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            holder.bind(orders[position])
        }

        override fun getItemCount(): Int = orders.size

        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cardView: MaterialCardView = itemView.findViewById(R.id.cardOrder)
            private val textPlanName: TextView = itemView.findViewById(R.id.textPlanName)
            private val textOrderNo: TextView = itemView.findViewById(R.id.textOrderNo)
            private val textAmount: TextView = itemView.findViewById(R.id.textAmount)
            private val textStatus: TextView = itemView.findViewById(R.id.textStatus)
            private val textDate: TextView = itemView.findViewById(R.id.textDate)

            fun bind(order: OrderEntity) {
                textPlanName.text = order.planName
                textOrderNo.text = order.orderNo
                textAmount.text = "${order.amount} ${order.assetCode}"
                textStatus.text = getStatusText(order.status)
                textDate.text = dateFormat.format(Date(order.createdAt))

                // 根据状态设置颜色
                when (order.status) {
                    "FULFILLED" -> textStatus.setTextColor(getColor(R.color.md_theme_primary))
                    "PAID" -> textStatus.setTextColor(getColor(R.color.md_theme_tertiary))
                    "PENDING" -> textStatus.setTextColor(getColor(R.color.md_theme_error))
                    else -> textStatus.setTextColor(getColor(R.color.md_theme_onSurfaceVariant))
                }

                cardView.setOnClickListener {
                    onItemClick(order)
                }
            }
        }
    }
}
