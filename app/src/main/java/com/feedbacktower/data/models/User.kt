package com.feedbacktower.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.feedbacktower.util.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val USER_ROW_ID = 0
@Entity(tableName = "user")
data class User(
    @SerializedName("city")
    var city: City?,
    @SerializedName("dob")
    var dob: String,
    @SerializedName("emailId")
    var emailId: String,
    @SerializedName("firstName")
    var firstName: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("profileSetup")
    var profileSetup: Boolean,
    @SerializedName("userType")
    var userType: String,
    @SerializedName("business")
    var business: MyBusiness?
): Serializable{
    @PrimaryKey(autoGenerate = false)
    val userRowId: Int = USER_ROW_ID
}