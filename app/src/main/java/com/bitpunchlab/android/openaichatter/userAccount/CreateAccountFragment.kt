package com.bitpunchlab.android.openaichatter.userAccount

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.openaichatter.R
import com.bitpunchlab.android.openaichatter.databinding.FragmentCreateAccountBinding
import com.bitpunchlab.android.openaichatter.firebase.FirebaseClientViewModel
import com.bitpunchlab.android.openaichatter.firebase.FirebaseClientViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CreateAccountFragment : Fragment() {

    private var _binding : FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseClient : FirebaseClientViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        firebaseClient = ViewModelProvider(requireActivity(),
            FirebaseClientViewModelFactory(requireActivity().application))
            .get(FirebaseClientViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.firebaseClient = firebaseClient

        firebaseClient.readyCreateAccountLiveData.observe(viewLifecycleOwner, Observer { ready ->
            Log.i("create account", "ready to create account")
        })

        binding.buttonSend.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                firebaseClient.createAccountFirebaseAuth(
                    firebaseClient.userEmail.value!!,
                    firebaseClient.userPassword.value!!
                )
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}