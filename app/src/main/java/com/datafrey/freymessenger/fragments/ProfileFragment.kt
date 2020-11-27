package com.datafrey.freymessenger.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.activities.SignInActivity
import com.datafrey.freymessenger.data
import com.datafrey.freymessenger.databinding.FragmentProfileBinding
import com.datafrey.freymessenger.startActivity
import com.datafrey.freymessenger.toast
import com.datafrey.freymessenger.viewmodels.ProfileViewModel
import com.datafrey.freymessenger.viewmodels.ProfileViewModel.ProfileViewModelFactory
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    companion object {
        private const val RC_PROFILE_ICON_IMAGE_PICKER = 124
    }

    private lateinit var root: View
    private lateinit var viewModel: ProfileViewModel

    private var pickedProfileImageUri: Uri? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        root = inflater.inflate(R.layout.fragment_profile, container, false)
        val binding = FragmentProfileBinding.bind(root)

        viewModel = ViewModelProvider(this, ProfileViewModelFactory())
            .get(ProfileViewModel::class.java)

        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            binding.currentUser = it
        })

        viewModel.userInputErrorMessage.observe(viewLifecycleOwner, Observer {
            requireContext().toast(it)
        })

        viewModel.showProfileChangesSavedMessage.observe(viewLifecycleOwner, Observer {
            if (it) {
                requireContext().toast(R.string.profile_changes_saved_message)
                viewModel.profileChangesSavedMessageShown()
            }
        })

        root.profileIconImageView.setOnClickListener {
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
                        val name = root.nameEditText.data
                        val bio = root.bioEditText.data
                        viewModel.saveProfileChanges(name, bio,
                            pickedProfileImageUri)
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.dialog_negative_answer)) { dialog, _ -> dialog.cancel() }
                    .show()
            }

            R.id.sign_out -> {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.sign_out_dialog_message)
                    .setPositiveButton(getString(R.string.dialog_positive_answer)) { dialog, _ ->
                        viewModel.signOutFromFirebase()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_PROFILE_ICON_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            pickedProfileImageUri = data?.data!!
            root.profileIconImageView.run { setImageURI(data.data) }
        }
    }
}