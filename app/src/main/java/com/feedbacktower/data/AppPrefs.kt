package com.feedbacktower.data

import android.content.Context
import android.content.SharedPreferences

class AppPrefs private constructor() {

    companion object {

        @Volatile
        private lateinit var sharedPrefs: SharedPreferences
        private var appPrefs: AppPrefs? = null

        fun getInstance(context: Context): AppPrefs =
            appPrefs ?: synchronized(this) {
                appPrefs ?: AppPrefs().also {
                    appPrefs = it
                    sharedPrefs = context.getSharedPreferences("ft_prefs", Context.MODE_PRIVATE)
                }
            }
    }

    var authToken: String?
        get() = sharedPrefs.getString("AUTH_TOKEN", null)
        set(value) {
            sharedPrefs.edit().putString("AUTH_TOKEN", value).apply()
        }


}