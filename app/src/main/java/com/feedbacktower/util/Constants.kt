package com.feedbacktower.util

import com.feedbacktower.network.env.Environment

object Constants {
    object Service {
        object Timeout {
            const val CONNECT: Long = 30 * 1000
            const val READ: Long = 30 * 1000
            const val WRITE: Long = 30 * 1000
        }

        object Error{
            const val HTTP_EXCEPTION_ERROR_CODE = "NETWORK_ERROR"
            const val SOCKET_TIMEOUT_EXCEPTION_ERROR_CODE = "TIMEOUT_ERROR"
            const val NO_INTERNET_ERROR_CODE = "NO_INTERNET_ERROR"
        }

    }

    object Media {
        const val MAX_VIDEO_LENGTH = 3 * 60 //secs
        const val MAX_VIDEO_SIZE = 10 //mb
    }

    const val AD_BANNER_DURATION: Long = 5000
    const val PAGE_SIZE = 10
    const val CAT_GRID_SIZE = 3
    const val CAT_MAX_HOME = 6
    const val KEY_LISTTYPE = "LIST_TYPE"
    const val KEY_CATEGORY = "KEY_CATEGORY"
    const val KEY_KEYWORD = "KEY_KEYWORD"
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 15
    const val SERVICE_ID: String = "SERVICE_ID"
    const val AUTHORIZATION: String = "Authorization"
    const val PHONE_LENGTH = 10
    const val OTP_LENGTH = 6
    const val CROP_IMAGE_QUALITY: Int = 60
    const val DB_NAME: String = "feedbacktower"
    const val QR_STATUS_CHECK_INTERVAL: Long = 3000L
    const val PAYMENT_STATUS_CHECK_INTERVAL: Long = 2000L
    const val PAYMENT_STATUS_CHECK_MAX_WAIT_TIME: Long = 10000L
    const val IMAGE_COMPRESSION_QUALITY: Int = 75
}
