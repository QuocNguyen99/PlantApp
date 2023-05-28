package com.example.plantapp.data.response

data class DefaultImage(
    var license: Int = -1,
    var license_name: String? = "",
    var license_url: String? = "",
    var medium_url: String? = "",
    var original_url: String? = "",
    var regular_url: String? = "",
    var small_url: String? = "",
    var thumbnail: String? = ""
)