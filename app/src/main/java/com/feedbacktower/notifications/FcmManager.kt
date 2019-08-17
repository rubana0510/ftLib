package com.feedbacktower.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FcmManager {
    fun subscribeToTopic(_topic: String) {
        val topic = _topic.toLowerCase()
        FirebaseMessaging.getInstance()
            .subscribeToTopic(topic).addOnFailureListener {
                Log.d("FcmManager", "Failed to Subscribe")
            }
            .addOnSuccessListener {
                Log.d("FcmManager", "Subscribed to $topic")
            }
    }
}