package com.bitpunchlab.android.openaichatter.openAI

import com.bitpunchlab.android.openaichatter.Constants
import com.bitpunchlab.android.openaichatter.models.CompletionResponse
import com.bitpunchlab.android.openaichatter.models.ImageResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
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

    @POST("completions")
    fun getCompletion(@Body data: HashMap<String, Any>, @Header("Authorization") token: String,
        @Header("Content-Type") type: String) : Call<CompletionResponse>

    @POST("images/generations")
    fun getImage(@Body data: HashMap<String, Any>, @Header("Authorization") token: String,
         @Header("Content-Type") type: String) : Call<ImageResponse>

    @POST("images/variations")
    fun getImageEdited(@Body data: HashMap<String, Any>, @Header("Authorization") token: String,
        @Header("Content-Type") type: String) : Call<ImageResponse>
}