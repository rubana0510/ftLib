package com.feedbacktower.network.service

import android.util.Log
import com.feedbacktower.App
import com.feedbacktower.BuildConfig
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.util.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by sanket on 25-09-2018.
 */
class ApiService {
    private val TAG = "ApiService"

    companion object Factory {
        fun create(): ApiServiceDescriptor {
            val clientBuilder = OkHttpClient.Builder().apply {
                connectTimeout(Constants.Service.Timeout.CONNECT, TimeUnit.MILLISECONDS)
                readTimeout(Constants.Service.Timeout.READ, TimeUnit.MILLISECONDS)
                writeTimeout(Constants.Service.Timeout.WRITE, TimeUnit.MILLISECONDS)
                addInterceptor(HttpLoggingInterceptor().apply {
                  //  level = HttpLoggingInterceptor.Level.BODY
                    level = HttpLoggingInterceptor.Level.HEADERS
                })
                addNetworkInterceptor ani@{ chain ->
                    val request = chain.request().newBuilder()
                        .addHeader(Constants.AUTHORIZATION, "Bearer ${App.getInstance().getToken()}").build()
                    return@ani chain.proceed(request);
                }
            }
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(Constants.Service.Secrets.BASE_URL)
                .client(clientBuilder.build())
                .build()
            return retrofit.create(ApiServiceDescriptor::class.java)
        }
    }
}