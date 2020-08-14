package com.feedbacktower.di

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.feedbacktower.BuildConfig
import com.feedbacktower.R
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.network.env.Env
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.util.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.HTTP
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {

    @Provides
    fun provideGsonConverter(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun provideOkhttpClient(
        context: Context,
        appPrefs: ApplicationPreferences
    ): OkHttpClient {
       // print("Bearer ${appPrefs.authToken}")
        val clientBuilder = OkHttpClient.Builder().apply {
            connectTimeout(Constants.Service.Timeout.CONNECT, TimeUnit.MILLISECONDS)
            readTimeout(Constants.Service.Timeout.READ, TimeUnit.MILLISECONDS)
            writeTimeout(Constants.Service.Timeout.WRITE, TimeUnit.MILLISECONDS)
            addNetworkInterceptor ani@{ chain ->
                val request = chain.request().newBuilder()
                    .addHeader(Constants.AUTHORIZATION, "Bearer ${appPrefs.authToken}")
                    .addHeader("Platform", "android")
                    .addHeader("VersionCode", BuildConfig.VERSION_CODE.toString())
                    .addHeader("VersionName", BuildConfig.VERSION_NAME)
                    .build()
                val response = chain.proceed(request)
                if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    appPrefs.clearUserPrefs()
                    LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(Intent(Constants.SESSION_EXPIRED_INTENT_FILTER))
                }
                return@ani response
            }
            addInterceptor(HttpLoggingInterceptor().apply {
                level  = HttpLoggingInterceptor.Level.HEADERS
            })
        }
        return clientBuilder.build()
    }

    @Provides
    fun provideNetworkService(
        converterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): ApiService {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(Env.SERVER_BASE_URL)
            .client(okHttpClient)
            .build()
        return retrofit.create(ApiService::class.java)
    }

}