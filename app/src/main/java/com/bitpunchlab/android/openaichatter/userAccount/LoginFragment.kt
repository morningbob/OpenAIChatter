package com.bitpunchlab.android.openaichatter.userAccount

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bitpunchlab.android.openaichatter.App_State
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

        binding.buttonCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.toCreateAccountAction)
        }

        firebaseClient.readyLoginLiveData.observe(viewLifecycleOwner, Observer { ready ->
            Log.i("login fragment", "ready to login")
            // show send button
        })

        firebaseClient.appState.observe(viewLifecycleOwner, appStateObserver)

        binding.buttonSend.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (firebaseClient.loginFirebaseAuth(
                        firebaseClient.userEmail.value!!,
                        firebaseClient.userPassword.value!!)) {
                    firebaseClient._appState.postValue(App_State.LOGGED_IN)
                    }
                else {
                    // error alert
                    loginFailureAlert()
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

    private val appStateObserver = Observer<App_State> { state ->
        when (state) {
            App_State.LOGGED_IN -> {
                findNavController().navigate(R.id.toMainAction)
            }
            App_State.LOGGED_OUT -> 0
            else -> 0
        }
    }

    private fun loginSuccessAlert() {
        val successAlert = AlertDialog.Builder(context)

        with(successAlert) {
            setTitle("Login Success")
            setMessage("You logged in successfully.")
            setPositiveButton("OK", DialogInterface.OnClickListener { dialog, button ->
                // do nothing
            })
            show()
        }
    }

    private fun loginFailureAlert() {
        val failureAlert = AlertDialog.Builder(context)

        with(failureAlert) {
            setTitle("Login Failed")
            setMessage("There is an error logging in.  Please check your email and password.")
            setPositiveButton("OK", DialogInterface.OnClickListener { dialog, button ->
                // do nothing
            })
            show()
        }
    }
}