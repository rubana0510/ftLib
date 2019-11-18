package com.feedbacktower.di

import com.feedbacktower.App
import com.feedbacktower.BuildConfig
import com.feedbacktower.network.env.Env
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.util.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {

    @Provides
    fun provideGsonConverter(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun provideOkhttpClient(): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder().apply {
            connectTimeout(Constants.Service.Timeout.CONNECT, TimeUnit.MILLISECONDS)
            readTimeout(Constants.Service.Timeout.READ, TimeUnit.MILLISECONDS)
            writeTimeout(Constants.Service.Timeout.WRITE, TimeUnit.MILLISECONDS)
            addInterceptor(HttpLoggingInterceptor().apply w@{
                if (!BuildConfig.DEBUG) return@w
                level = HttpLoggingInterceptor.Level.BODY
                level = HttpLoggingInterceptor.Level.HEADERS
            })
            addNetworkInterceptor ani@{ chain ->
                val request = chain.request().newBuilder()
                    .addHeader(Constants.AUTHORIZATION, "Bearer ${App.getInstance().getToken()}")
                    .addHeader("Platform", "android")
                    .addHeader("VersionCode", BuildConfig.VERSION_CODE.toString())
                    .addHeader("VersionName", BuildConfig.VERSION_NAME)
                    .build()
                return@ani chain.proceed(request);
            }
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