package com.example.clothes.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Piece(
    override val id: String? = null,
    override val list: String? = null,
    override val category: String? = null,
    override val image: String? = null,
    val type: String? = null,
    val brand: String? = null,
    val color: String? = null,
    val size: String? = null,
): Cloth(id, list, category, image)