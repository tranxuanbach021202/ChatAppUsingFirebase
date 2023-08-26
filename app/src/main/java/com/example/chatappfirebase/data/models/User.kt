package com.example.chatappfirebase.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize


@IgnoreExtraProperties
@Parcelize
data class User(
    var uid: String? ="",
    var name: String? = "",
    var imgProfile: String? = "",
    var gender: String? = "",
    var phoneNumber: String? = "",
    var address: String? = "",
    var status: String? = ""
) : Parcelable {
    @Exclude
    fun toMap() : Map<String, String?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "imgProfile" to imgProfile,
            "gender" to gender,
            "phoneNumber" to phoneNumber,
            "address" to address,
            "status" to status
        )
    }

}
