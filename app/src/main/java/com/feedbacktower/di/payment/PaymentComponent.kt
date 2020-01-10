package com.feedbacktower.di.payment

import com.feedbacktower.ui.myplan.MyPlanScreen
import com.feedbacktower.ui.payment.PlanPaymentResultScreen
import com.feedbacktower.ui.plans.SubscriptionPlansScreen
import com.feedbacktower.ui.qrtransfer.receiver.scan.ReceiverScanFragment
import com.feedbacktower.ui.qrtransfer.receiver.wait.ReceiverWaitFragment
import com.feedbacktower.ui.qrtransfer.sender.showqr.SenderQrFragment
import com.feedbacktower.ui.qrtransfer.sender.wait.SenderWaitFragment
import dagger.Subcomponent

@PaymentScope
@Subcomponent
interface PaymentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PaymentComponent
    }

    fun inject(activity: PlanPaymentResultScreen)
    fun inject(activity: SubscriptionPlansScreen)
    fun inject(activity: MyPlanScreen)
    fun inject(fragment: ReceiverScanFragment)
    fun inject(fragment: ReceiverWaitFragment)
    fun inject(fragment: SenderQrFragment)
    fun inject(fragment: SenderWaitFragment)
}