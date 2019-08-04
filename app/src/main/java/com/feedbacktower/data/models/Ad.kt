package com.feedbacktower.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ads")
data class Ad(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val summary: String,
    val description: String,
    val businessId: String,
    val link: String,
    val duration: Long
)