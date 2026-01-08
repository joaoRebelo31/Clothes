package com.example.clothes.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Combination(
    override val id: String? = null,
    override val list: String? = null,
    override val category: String? = null,
    override val image: String? = null,
    var piece1: Piece? = null,
    val piece2: Piece? = null
): Cloth(id, list, category, image)