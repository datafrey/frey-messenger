package com.datafrey.freymessenger.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.model.DatabaseNodeNames
import com.datafrey.freymessenger.model.User
import com.datafrey.freymessenger.userinputvalidation.InputIsEmptyMiddleware
import com.datafrey.freymessenger.userinputvalidation.InputIsTooLongMiddleware
import com.datafrey.freymessenger.userinputvalidation.InputIsTooShortMiddleware
import com.datafrey.freymessenger.userinputvalidation.InputValidationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignInViewModel : ViewModel() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val usersDatabaseReference by lazy {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseNodeNames.USERS_DB_NODE_NAME)
    }

    private val _signInSuccessful = MutableLiveData<Boolean>()
    val signInSuccessful: LiveData<Boolean>
        get() = _signInSuccessful

    private val _userInputErrorMessage = MutableLiveData<Int>()
    val userInputErrorMessage: LiveData<Int>
        get() = _userInputErrorMessage

    fun signInToFirebase(email: String, password: String) {
        if (signInInputIsValid(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _signInSuccessful.value = task.isSuccessful
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
                    }
                    _signInSuccessful.value = task.isSuccessful
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
                _userInputErrorMessage.value = R.string.empty_email_field_error_message
                return false
            }

            else -> {
            }
        }

        when (passwordValidationResult) {
            InputValidationResult.INPUT_IS_EMPTY -> {
                _userInputErrorMessage.value = R.string.empty_password_field_error_message
                return false
            }

            InputValidationResult.INPUT_IS_TOO_SHORT -> {
                _userInputErrorMessage.value = R.string.too_short_password_error_message
                return false
            }

            else -> {
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
        if (emailAndPasswordAreValid) return false

        val repeatPasswordValidator = InputIsEmptyMiddleware()
        val nameValidator = InputIsEmptyMiddleware()
        nameValidator.linkWith(InputIsTooLongMiddleware(30))

        val repeatPasswordValidationResult = repeatPasswordValidator.check(repeatPassword)
        val nameValidationResult = nameValidator.check(name)

        when (repeatPasswordValidationResult) {
            InputValidationResult.INPUT_IS_EMPTY -> {
                _userInputErrorMessage.value = R.string.empty_repeat_password_field_error_message
                return false
            }

            else -> {
            }
        }

        if (password != repeatPassword) {
            _userInputErrorMessage.value = R.string.passwords_dont_match_error_message
            return false
        }

        when (nameValidationResult) {
            InputValidationResult.INPUT_IS_EMPTY -> {
                _userInputErrorMessage.value = R.string.empty_name_field_error_message
                return false
            }

            InputValidationResult.INPUT_IS_TOO_LONG -> {
                _userInputErrorMessage.value = R.string.too_long_name_error_message
                return false
            }

            else -> {
            }
        }

        return true
    }

    class SignInViewModelFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
                return SignInViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}