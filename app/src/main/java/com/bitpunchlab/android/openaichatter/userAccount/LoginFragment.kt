package com.bitpunchlab.android.openaichatter.userAccount

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bitpunchlab.android.openaichatter.R
import com.bitpunchlab.android.openaichatter.databinding.FragmentLoginBinding
import com.bitpunchlab.android.openaichatter.firebase.FirebaseClientViewModel
import com.bitpunchlab.android.openaichatter.firebase.FirebaseClientViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseClient : FirebaseClientViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        firebaseClient = ViewModelProvider(requireActivity(),
            FirebaseClientViewModelFactory(requireActivity().application))
            .get(FirebaseClientViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.firebaseClient = firebaseClient

        binding.buttonSend.setOnClickListener {
            //findNavController().navigate(R.id.toFragment)
        }

        binding.buttonCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.toCreateAccountAction)
        }

        firebaseClient.readyLoginLiveData.observe(viewLifecycleOwner, Observer { ready ->
            Log.i("login fragment", "ready to login")
        })

        binding.buttonSend.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                firebaseClient.loginFirebaseAuth(
                    firebaseClient.userEmail.value!!,
                    firebaseClient.userPassword.value!!
                )
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
}