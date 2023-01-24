package com.bitpunchlab.android.openaichatter.firebase

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import com.bitpunchlab.android.openaichatter.App_State
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class FirebaseClientViewModel(application: Application) : AndroidViewModel(application) {

    var auth : FirebaseAuth = FirebaseAuth.getInstance()

    private var authStateListener = FirebaseAuth.AuthStateListener {
        if (auth.currentUser != null) {
            // logged in
            _appState.postValue(App_State.LOGGED_IN)
        } else {
            // not logged in
            _appState.postValue(App_State.LOGGED_OUT)
        }
    }

    // these variable holds the live data user enter when loging in or creating account
    var _userName = MutableLiveData<String>()
    val userName get() = _userName

    var _userEmail = MutableLiveData<String>()
    val userEmail get() = _userEmail

    var _userPassword = MutableLiveData<String>()
    val userPassword get() = _userPassword

    var _userConfirmPassword = MutableLiveData<String>()
    val userConfirmPassword get() = _userConfirmPassword

    var nameError = MutableLiveData<String>()
    var emailError = MutableLiveData<String>()
    var passwordError = MutableLiveData<String>()
    var confirmPasswordError = MediatorLiveData<String>()

    var readyCreateAccountLiveData = MediatorLiveData<Boolean>()
    var readyLoginLiveData = MediatorLiveData<Boolean>()

    private var fieldsValidArray = ArrayList<Int>()
    private fun sumFieldsValue() : Boolean {
        return fieldsValidArray.sum() == 4
    }

    var _appState = MutableLiveData<App_State>(App_State.NORMAL)
    val appState get() = _appState

    private val nameValid: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(userName) { name ->
            if (name.isNullOrEmpty()) {
                nameError.value = "Name must not be empty."
                value = false
            } else {
                value = true
                nameError.value = ""
            }
        }
    }

    private val emailValid: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(userEmail) { email ->
            if (!email.isNullOrEmpty()) {
                if (!isEmailValid(email)) {
                    emailError.value = "Please enter a valid email."
                    value = false
                } else {
                    value = true
                    emailError.value = ""
                }
            } else {
                value = false
            }
            Log.i("email valid? ", value.toString())
        }
    }

    private val passwordValid: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(userPassword) { password ->
            if (!password.isNullOrEmpty()) {
                if (isPasswordContainSpace(password)) {
                    passwordError.value = "Password cannot has space."
                    value = false
                } else if (password.count() < 8) {
                    passwordError.value = "Password should be at least 8 characters."
                    value = false
                } else if (!isPasswordValid(password)) {
                    passwordError.value = "Password can only be composed of letters and numbers."
                    value = false
                } else {
                    passwordError.value = ""
                    value = true
                }
            } else {
                value = false
            }
            Log.i("password valid? ", value.toString())
        }
    }

    private val confirmPasswordValid: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(userConfirmPassword) { confirmPassword ->
            if (!confirmPassword.isNullOrEmpty()) {
                if (!isConfirmPasswordValid(userPassword.value!!, confirmPassword)) {
                    confirmPasswordError.value = "Passwords must be the same."
                    value = false
                } else {
                    confirmPasswordError.value = ""
                    value = true
                }
            } else {
                value = false
            }
            Log.i("confirm valid? ", value.toString())
        }
    }

    init {

        fieldsValidArray = arrayListOf(0,0,0,0)
        auth.addAuthStateListener(authStateListener)

        readyCreateAccountLiveData.addSource(nameValid) { valid ->
            if (valid) {
                fieldsValidArray[0] = 1
                // check other fields validity
                readyCreateAccountLiveData.value = sumFieldsValue()
            } else {
                fieldsValidArray[0] = 0
                readyCreateAccountLiveData.value = false
            }
        }

        readyCreateAccountLiveData.addSource(emailValid) { valid ->
            if (valid) {
                fieldsValidArray[1] = 1
                // check other fields validity
                readyCreateAccountLiveData.value = sumFieldsValue()
            } else {
                fieldsValidArray[1] = 0
                readyCreateAccountLiveData.value = false
            }
        }

        readyCreateAccountLiveData.addSource(passwordValid) { valid ->
            if (valid) {
                fieldsValidArray[2] = 1
                // check other fields validity
                readyCreateAccountLiveData.value = sumFieldsValue()
            } else {
                fieldsValidArray[2] = 0
                readyCreateAccountLiveData.value = false
            }
        }

        readyCreateAccountLiveData.addSource(confirmPasswordValid) { valid ->
            if (valid) {
                fieldsValidArray[3] = 1
                // check other fields validity
                readyCreateAccountLiveData.value = sumFieldsValue()
            } else {
                fieldsValidArray[3] = 0
                readyCreateAccountLiveData.value = false
            }
        }

        readyLoginLiveData.addSource(emailValid) { valid ->
            readyLoginLiveData.value = (valid && passwordValid.value != null && passwordValid.value!!)
        }

        readyLoginLiveData.addSource(passwordValid) { valid ->
            readyLoginLiveData.value = (valid && emailValid.value != null && emailValid.value!!)
        }
    }

    private fun isEmailValid(email: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String) : Boolean {
        val passwordPattern = Pattern.compile("^[A-Za-z0-9]{8,20}$")
        return passwordPattern.matcher(password).matches()
    }

    private fun isPasswordContainSpace(password: String) : Boolean {
        return password.contains(" ")
    }

    private fun isConfirmPasswordValid(password: String, confirmPassword: String) : Boolean {
        //Log.i("confirming password: ", "password: $password, confirm: $confirmPassword")
        return password == confirmPassword
    }

    suspend fun loginFirebaseAuth(email: String, password: String) =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
            auth
                .signInWithEmailAndPassword(email.lowercase(Locale.ROOT), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("login", "successfully logged in")
                        cancellableContinuation.resume(true) {}
                    } else {
                        Log.i("login", "failed to log in ")
                        cancellableContinuation.resume(false) {}
                }
        }
    }

    suspend fun createAccountFirebaseAuth(email: String, password: String) =
        suspendCancellableCoroutine<Boolean> { cancellableContinuation ->
            auth
                .createUserWithEmailAndPassword(email.lowercase(Locale.ROOT), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("create account", "created account")
                    } else {
                        Log.i("create account", "failed to create account")
                    }
                }
        }
}

class FirebaseClientViewModelFactory(private val application: Application)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirebaseClientViewModel::class.java)) {
            return FirebaseClientViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}