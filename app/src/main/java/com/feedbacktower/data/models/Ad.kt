package com.feedbacktower.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "ads")
data class Ad(
    @ColumnInfo(name = "ad_id")
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("subtitle")
    val subtitle: String,
    @SerializedName("info")
    val info: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("media")
    val media: String,
    @Embedded
    @SerializedName("business")
    val business: Business
):Serializable {
    data class Business(
        @ColumnInfo(name = "business_id")
        @SerializedName("id")
        val id: String,
        @ColumnInfo(name = "business_name")
        @SerializedName("name")
        val name: String
    ):Serializable
}