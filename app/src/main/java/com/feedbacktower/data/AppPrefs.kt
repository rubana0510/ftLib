package com.feedbacktower.data

import android.content.Context
import android.content.SharedPreferences
import com.feedbacktower.network.models.AuthResponse
import com.google.gson.Gson

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

    var user: AuthResponse.User?
        get() {
            val user: AuthResponse.User? =
                Gson().fromJson(sharedPrefs.getString("USER", null), AuthResponse.User::class.java)
            return user
        }
        set(value) {
            sharedPrefs.edit().putString("USER", Gson().toJson(value)).apply()
        }

}