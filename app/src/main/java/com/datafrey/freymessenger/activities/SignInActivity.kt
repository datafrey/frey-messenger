package com.datafrey.freymessenger.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.data
import com.datafrey.freymessenger.presenters.SignInPresenter
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(R.layout.activity_sign_in) {

    private var signInModeActive = true
    private lateinit var presenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter =
            SignInPresenter(this)

        signInSignUpButton.setOnClickListener {
            val email = emailEditText.data
            val password = passwordEditText.data

            if (signInModeActive) {
                presenter.signInToFirebase(email, password)
            } else {
                val repeatPassword = repeatPasswordEditText.data
                val name = nameEditText.data

                presenter.signUpToFirebase(email, password, repeatPassword, name)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
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