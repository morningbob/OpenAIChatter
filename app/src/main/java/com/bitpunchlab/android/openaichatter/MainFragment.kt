package com.bitpunchlab.android.openaichatter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.navigation.fragment.findNavController
import com.bitpunchlab.android.openaichatter.databinding.FragmentMainBinding
import com.bitpunchlab.android.openaichatter.models.APIRequest
import com.bitpunchlab.android.openaichatter.openAI.OPENAI_API
import com.bitpunchlab.android.openaichatter.openAI.OpenAIAPIService
import com.bitpunchlab.android.openaichatter.openAI.OpenAIClientViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.await
import java.io.IOException

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.buttonRequest.setOnClickListener {
            //findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            CoroutineScope(Dispatchers.IO).launch {
                val responseString = requestCompletion()
                CoroutineScope(Dispatchers.Main).launch {
                    binding.textviewResponse.text = responseString
                }
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

    private suspend fun requestCompletion() : String? =
        suspendCancellableCoroutine<String?> { cancellableContinuation ->
            val request = APIRequest(model = "text-davinci-003", prompt = "hello there",
                temperature = 0.5, max_tokens = 25.0)
            try {
                val response = OPENAI_API.retrofit.getCompletion(request).execute()
                cancellableContinuation.resume(response.toString()) {}
            } catch (e: IOException) {
                Log.i("request completion","error getting a response ${e}")
                cancellableContinuation.resume(null) {}
            }
            //.await()
            //response.enqueue(Callback<ResponseBody>)

        }

}