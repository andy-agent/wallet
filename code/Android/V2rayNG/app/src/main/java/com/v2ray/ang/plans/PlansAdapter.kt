package com.v2ray.ang.plans

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.v2ray.ang.R
import com.v2ray.ang.payment.data.model.Plan

/**
 * 套餐列表适配器
 */
class PlansAdapter(
    private val onPlanClick: (Plan) -> Unit
) : ListAdapter<Plan, PlansAdapter.PlanViewHolder>(PlanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardViewPlan)
        private val textBadge: TextView = itemView.findViewById(R.id.textBadge)
        private val textName: TextView = itemView.findViewById(R.id.textPlanName)
        private val textDescription: TextView = itemView.findViewById(R.id.textPlanDescription)
        private val textPrice: TextView = itemView.findViewById(R.id.textPlanPrice)
        private val textTraffic: TextView = itemView.findViewById(R.id.textPlanTraffic)
        private val textDuration: TextView = itemView.findViewById(R.id.textPlanDuration)
        private val textPaymentMethods: TextView = itemView.findViewById(R.id.textPaymentMethods)

        fun bind(plan: Plan) {
            // 徽章
            if (!plan.badge.isNullOrEmpty()) {
                textBadge.visibility = View.VISIBLE
                textBadge.text = plan.badge
            } else {
                textBadge.visibility = View.GONE
            }

            textName.text = plan.name
            textDescription.text = plan.description
            textPrice.text = "$${plan.priceUsd}"
            textTraffic.text = "流量: ${plan.getTrafficDisplay()}"
            textDuration.text = "时长: ${plan.getDurationDisplay()}"

            // 支付方式
            val methods = mutableListOf<String>()
            if (plan.supportsSol()) methods.add("SOL")
            if (plan.supportsUsdtTrc20()) methods.add("USDT-TRC20")
            textPaymentMethods.text = "支持: ${methods.joinToString(", ")}"

            // 点击事件
            cardView.setOnClickListener {
                onPlanClick(plan)
            }

            // 推荐套餐高亮
            if (plan.badge == "HOT") {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.colorAccent)
                )
            } else {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.md_theme_surface)
                )
            }
        }
    }

    class PlanDiffCallback : DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem == newItem
        }
    }
}
