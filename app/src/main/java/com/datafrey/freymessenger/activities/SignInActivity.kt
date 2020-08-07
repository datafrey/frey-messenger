package com.datafrey.freymessenger.activities

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.data
import com.datafrey.freymessenger.model.User
import com.datafrey.freymessenger.startActivity
import com.datafrey.freymessenger.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(R.layout.activity_sign_in) {

    companion object {
        private const val TAG = "SignInActivity"
    }

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val usersDatabaseReference by lazy { database.getReference("users") }

    private var signInModeActive = true

    fun signInSignUpButtonClick(view: View) {
        try {
            checkUserInputRequirements()

            val email = emailEditText.data
            val password = passwordEditText.data

            if (signInModeActive) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            startActivity<MainActivity>()
                            finish()
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            toast(R.string.authentication_failed_error_message)
                        }
                    }
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:success")
                            val firebaseUser = auth.currentUser
                            createUser(firebaseUser!!)
                            startActivity<MainActivity>()
                            finish()
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            toast(R.string.authentication_failed_error_message)
                        }
                    }
            }
        } catch (iae: IllegalArgumentException) {
            toast(iae.message.toString())
        }
    }

    private fun checkUserInputRequirements() {
        require(emailEditText.data != "") { getString(R.string.empty_email_field_error_message) }
        require(passwordEditText.data != "") { getString(R.string.empty_password_field_error_message) }
        require(passwordEditText.data.length > 6) { getString(R.string.too_short_password_error_message) }
        if (!signInModeActive) {
            require(repeatPasswordEditText.data != "") { getString(R.string.empty_repeat_password_field_error_message) }
            require(passwordEditText.data == repeatPasswordEditText.data) { getString(R.string.passwords_dont_match_error_message) }
            require(nameEditText.data != "") { getString(R.string.empty_name_field_error_message) }
        }
    }

    private fun createUser(firebaseUser: FirebaseUser) {
        val user = User(
            firebaseUser.uid,
            nameEditText.data,
            firebaseUser.email.toString().trim()
        )

        usersDatabaseReference.child(firebaseUser.uid).setValue(user)
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