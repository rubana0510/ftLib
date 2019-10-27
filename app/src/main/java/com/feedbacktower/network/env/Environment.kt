package com.feedbacktower.network.env

import com.feedbacktower.BuildConfig

interface Environment {
    val SERVER_BASE_URL: String
    val S3_BASE_URL: String
    val MERCHANT_ID: String
    val MERCHANT_KEY: String
}

class Live : Environment {
    override val SERVER_BASE_URL: String
        get() = BuildConfig.LIVE_SERVER_BASE_URL
    override val S3_BASE_URL: String
        get() = BuildConfig.LIVE_S3_BASE_URL
    override val MERCHANT_ID: String
        get() = BuildConfig.LIVE_MERCHANT_ID
    override val MERCHANT_KEY: String
        get() = BuildConfig.LIVE_MERCHANT_KEY
}


class Test : Environment {
    override val SERVER_BASE_URL: String
        get() = BuildConfig.TEST_SERVER_BASE_URL
    override val S3_BASE_URL: String
        get() = BuildConfig.TEST_S3_BASE_URL
    override val MERCHANT_ID: String
        get() = BuildConfig.TEST_MERCHANT_ID
    override val MERCHANT_KEY: String
        get() = BuildConfig.TEST_MERCHANT_KEY
}

val Env: Environment = Live()
