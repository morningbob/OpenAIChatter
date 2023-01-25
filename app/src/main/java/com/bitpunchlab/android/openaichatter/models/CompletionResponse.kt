package com.bitpunchlab.android.openaichatter.models

data class CompletionResponse(
    var choices : List<Choice>

) : java.io.Serializable

data class Choice(
    var text : String
) : java.io.Serializable
