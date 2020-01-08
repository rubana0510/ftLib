package com.feedbacktower.di.payment

import com.feedbacktower.ui.payment.PlanPaymentResultScreen
import dagger.Subcomponent

@PaymentScope
@Subcomponent
interface PaymentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PaymentComponent
    }

    fun inject(activity: PlanPaymentResultScreen)
}