package com.datafrey.freymessenger.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.model.DatabaseNodeNames
import com.datafrey.freymessenger.model.StorageNodeNames
import com.datafrey.freymessenger.model.User
import com.datafrey.freymessenger.util.userinputvalidation.InputIsEmptyMiddleware
import com.datafrey.freymessenger.util.userinputvalidation.InputIsTooLongMiddleware
import com.datafrey.freymessenger.util.userinputvalidation.InputValidationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val currentUserDatabaseReference = FirebaseDatabase.getInstance()
        .getReference(DatabaseNodeNames.USERS_DB_NODE_NAME)
        .child(auth.currentUser!!.uid)

    private val profileIconsStorageReference by lazy {
        FirebaseStorage.getInstance()
            .reference.child(StorageNodeNames.PROFILE_ICONS_STORAGE_NODE_NAME)
    }

    private var _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User>
        get() = _currentUser

    private val _userInputErrorMessage = MutableLiveData<Int>()
    val userInputErrorMessage: LiveData<Int>
        get() = _userInputErrorMessage

    private val _showProfileChangesSavedMessage = MutableLiveData<Boolean>(false)
    val showProfileChangesSavedMessage: LiveData<Boolean>
        get() = _showProfileChangesSavedMessage

    fun profileChangesSavedMessageShown() {
        _showProfileChangesSavedMessage.value = false
    }

    init {
        attachCurrentUserDatabaseReferenceListener()
    }

    private fun attachCurrentUserDatabaseReferenceListener() {
        currentUserDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _currentUser.value = dataSnapshot.getValue(User::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun saveProfileChanges(
        name: String,
        bio: String,
        pickedProfileImageUri: Uri?
    ) {
        if (profileInputIsValid(name, bio)) {
            if (pickedProfileImageUri != null) {
                val newProfileIconReference = profileIconsStorageReference
                    .child(pickedProfileImageUri.lastPathSegment.toString())

                val uploadTask =
                    newProfileIconReference.putFile(pickedProfileImageUri)

                uploadTask.continueWithTask { newProfileIconReference.downloadUrl }
                    .addOnCompleteListener { p0 ->
                        if (p0.isSuccessful) {
                            saveProfileChangesToFirebase(name, bio, p0.result!!)
                        }
                    }
            } else {
                saveProfileChangesToFirebase(name, bio)
            }
        }
    }

    private fun profileInputIsValid(name: String, bio: String): Boolean {
        val nameValidator = InputIsEmptyMiddleware()
        nameValidator.linkWith(InputIsTooLongMiddleware(30))
        val bioValidator = InputIsTooLongMiddleware(200)

        val nameValidationResult = nameValidator.check(name)
        val bioValidationResult = bioValidator.check(bio)

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

        when (bioValidationResult) {
            InputValidationResult.INPUT_IS_TOO_LONG -> {
                _userInputErrorMessage.value = R.string.too_long_bio_error_message
                return false
            }

            else -> {
            }
        }

        return true
    }

    private fun saveProfileChangesToFirebase(name: String, bio: String, downloadUri: Uri? = null) {
        _currentUser.value!!.run {
            this.name = name
            this.bio = bio
            profilePictureUrl = downloadUri?.toString() ?: profilePictureUrl
        }

        currentUserDatabaseReference.setValue(_currentUser.value)

        _showProfileChangesSavedMessage.value = true
    }

    fun signOutFromFirebase() = auth.signOut()

    class ProfileViewModelFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}