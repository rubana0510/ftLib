package com.feedbacktower.data

import android.content.Context
import android.content.SharedPreferences
import com.feedbacktower.data.models.MyBusiness
import com.feedbacktower.data.models.User
import com.google.gson.Gson

class AppPrefs private constructor() {

    val currentUser by lazy { user }
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

    var firebaseToken: String?
        get() = sharedPrefs.getString("FIREBASE_TOKEN", null)
        set(value) {
            sharedPrefs.edit().putString("FIREBASE_TOKEN", value).apply()
        }

    var authToken: String?
        get() = sharedPrefs.getString("AUTH_TOKEN", null)
        set(value) {
            sharedPrefs.edit().putString("AUTH_TOKEN", value).apply()
        }

    fun setValue(key: String, value: String) {
        sharedPrefs.edit().putString(key, value).apply()
    }

    fun getValue(key: String): String? {
        return sharedPrefs.getString(key, null)
    }

    var user: User?
        get() {
            return Gson().fromJson(sharedPrefs.getString("USER", null), User::class.java)
        }
        set(value) {
            sharedPrefs.edit().putString("USER", Gson().toJson(value)).apply()
        }

    var business: MyBusiness?
        get() {
            return Gson().fromJson(sharedPrefs.getString("BUSINESS", null), MyBusiness::class.java)
        }
        set(value) {
            sharedPrefs.edit().putString("BUSINESS", Gson().toJson(value)).apply()
        }

}