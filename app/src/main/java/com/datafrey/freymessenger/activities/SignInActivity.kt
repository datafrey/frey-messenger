package com.datafrey.freymessenger.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.data
import com.datafrey.freymessenger.startActivity
import com.datafrey.freymessenger.toast
import com.datafrey.freymessenger.viewmodels.SignInViewModel
import com.datafrey.freymessenger.viewmodels.SignInViewModel.SignInViewModelFactory
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(R.layout.activity_sign_in) {

    private var signInModeActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this, SignInViewModelFactory())
            .get(SignInViewModel::class.java)

        viewModel.userInputErrorMessage.observe(this, Observer {
            toast(it)
        })

        viewModel.signInSuccessful.observe(this, Observer {
            if (it) {
                startActivity<MainActivity>()
                finish()
            } else {
                toast(R.string.authentication_failed_error_message)
            }
        })

        signInSignUpButton.setOnClickListener {
            val email = emailEditText.data
            val password = passwordEditText.data

            if (signInModeActive) {
                viewModel.signInToFirebase(email, password)
            } else {
                val repeatPassword = repeatPasswordEditText.data
                val name = nameEditText.data

                viewModel.signUpToFirebase(email, password, repeatPassword, name)
            }
        }
    }

    fun toggleModeTextViewClick(view: View) {
        repeatPasswordEditText.isVisible = !repeatPasswordEditText.isVisible
        nameEditText.isVisible = !nameEditText.isVisible

        if (signInModeActive) {
            signInSignUpButton.text = getString(R.string.sign_up_button_text)
            toggleSignInSignUpTextView.text = getString(R.string.toggle_mode_text_view_sign_up_mode_text)
        } else {
            signInSignUpButton.text = getString(R.string.sign_in_button_text)
            toggleSignInSignUpTextView.text = getString(R.string.toggle_mode_text_view_sign_in_mode_text)
        }
        signInModeActive = !signInModeActive
    }
}