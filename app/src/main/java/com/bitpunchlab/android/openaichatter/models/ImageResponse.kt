package com.bitpunchlab.android.openaichatter.models

data class ImageResponse(
    var data : List<UrlObject>//UrlArray
) : java.io.Serializable

data class UrlArray(
    var urls : List<UrlObject>
) : java.io.Serializable

data class UrlObject(
    var url : String
)
