package com.datafrey.freymessenger.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.datafrey.freymessenger.*
import com.datafrey.freymessenger.activities.SignInActivity
import com.datafrey.freymessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    companion object {
        private const val RC_PROFILE_ICON_IMAGE_PICKER = 124
    }

    private val usersDatabaseReference = FirebaseDatabase.getInstance()
        .getReference("users")
    private var profileIconsStorageReference = FirebaseStorage.getInstance().reference
        .child("profile_icons")

    private lateinit var profileIconImageView: CircleImageView
    private var pickedProfileImageUri: Uri? = null

    private var currentUserInfo: User? = null

    private lateinit var root: View

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        root = inflater.inflate(R.layout.fragment_profile, container, false)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentUserInfoReference = usersDatabaseReference.child(firebaseUser!!.uid)

        currentUserInfoReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentUserInfo = dataSnapshot.getValue(User::class.java)

                root.nameEditText.setText(currentUserInfo?.name)
                root.bioEditText.setText(currentUserInfo?.bio)
                context?.loadImage(currentUserInfo?.profilePictureUrl!!, root.profileIconImageView)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        root.idTextView.text = "id: ${firebaseUser.uid}"

        profileIconImageView = root.profileIconImageView

        profileIconImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.image_picker_title)),
                RC_PROFILE_ICON_IMAGE_PICKER
            )
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.save_dialog_message)
                    .setPositiveButton(getString(R.string.dialog_positive_answer)) { dialog, _ ->
                        if (pickedProfileImageUri != null) {
                            val newProfileIconReference = profileIconsStorageReference
                                .child(pickedProfileImageUri!!.lastPathSegment.toString())

                            val uploadTask =
                                newProfileIconReference.putFile(pickedProfileImageUri!!)

                            uploadTask.continueWithTask { newProfileIconReference.downloadUrl }
                                .addOnCompleteListener { p0 ->
                                    if (p0.isSuccessful)
                                        saveChangesInCurrentUserInfo(p0.result!!)
                                }
                        } else
                            saveChangesInCurrentUserInfo()
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.dialog_negative_answer)) { dialog, _ -> dialog.cancel() }
                    .show()
            }

            R.id.sign_out -> {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.sign_out_dialog_message)
                    .setPositiveButton(getString(R.string.dialog_positive_answer)) { dialog, _ ->
                        FirebaseAuth.getInstance().signOut()
                        startActivity<SignInActivity>()
                        activity?.finish()
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.dialog_negative_answer)) { dialog, _ -> dialog.cancel() }
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveChangesInCurrentUserInfo(downloadUri: Uri? = null) {
        if (root.nameEditText.data.isEmpty()) {
            context?.toast(getString(R.string.empty_name_field_error_message), Toast.LENGTH_LONG)
            return
        }

        with (currentUserInfo!!) {
            with (root) {
                name = nameEditText.data
                bio = bioEditText.data
                profilePictureUrl = downloadUri?.toString() ?: profilePictureUrl
            }
        }

        usersDatabaseReference.child(currentUserInfo?.id!!).setValue(currentUserInfo)
        context?.toast(getString(R.string.profile_changes_saved_message))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_PROFILE_ICON_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            pickedProfileImageUri = data?.data!!
            profileIconImageView.run { setImageURI(data.data) }
        }
    }
}