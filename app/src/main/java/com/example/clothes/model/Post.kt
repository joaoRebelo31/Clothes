package com.example.clothes.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Post (
    val id: String? = null,
    val profileImage: String? = null,
    val profileUsername: String? = null,
    val image: String? = null,
    val piece1: Piece? = null,
    val piece2: Piece? = null,
) : Serializable {
}