package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("businessId")
    val businessId: String,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("visible")
    val visible: Boolean,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("user")
    val user: User?,
    @SerializedName("business")
    val business: Business
) : BaseModel(id) {
    val reviewerName: String
        get() {
            return if (user != null) {
                "${user.firstName} ${user.lastName}"
            } else {
               "Unknown user"
            }
        }
    val profileId: String
        get() {
            return user?.id ?: business.userId
        }
}

