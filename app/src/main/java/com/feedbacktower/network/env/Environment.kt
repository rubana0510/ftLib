package com.feedbacktower.network.env

import com.feedbacktower.BuildConfig

interface Environment {
    val SERVER_BASE_URL: String
    val S3_BASE_URL: String
    val MERCHANT_ID: String
    val MERCHANT_KEY: String
    val HELP_DEV_BASE_URL:String
}

class Prod : Environment {
    override val SERVER_BASE_URL: String
        get() = BuildConfig.PROD_SERVER_BASE_URL
    override val S3_BASE_URL: String
        get() = BuildConfig.PROD_S3_BASE_URL
    override val MERCHANT_ID: String
        get() = BuildConfig.PROD_MERCHANT_ID
    override val MERCHANT_KEY: String
        get() = BuildConfig.PROD_MERCHANT_KEY
    override val HELP_DEV_BASE_URL: String
        get() = BuildConfig.HELP_BASE_URL
}


class Dev : Environment {
    override val SERVER_BASE_URL: String
        get() = BuildConfig.DEV_SERVER_BASE_URL
    override val S3_BASE_URL: String
        get() = BuildConfig.DEV_S3_BASE_URL
    override val MERCHANT_ID: String
        get() = BuildConfig.DEV_MERCHANT_ID
    override val MERCHANT_KEY: String
        get() = BuildConfig.DEV_MERCHANT_KEY
    override val HELP_DEV_BASE_URL: String
        get() = BuildConfig.HELP_BASE_URL
}

val Env: Environment = Dev()
