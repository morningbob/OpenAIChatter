package com.bitpunchlab.android.openaichatter.openAI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.openaichatter.firebase.FirebaseClientViewModel

class OpenAIClientViewModel(application: Application) : AndroidViewModel(application) {


}

class OpenAIClientViewModelFactory(private val application: Application)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OpenAIClientViewModel::class.java)) {
            return OpenAIClientViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}