package com.example.clothes.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Profile(
    val image: String? = null,
    val username: String? = null,
    val posts: List<Post>? = null
): Serializable
