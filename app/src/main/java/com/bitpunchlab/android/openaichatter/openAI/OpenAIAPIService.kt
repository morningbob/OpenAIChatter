package com.bitpunchlab.android.openaichatter.openAI

import com.bitpunchlab.android.openaichatter.Constants
import com.bitpunchlab.android.openaichatter.models.APIRequest
import com.bitpunchlab.android.openaichatter.models.APIResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

const val BASE_URL = "https://api.openai.com/v1/"

object OPENAI_API {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenAIAPIService::class.java)
}

interface OpenAIAPIService {
    //"Content-Type: application/json"})
    //val requestBody = ResponseBody
    @Headers("Authorization: Bearer ${Constants.OPENAI_API_KEY}",
        "Content-Type: application/json")
    @POST("completions")
    //fun getCompletion() : Call<APIResponse>
    fun getCompletion(@Body data: APIRequest) : Call<ResponseBody>
}