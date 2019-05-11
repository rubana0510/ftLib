package com.feedbacktower.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.feedbacktower.util.Constants
import com.google.gson.annotations.SerializedName
const val USER_ROW_ID = 0
@Entity(tableName = "user")
data class User(
    @SerializedName("city")
    var city: Any?,
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
    var business: Business?
){
    val profileImage: String
    get(){
        return Constants.Service.Secrets.BASE_URL + "/user/" + id
    }
    @PrimaryKey(autoGenerate = false)
    val userRowId: Int = USER_ROW_ID
}