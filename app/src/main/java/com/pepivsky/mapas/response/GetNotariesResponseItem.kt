package com.pepivsky.mapas.response

data class GetNotariesResponseItem(
    val city_id: String,
    val distance: Double,
    val dpt_id: String,
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val notary_id: String
)