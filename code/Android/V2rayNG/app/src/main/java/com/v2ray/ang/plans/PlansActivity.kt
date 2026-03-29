package com.v2ray.ang.plans

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.v2ray.ang.databinding.ActivityPlansBinding
import com.v2ray.ang.payment.data.model.Plan
import com.v2ray.ang.payment.data.repository.PaymentRepository
import kotlinx.coroutines.launch

/**
 * 套餐列表页面
 */
class PlansActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlansBinding
    private lateinit var repository: PaymentRepository
    private lateinit var adapter: PlansAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "选择套餐"

        repository = PaymentRepository(this)
        setupRecyclerView()
        loadPlans()
    }

    private fun setupRecyclerView() {
        adapter = PlansAdapter { plan ->
            onPlanSelected(plan)
        }
        binding.recyclerViewPlans.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPlans.adapter = adapter
    }

    private fun loadPlans() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            
            val result = repository.getPlans()
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { plans ->
                if (plans.isEmpty()) {
                    binding.textViewEmpty.visibility = View.VISIBLE
                    binding.recyclerViewPlans.visibility = View.GONE
                } else {
                    binding.textViewEmpty.visibility = View.GONE
                    binding.recyclerViewPlans.visibility = View.VISIBLE
                    adapter.submitList(plans)
                }
            }.onFailure { error ->
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.textViewEmpty.text = "加载失败: ${error.message}"
                Toast.makeText(this@PlansActivity, "加载套餐失败: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onPlanSelected(plan: Plan) {
        // 跳转到支付页面
        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("plan_id", plan.id)
            putExtra("plan_name", plan.name)
            putExtra("plan_price", plan.priceUsd)
            putExtra("plan_traffic", plan.getTrafficDisplay())
            putExtra("plan_duration", plan.getDurationDisplay())
            putExtra("supports_sol", plan.supportsSol())
            putExtra("supports_usdt", plan.supportsUsdtTrc20())
        }
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
