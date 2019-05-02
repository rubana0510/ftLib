package com.feedbacktower.data.models

import com.feedbacktower.R
import java.lang.IllegalArgumentException

data class SubscriptionPlan(
    val planId: String,
    val planName: String,
    val type: String,
    val duration: Int,
    val fee: Double,
    val walletAmount: Double
) {
    val planType: PlanType
        get() {
            return when (type) {
                "YEAR" -> PlanType.YEAR
                "DAY" -> PlanType.DAY
                "MONTH" -> PlanType.MONTH
                else -> throw IllegalArgumentException()
            }
        }
    val durationUnitDisplay: String
        get() {
            return when (planType) {
                PlanType.YEAR -> if (duration == 1) "YEAR" else "YEARS"
                PlanType.DAY -> if (duration == 1) "DAY" else "DAYS"
                PlanType.MONTH -> if (duration == 1) "MONTH" else "MONTHS"
                else -> throw IllegalArgumentException()
            }
        }
    val durationDisplay: String
        get() = duration.toString()

    var isSelected: Boolean = false

    var cardColor: Int = R.color.planCard1

    enum class PlanType { YEAR, MONTH, DAY, UNKNOWN }
}