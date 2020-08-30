package com.datafrey.freymessenger.main

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.loadImage
import com.datafrey.freymessenger.model.DatabaseNodeNames
import com.datafrey.freymessenger.model.StorageNodeNames
import com.datafrey.freymessenger.model.User
import com.datafrey.freymessenger.toast
import com.datafrey.freymessenger.userinputvalidation.InputIsEmptyMiddleware
import com.datafrey.freymessenger.userinputvalidation.InputIsTooLongMiddleware
import com.datafrey.freymessenger.userinputvalidation.InputValidationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfilePresenter(private var view: View?) {

    private val usersDatabaseReference by lazy {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseNodeNames.USERS_DB_NODE_NAME)
    }

    private val profileIconsStorageReference by lazy {
        FirebaseStorage.getInstance()
            .reference.child(StorageNodeNames.PROFILE_ICONS_STORAGE_NODE_NAME)
    }

    private var currentUserInfo: User? = null

    init {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentUserInfoReference = usersDatabaseReference.child(firebaseUser!!.uid)

        currentUserInfoReference.addValueEventListener(object: ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentUserInfo = dataSnapshot.getValue(User::class.java)
                view!!.run {
                    currentUserInfo!!.run {
                        nameEditText.setText(name)
                        bioEditText.setText(bio)
                        context?.loadImage(profilePictureUrl, profileIconImageView)
                        idTextView.text = "id: $id"
                    }
                }
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
                            saveChangesInCurrentUserInfo(name, bio, p0.result!!)
                        }
                    }
            } else {
                saveChangesInCurrentUserInfo(name, bio)
            }
        }
    }

    private fun profileInputIsValid(name: String, bio: String): Boolean {
        val nameValidator = InputIsEmptyMiddleware()
        nameValidator.linkWith(InputIsTooLongMiddleware(30))
        val bioValidator = InputIsTooLongMiddleware(200)

        val nameValidationResult = nameValidator.check(name)
        val bioValidationResult = bioValidator.check(bio)

        with(view!!.context) {
            when (nameValidationResult) {
                InputValidationResult.INPUT_IS_EMPTY -> {
                    toast(R.string.empty_name_field_error_message)
                    return false
                }

                InputValidationResult.INPUT_IS_TOO_LONG -> {
                    toast(R.string.too_long_name_error_message)
                    return false
                }

                else -> {
                }
            }

            when (bioValidationResult) {
                InputValidationResult.INPUT_IS_TOO_LONG -> {
                    toast(R.string.too_long_bio_error_message)
                    return false
                }

                else -> {
                }
            }
        }

        return true
    }

    private fun saveChangesInCurrentUserInfo(name: String, bio: String, downloadUri: Uri? = null) {
        currentUserInfo!!.run {
            this.name = name
            this.bio = bio
            profilePictureUrl = downloadUri?.toString() ?: profilePictureUrl
        }

        usersDatabaseReference.child(currentUserInfo?.id!!).setValue(currentUserInfo)
        view!!.context.toast(R.string.profile_changes_saved_message)
    }

    fun signOutFromFirebase() = FirebaseAuth.getInstance().signOut()

    fun detachView() {
        view = null
    }

}