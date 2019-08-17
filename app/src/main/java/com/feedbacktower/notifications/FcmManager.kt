package com.feedbacktower.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FcmManager {
    fun subscribeToTopic(userType: String) {
        val topic = userType.toLowerCase()
        FirebaseMessaging.getInstance()
            .subscribeToTopic(topic).addOnFailureListener {
                Log.d("FcmManager", "Failed to Subscribe")
            }
            .addOnSuccessListener {
                Log.d("FcmManager", "Subscribed to $topic")
            }
    }
}