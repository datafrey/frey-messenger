package com.datafrey.freymessenger.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.adapters.MessageAdapter
import com.datafrey.freymessenger.data
import com.datafrey.freymessenger.loadImage
import com.datafrey.freymessenger.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_activity_action_bar.view.*

class ChatActivity : AppCompatActivity(R.layout.activity_chat) {

    companion object {
        private const val RC_SEND_IMAGE_IMAGE_PICKER = 123
    }

    private var auth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance()
    private var messagesDatabaseReference = database.reference
        .child("messages")

    private var storage = FirebaseStorage.getInstance()
    private var chatImagesStorageReference = storage.reference
        .child("chat_images")

    private lateinit var senderUserId: String
    private lateinit var senderUserName: String
    private lateinit var recipientUserId: String
    private lateinit var recipientUserName: String
    private lateinit var recipientUserProfilePictureUrl: String

    private var messages = mutableListOf<Message>()
    private lateinit var messageListViewAdapter: MessageAdapter

    private var layoutIsChangingByUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        readIntentInfo()
        setupActionBar()
        setupMessageListView()
        setupSendMessageLayout()
        attachMessagesDatabaseReferenceChildEventListener()
    }

    private fun readIntentInfo() {
        with(intent) {
            senderUserId = auth.currentUser?.uid.toString()
            senderUserName = getStringExtra("senderUserName").toString()
            recipientUserId = getStringExtra("recipientUserId").toString()
            recipientUserName = getStringExtra("recipientUserName").toString()
            recipientUserProfilePictureUrl = getStringExtra("recipientUserProfilePictureUrl").toString()
        }
    }

    private fun setupActionBar() {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater
        val view = inflater.inflate(R.layout.chat_activity_action_bar, null)

        with (view) {
            loadImage(recipientUserProfilePictureUrl, userIconImageView)
            userNameTextView.text = recipientUserName
        }

        with (supportActionBar!!) {
            setDisplayShowCustomEnabled(true)
            customView = view
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupMessageListView() {
        messageListViewAdapter = MessageAdapter(this, R.layout.message_item, messages)

        with (messageListView) {
            adapter = messageListViewAdapter

            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                if (!layoutIsChangingByUser)
                    messageListView.post { messageListView.setSelection(messageListView.count - 1) }
            }

            setOnTouchListener { _, _ ->
                layoutIsChangingByUser = true
                false
            }
        }
    }

    private fun setupSendMessageLayout() {
        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sendMessageButton.isEnabled = p0.toString().trim().isNotEmpty()
            }
        })

        sendMessageButton.setOnClickListener {
            val message = Message(
                text = messageEditText.data,
                senderId = senderUserId,
                recipientId = recipientUserId
            )

            messagesDatabaseReference.push().setValue(message)

            messageEditText.setText("")
        }

        sendPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.image_picker_title)),
                RC_SEND_IMAGE_IMAGE_PICKER
            )
        }
    }

    private fun attachMessagesDatabaseReferenceChildEventListener() {
        messagesDatabaseReference.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                with(message!!) {
                    if (senderId == auth.currentUser?.uid && recipientId == recipientUserId) {
                        isMine = true
                        messageListViewAdapter.add(this)
                    } else if (recipientId == auth.currentUser?.uid && senderId == recipientUserId) {
                        isMine = false
                        messageListViewAdapter.add(this)
                    }
                }

                messageListView.post { messageListView.setSelection(messageListView.count) }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SEND_IMAGE_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            val imageReference = chatImagesStorageReference
                .child(selectedImageUri?.lastPathSegment.toString())

            val uploadTask = imageReference.putFile(selectedImageUri!!)

            uploadTask.continueWithTask { imageReference.downloadUrl }
                .addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val downloadUri = p0.result
                    val message = Message(
                        imageUrl = downloadUri.toString(),
                        senderId = auth.currentUser?.uid,
                        recipientId = recipientUserId
                    )

                    messagesDatabaseReference.push().setValue(message)
                }
            }
        }
    }
}