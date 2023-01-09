package com.example.jeksonchat.presentation.ui.auth

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeksonchat.R
import com.example.jeksonchat.business.domain.models.User
import com.example.jeksonchat.utils.USER_LIST_PATH
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private var _isOpenChatByUser = MutableLiveData(false)
    val isOpenChatByUser: LiveData<Boolean>
        get() = _isOpenChatByUser

    private var _exceptionMessageForUser = MutableLiveData<String?>(null)
    val exceptionMessageForUser: LiveData<String?>
        get() = _exceptionMessageForUser

    private var _resMessageForUser = MutableLiveData<Int?>(null)
    val resMessageForUser: LiveData<Int?>
        get() = _resMessageForUser

    private var _btnSignInVisibility = MutableLiveData(true)
    val btnSignInVisibility: LiveData<Boolean> = _btnSignInVisibility

    private val _isSignUpPage = MutableLiveData(false)
    val isSignUpPage: LiveData<Boolean> = _isSignUpPage

    private val _isETLoginError = MutableLiveData<Boolean?>()
    val isETLoginError: LiveData<Boolean?> = _isETLoginError

    private val _isETUserNameError = MutableLiveData<Boolean?>()
    val isETUserNameError: LiveData<Boolean?> = _isETUserNameError

    private val _etPasswordError = MutableLiveData<Boolean?>()
    val etPasswordError: LiveData<Boolean?> = _etPasswordError

    private val _etConfirmPasswordError = MutableLiveData<Boolean?>()
    val etConfirmPasswordError: LiveData<Boolean?> = _etConfirmPasswordError

    private val email = MutableLiveData<CharSequence>()
    private val userName = MutableLiveData<CharSequence>()
    private val password = MutableLiveData<CharSequence>()
    private val confirmPassword = MutableLiveData<CharSequence>()

    private val userListRef = Firebase.database.getReference(USER_LIST_PATH)

    private val auth = Firebase.auth

    fun chooseEnter() {
        var deviseToken = "----------"
        Firebase.messaging.token.addOnCompleteListener {
            if (it.isSuccessful) deviseToken = it.result

            if (isSignUpPage.value == true) signUp(auth, deviseToken)
            else logIn(auth, deviseToken)
        }
    }

    fun onLoginTextChanged(it: CharSequence) {
        if (checkForWatcherEmail(it.toString())) {
            _isETLoginError.value = null
        } else {
            _isETLoginError.value = true
        }
        setEmail(it)
    }

    fun onNameTextChanged(it: CharSequence) {
        if (checkForWatcherUserName(it.toString())) {
            _isETUserNameError.value = null
        } else {
            _isETUserNameError.value = true
        }
        setUserName(it)
    }

    fun etPasswordTextChanged(it: CharSequence) {
        if (checkForWatcherPassword(it.toString())) {
            _etPasswordError.value = null
        } else {
            _etPasswordError.value = true
        }
        setPassword(it)
    }

    fun etConfirmPasswordTextChanged(it: CharSequence) {
        if (checkForWatcherConfirmPassword(password.value.toString(), it.toString())) {
            _etConfirmPasswordError.value = null
        } else {
            _etConfirmPasswordError.value = true
        }
        setConfirmPassword(it)
    }

    fun changeVisibilityAtProgress() {
        _btnSignInVisibility.postValue(!(btnSignInVisibility.value ?: false))
    }

    fun switchLogReg() {
        _isSignUpPage.value = !isSignUpPage.value!!
    }

    private fun checkForEmailMatch(): Boolean {
        return when {
            email.value.isNullOrEmpty() -> {
                _resMessageForUser.value = R.string.email_not_filled
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches() -> {
                _resMessageForUser.value = R.string.email_does_not_match
                false
            }
            else -> true
        }
    }

    private fun setEmail(value: CharSequence?) {
        value?.let { email.value = it }
    }

    private fun setUserName(value: CharSequence?) {
        value.let {
            if (it?.toString() != "") {
                userName.value = it
            }
        }
    }

    private fun setName(auth: FirebaseAuth) {
        auth.currentUser?.updateProfile(userProfileChangeRequest {
            displayName = userName.value.toString()
        })?.addOnCompleteListener {
            if (it.isSuccessful) {
                _isOpenChatByUser.value = true

            }
        }
    }

    private fun setPassword(value: CharSequence?) {
        value?.let { password.value = it }
    }

    private fun setConfirmPassword(value: CharSequence?) {
        value?.let { confirmPassword.value = it }
    }

    private fun checkForPasswordMatchSingIn(): Boolean {
        return when {
            password.value.isNullOrEmpty() -> {
                _resMessageForUser.value = R.string.passwords_not_filled
                false
            }
            password.value!!.length < 8 -> {
                _resMessageForUser.value = R.string.password_length_eight
                false
            }
            else -> true
        }
    }

    private fun logIn(auth: FirebaseAuth, deviseUserToken: String) {
        if (checkForEmailMatch() && checkForPasswordMatchSingIn()) {
            auth.signInWithEmailAndPassword(
                email.value.toString(),
                password.value.toString()
            )
                .addOnCompleteListener { task ->
                    // запустить прогресс бар
                    changeVisibilityAtProgress()

                    taskResultLogIn(task, deviseUserToken)
                }
        }
    }

    private fun signUp(auth: FirebaseAuth, deviseUserToken: String) {
        if (checkForEmailMatch() &&
            checkForPasswordsMatchSingUp() &&
            checkForUserNameEnter()
        ) {
            auth.createUserWithEmailAndPassword(
                email.value.toString(),
                password.value.toString(),
            )
                .addOnCompleteListener { task ->
                    // запустить прогресс бар
                    changeVisibilityAtProgress()

                    setName(auth)
                    taskResultSingUp(task, deviseUserToken)
                }
        }
    }

    private fun checkForUserNameEnter(): Boolean {
        return when {
            userName.value.isNullOrEmpty() -> {
                _resMessageForUser.value = R.string.user_name_not_filled
                false
            }
            else -> true
        }
    }

    private fun checkForPasswordsMatchSingUp(): Boolean {
        return when {
            password.value.isNullOrEmpty() || confirmPassword.value.isNullOrEmpty() -> {
                _resMessageForUser.value = R.string.passwords_not_filled
                false
            }
            password.value!!.length < 8 -> {
                _resMessageForUser.value = R.string.password_length_eight
                false
            }
            password.value.toString() != confirmPassword.value.toString() -> {
                _resMessageForUser.value = R.string.passwords_does_not_match
                false
            }
            else -> true
        }
    }

    private fun taskResultSingUp(task: Task<AuthResult>, deviseUserToken: String) {
        when {
            task.isSuccessful -> {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    if (userListRef.key?.contains(currentUser.uid) != true) {
                        val user = User(currentUser.uid, currentUser.email, userName.value.toString(), notificationTokens = listOf(deviseUserToken))
                        userListRef.child(currentUser.uid).setValue(user)
                    } else {
                        updateDBUserToken(currentUser, deviseUserToken)
                    }
                }
            }
            task.exception is Exception -> {
                task.exception?.localizedMessage?.let { message ->
                    _exceptionMessageForUser.value = message
                }
            }
        }
    }

    private fun taskResultLogIn(task: Task<AuthResult>, deviseUserToken: String) {
        when {
            task.isSuccessful -> {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    updateDBUserToken(currentUser, deviseUserToken)
                }
                _isOpenChatByUser.value = true
            }
            task.exception is Exception -> {
                task.exception?.localizedMessage?.let { message ->
                    _exceptionMessageForUser.value = message
                }
            }
        }
    }

    private fun updateDBUserToken(currentUser: FirebaseUser, deviseUserToken: String) {
        userListRef.child(currentUser.uid).child("notificationTokens").get().addOnSuccessListener {
            val dbUserToken: MutableList<String> = mutableListOf()
            it.children.forEach { notificationToken ->
                notificationToken.getValue(String::class.java)?.let { token -> dbUserToken.add(token) }
            }
            if (dbUserToken.isEmpty() || !dbUserToken.contains(deviseUserToken)) {
                dbUserToken.add(deviseUserToken)
                val user = User(currentUser.uid, currentUser.email, currentUser.displayName, notificationTokens = dbUserToken)
                userListRef.child(currentUser.uid).setValue(user)
            }
        }
    }

    private fun checkForWatcherEmail(email: String): Boolean {
        return email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkForWatcherUserName(userName: String): Boolean {
        return userName.isNotEmpty()
    }

    private fun checkForWatcherPassword(password: String): Boolean {
        return password.isEmpty() || password.length > 7
    }

    private fun checkForWatcherConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

}