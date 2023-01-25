package com.bitpunchlab.android.openaichatter.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
class API_Request {
    var model = ""
    var prompt = ""
    var temperature = 0
    var max_tokens = 25

    init(var enginee: String, var input: String, temp: Int, tokens: Int) {
        model = enginee
    }
}
*/
 @Parcelize
data class APIRequest(
    var model : String = "",
    var prompt : String = "",
    var temperature : Double = 0.0,
    var max_tokens : Double = 25.0
) : Parcelable