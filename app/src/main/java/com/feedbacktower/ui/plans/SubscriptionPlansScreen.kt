package com.feedbacktower.ui.plans

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.R
import com.feedbacktower.adapters.PlanAdapter
import com.feedbacktower.data.models.SubscriptionPlan
import com.feedbacktower.databinding.ActivitySubscriptionPlansScreenBinding
import com.feedbacktower.util.setHorizontal

class SubscriptionPlansScreen : AppCompatActivity() {

    private lateinit var planListView: RecyclerView
    private lateinit var planAdapter: PlanAdapter
    private val planList: List<SubscriptionPlan> = ArrayList()
    private var colorList:List<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySubscriptionPlansScreenBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_subscription_plans_screen)
        initUi(binding)
    }

    private fun initUi(binding: ActivitySubscriptionPlansScreenBinding) {
        colorList = listOf(R.color.planCard1, R.color.planCard2, R.color.planCard3, R.color.planCard4, R.color.planCard5)
        planAdapter = PlanAdapter(onPlanClick)
        planListView = binding.planListView
        planListView.setHorizontal(this)
        planListView.adapter = planAdapter
        getPlanList()
        binding.onContinueClick = onContinueClick
    }

    private fun getPlanList() {
        var list = listOf(
            SubscriptionPlan("1", "Daily plan", "DAY", 20, 500.0, 1000.0),
            SubscriptionPlan("2", "Monthly plan", "MONTH", 5, 1000.0, 2000.0),
            SubscriptionPlan("3", "Yearly plan", "YEAR", 1, 3000.0, 5000.0)
        )
        list.forEachIndexed { index, plan ->
            plan.cardColor = getCardColor(index)
        }
        planAdapter.submitList(
            list
        )
    }

    private fun getCardColor(index: Int): Int = colorList[index%colorList.size]

    private val onContinueClick = View.OnClickListener { }

    private val onPlanClick = { item: SubscriptionPlan ->
        for (p in planList) {
            p.isSelected = item.planId == p.planId
        }
        planAdapter.submitList(planList)
    }

}
