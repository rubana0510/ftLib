package com.feedbacktower.data

class AppPrefs private constructor() {

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppPrefs? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AppPrefs().also { instance = it }
            }
    }

    fun isSignedIn(): Boolean = false

}