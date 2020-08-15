package com.datafrey.freymessenger.presenters

import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.activities.MainActivity
import com.datafrey.freymessenger.activities.SignInActivity
import com.datafrey.freymessenger.model.User
import com.datafrey.freymessenger.startActivity
import com.datafrey.freymessenger.toast
import com.datafrey.freymessenger.userinputvalidation.InputIsEmptyMiddleware
import com.datafrey.freymessenger.userinputvalidation.InputIsTooLongMiddleware
import com.datafrey.freymessenger.userinputvalidation.InputIsTooShortMiddleware
import com.datafrey.freymessenger.userinputvalidation.InputValidationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignInPresenter(private var view: SignInActivity?) {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val usersDatabaseReference by lazy { database.getReference("users") }

    fun detachView() {
        view = null
    }

    fun signInToFirebase(email: String, password: String) {
        if (signInInputIsValid(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        view!!.startActivity<MainActivity>()
                        view!!.finish()
                    } else {
                        view!!.toast(R.string.authentication_failed_error_message)
                    }
                }
        }
    }

    fun signUpToFirebase(
        email: String,
        password: String,
        repeatPassword: String,
        name: String
    ) {
        if (signUpInputIsValid(email, password, repeatPassword, name)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser!!
                        val user = User(
                            firebaseUser.uid,
                            name,
                            email
                        )

                        usersDatabaseReference.child(firebaseUser.uid).setValue(user)

                        view!!.startActivity<MainActivity>()
                        view!!.finish()
                    } else {
                        view!!.toast(R.string.authentication_failed_error_message)
                    }
                }
        }
    }

    private fun signInInputIsValid(email: String, password: String): Boolean {
        val emailValidator = InputIsEmptyMiddleware()
        val passwordValidator = InputIsEmptyMiddleware()
        passwordValidator.linkWith(InputIsTooShortMiddleware(7))

        val emailValidationResult = emailValidator.check(email)
        val passwordValidationResult = passwordValidator.check(password)

        when (emailValidationResult) {
            InputValidationResult.INPUT_IS_EMPTY -> {
                view!!.toast(R.string.empty_email_field_error_message)
                return false
            }
        }

        when (passwordValidationResult) {
            InputValidationResult.INPUT_IS_EMPTY -> {
                view!!.toast(R.string.empty_password_field_error_message)
                return false
            }

            InputValidationResult.INPUT_IS_TOO_SHORT -> {
                view!!.toast(R.string.too_short_password_error_message)
                return false
            }
        }

        return true
    }

    private fun signUpInputIsValid(
        email: String,
        password: String,
        repeatPassword: String,
        name: String
    ): Boolean {
        val emailAndPasswordAreValid = signInInputIsValid(email, password)
        if (emailAndPasswordAreValid) {
            val repeatPasswordValidator = InputIsEmptyMiddleware()
            val nameValidator = InputIsEmptyMiddleware()
            nameValidator.linkWith(InputIsTooLongMiddleware(30))

            val repeatPasswordValidationResult = repeatPasswordValidator.check(repeatPassword)
            val nameValidationResult = nameValidator.check(name)

            when (repeatPasswordValidationResult) {
                InputValidationResult.INPUT_IS_EMPTY -> {
                    view!!.toast(R.string.empty_repeat_password_field_error_message)
                    return false
                }
            }

            if (password != repeatPassword) {
                view!!.toast(R.string.passwords_dont_match_error_message)
                return false
            }

            when (nameValidationResult) {
                InputValidationResult.INPUT_IS_EMPTY -> {
                    view!!.toast(R.string.empty_name_field_error_message)
                    return false
                }

                InputValidationResult.INPUT_IS_TOO_LONG -> {
                    view!!.toast(R.string.too_long_name_error_message)
                    return false
                }
            }
        } else {
            return false
        }

        return true
    }

}