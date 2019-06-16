package com.feedbacktower.ui.myplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.feedbacktower.R
import com.feedbacktower.data.models.SubscriptionPlan
import com.feedbacktower.data.type.DurationType
import com.feedbacktower.databinding.ActivityMyPlanScreenBinding
import com.feedbacktower.network.manager.TransactionManager
import com.feedbacktower.ui.plans.SubscriptionPlansScreen
import com.feedbacktower.util.launchActivity
import org.jetbrains.anko.toast
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.SimpleDateFormat
import java.util.*

class MyPlanScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMyPlanScreenBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_my_plan_screen)
        title = "Subscription Plan"
        binding.renewPlan.setOnClickListener {
            launchActivity<SubscriptionPlansScreen> { }
        }
        getPlanTransactions(binding)
    }

    private fun getPlanTransactions(binding: ActivityMyPlanScreenBinding) {
        binding.isLoading = true
        TransactionManager.getInstance()
            .getTransactions { response, error ->
                if (error == null && response?.transactions != null) {
                    if (response.transactions.isNotEmpty()) {
                        binding.isLoading = false
                        val latestPlanTaken = response.transactions[0].subscriptionPlan
                        binding.plan = latestPlanTaken
                        binding.expiryDate = getExpiry(latestPlanTaken)

                    } else {
                        launchActivity<SubscriptionPlansScreen>()
                        toast("You don't have any active plans")
                    }
                }
            }
    }

    private fun getExpiry(plan: SubscriptionPlan): String {
        val planStatedOn = plan.updatedAt
        val period = plan.period
        val startDate = DateTime(planStatedOn, DateTimeZone.UTC)
        val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        if (plan.durationType == DurationType.UNKNOWN) {
            return "Unknown"
        }
        val expiryDate: DateTime? =
        if (plan.durationType == DurationType.YEAR) {
            startDate.plusYears(period)
        } else if (plan.durationType == DurationType.MONTH) {
            startDate.plusMonths(period)
        }else{
            startDate.plusYears(period)
        }
        expiryDate?.toDateTime(DateTimeZone.forID(DateTimeZone.getDefault().id))
        return toFormat.format(expiryDate?.toDate())
    }
}
