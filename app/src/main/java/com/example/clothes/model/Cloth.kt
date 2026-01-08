package com.example.clothes.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
open class Cloth (
    open val id: String? = null,
    open val category: String? = null,
    open val list: String? = null,
    open val image: String? = null
): Serializable