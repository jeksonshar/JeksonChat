package com.example.jeksonchat.presentation.ui.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.jeksonchat.R
import com.example.jeksonchat.databinding.ActivityAuthBinding
import com.example.jeksonchat.presentation.ui.chat.ChatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private var _binding: ActivityAuthBinding? = null
    private val binding: ActivityAuthBinding
        get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        binding.lifecycleOwner = this

        binding.vm = viewModel

        viewModel.isOpenChatByUser.observe(this) {
            if (it) {
                moveToChat()
            }
        }

        viewModel.exceptionMessageForUser.observe(this) {
            if (!it.isNullOrEmpty()) {
                showSnackBar(it)
            }
        }

        viewModel.resMessageForUser.observe(this) {
            if (it != null) {
                showSnackBar(applicationContext.getString(it))
            }
        }

        if (Firebase.auth.currentUser != null) moveToChat()
    }

    private fun moveToChat() {
        startActivity(newIntent(this, ChatActivity()))
        finish()
    }

    private fun showSnackBar(message: String) {
        val mySnackBar = Snackbar
            .make(binding.root, message, Snackbar.LENGTH_INDEFINITE)

        mySnackBar.setAction("Ok") {
            if (!binding.btnSignIn.isVisible) {
                viewModel.changeVisibilityAtProgress()
            }
            mySnackBar.dismiss()
        }
        mySnackBar.show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        fun newIntent(context: Context, activity: AppCompatActivity): Intent {
            return Intent(context, activity::class.java)
        }
    }
}