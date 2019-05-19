package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Suggestion(
    @SerializedName("businessId")
    val businessId: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("repliedAt")
    val repliedAt: String,
    @SerializedName("reply")
    val reply: String?,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("user")
    val user: User?,
    @SerializedName("business")
    val business: Business?,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("visible")
    val visible: Boolean
) : Serializable {
    val displayName: String
        get() {
            return if (user != null) {
                user.firstName + " " + user.lastName
            } else {
                return if (business != null) {
                    "${business.name}"
                } else {
                    "Unknown"
                }
            }
        }
    val displayId: String
        get() {
            return if (user != null) {
                userId
            } else {
                business?.userId ?: ""
            }
        }
    val isReplied: Boolean
        get() {
            return !reply.isNullOrEmpty()
        }
}