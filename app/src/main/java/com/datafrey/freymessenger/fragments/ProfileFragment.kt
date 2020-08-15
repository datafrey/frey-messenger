package com.datafrey.freymessenger.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.activities.SignInActivity
import com.datafrey.freymessenger.data
import com.datafrey.freymessenger.presenters.ProfilePresenter
import com.datafrey.freymessenger.startActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    companion object {
        private const val RC_PROFILE_ICON_IMAGE_PICKER = 124
    }

    private lateinit var presenter: ProfilePresenter

    private lateinit var profileIconImageView: CircleImageView
    private var pickedProfileImageUri: Uri? = null

    private lateinit var root: View

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        root = inflater.inflate(R.layout.fragment_profile, container, false)
        presenter = ProfilePresenter(root)
        
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
                        val name = root.nameEditText.data
                        val bio = root.bioEditText.data
                        presenter.saveProfileChanges(name, bio,
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
                        presenter.signOutFromFirebase()
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
            profileIconImageView.run { setImageURI(data.data) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}