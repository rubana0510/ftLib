package com.feedbacktower.ui.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.feedbacktower.R
import com.feedbacktower.network.manager.TransactionManager
import com.feedbacktower.ui.SplashScreen
import com.feedbacktower.ui.plans.SubscriptionPlansScreen
import com.feedbacktower.util.gone
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.toDate
import com.feedbacktower.util.visible
import kotlinx.android.synthetic.main.activity_plan_payment_success_screen.*
import kotlinx.android.synthetic.main.activity_subscription_plan_screen.*
import kotlinx.android.synthetic.main.activity_subscription_plan_screen.continueButton
import org.jetbrains.anko.toast

class PlanPaymentSuccessScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_payment_success_screen)

        continueButton.setOnClickListener {
            launchActivity<SplashScreen> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            finish()
        }
        getPlanTransactions()
    }

    private fun getPlanTransactions() {
        txDetails.gone()
        TransactionManager.getInstance()
            .getTransactions { response, error ->
                if (error == null && response?.transactions != null) {
                    if (response.transactions.isNotEmpty()) {
                        txDetails.visible()
                        val transaction = response.transactions[0]
                        transactionId.text = transaction.txnid
                        date.text = transaction.updatedAt.toDate()
                    } else {
                        //toast("You don't have any active plans")
                        Log.e("PlanPaySuccess", "No transaction data")
                    }
                }
            }
    }

}
