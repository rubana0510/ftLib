package com.feedbacktower.network.env

import com.feedbacktower.BuildConfig

object Environment {

    //LIVE
    const val SERVER_BASE_URL = BuildConfig.LIVE_SERVER_BASE_URL
    const val S3_BASE_URL = BuildConfig.LIVE_S3_BASE_URL
    const val MERCHANT_ID = BuildConfig.LIVE_MERCHANT_ID
    const val MERCHANT_KEY = BuildConfig.LIVE_MERCHANT_KEY

   /* //TEST
    const val SERVER_BASE_URL = BuildConfig.TEST_SERVER_BASE_URL
    const val S3_BASE_URL = BuildConfig.TEST_S3_BASE_URL
    const val MERCHANT_ID = BuildConfig.TEST_MERCHANT_ID
    const val MERCHANT_KEY = BuildConfig.TEST_MERCHANT_KEY*/
}