package com.bitpunchlab.android.openaichatter

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.navigation.fragment.findNavController
import com.bitpunchlab.android.openaichatter.Constants.OPENAI_API_KEY
import com.bitpunchlab.android.openaichatter.databinding.FragmentMainBinding
import com.bitpunchlab.android.openaichatter.models.CompletionResponse
import com.bitpunchlab.android.openaichatter.openAI.OPENAI_API
import com.bitpunchlab.android.openaichatter.openAI.OpenAIAPIService
import com.bitpunchlab.android.openaichatter.openAI.OpenAIClientViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.buttonCompletion.setOnClickListener {
            val prompt = binding.editTextPrompt.text.toString()
            if (prompt != null && prompt != "") {
                processCompletionRequest(prompt)

            } else {
                // alert user blank prompt
            }
        }

        binding.buttonCreateImage.setOnClickListener {
            val prompt = binding.editTextPrompt.text.toString()
            if (prompt != null && prompt != "") {
                processCreateImageRequest(prompt)

            } else {
                // alert user blank prompt
            }
        }

        binding.buttonEditImage.setOnClickListener {
            val prompt = binding.editTextPrompt.text.toString()
            if (prompt != null && prompt != "") {
                processEditImageRequest(prompt)

            } else {
                // alert user blank prompt
            }
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun requestCompletion(prompt: String) : String? =
        suspendCancellableCoroutine<String?> { cancellableContinuation ->

            val requestData = java.util.HashMap<String, Any>()
            requestData["model"] = "text-davinci-003"
            requestData["prompt"] = prompt
            requestData["temperature"] = 0.5
            requestData["max_tokens"] = 2048

            try {
                val response = OPENAI_API.retrofit.getCompletion(
                    requestData, "Bearer $OPENAI_API_KEY",
                    "application/json"
                ).execute()
                val textResponse = response.body()
                if (textResponse != null) {
                    cancellableContinuation.resume(textResponse.choices[0].text) {}
                } else {
                    cancellableContinuation.resume(null) {}
                }
            } catch (e: IOException) {
                Log.i("request completion", "error getting a response ${e}")
                cancellableContinuation.resume(null) {}
            }
        }

    private fun processCompletionRequest(prompt: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = requestCompletion(prompt)
            response?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.textviewResponse.text = response
                }
            }
        }
    }

    private suspend fun requestCreateImage(prompt: String) : String? =
        suspendCancellableCoroutine<String?> { cancellableContinuation ->

            val requestData = java.util.HashMap<String, Any>()
            //requestData["model"] = "text-davinci-003"
            requestData["prompt"] = prompt
            requestData["n"] = 1
            requestData["size"] = "1024x1024"

            try {
                val response = OPENAI_API.retrofit.getImage(
                    requestData, "Bearer $OPENAI_API_KEY",
                    "application/json"
                ).execute()
                val imageResponse = response.body()//?.string()
                Log.i("response object", response.toString())
                cancellableContinuation.resume(imageResponse!!.data!![0].url) {}
            } catch (e: IOException) {
                Log.i("request completion", "error getting a response ${e}")
                cancellableContinuation.resume(null) {}
            }
        }

    private fun processCreateImageRequest(prompt: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = requestCreateImage(prompt)
            response?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.i("got back url", response)
                    displayImage(response)
                }
            }
        }
    }

    private fun displayImage(urlString: String) {
        Glide
            .with(requireContext())
            .load(urlString)
            .into(binding.imageResponse)
    }

    private suspend fun requestEditImage(prompt: String) : String? =
        suspendCancellableCoroutine<String?> { cancellableContinuation ->

            val requestData = java.util.HashMap<String, Any>()
            //requestData["model"] = "text-davinci-003"
            requestData["image"] = preprocessImage()
            //requestData["prompt"] = prompt
            requestData["n"] = 1
            requestData["size"] = "1024x1024"

            try {
                val response = OPENAI_API.retrofit.getImageEdited(
                    requestData, "Bearer $OPENAI_API_KEY",
                    "application/json"
                ).execute()
                val imageResponse = response.body()//?.string()
                Log.i("response object", response.toString())
                if (imageResponse != null) {
                    cancellableContinuation.resume(imageResponse.data[0].url) {}
                } else {
                    Log.i("request edit image", "error getting image")
                    cancellableContinuation.resume(null) {}
                }
            } catch (e: IOException) {
                Log.i("request completion", "error getting a response ${e}")
                cancellableContinuation.resume(null) {}
            }
        }

    private fun preprocessImage() : String {
        val bitmap = BitmapFactory.decodeResource(binding.imageResponse.resources, R.drawable.login)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val byteArray = baos.toByteArray()
        val encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return encodedString
    }

    private fun processEditImageRequest(prompt: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = requestEditImage(prompt)
            response?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.i("got back url", response)
                    displayImage(response)
                }
            }
        }
    }

}

//val jsonData = response.body()?.string()
//val sen = Gson().fromJson<CompletionResponse>(jsonData, CompletionResponse::class.java)