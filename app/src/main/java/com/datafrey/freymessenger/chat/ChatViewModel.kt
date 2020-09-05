package com.datafrey.freymessenger.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.datafrey.freymessenger.model.DatabaseNodeNames
import com.datafrey.freymessenger.model.Message
import com.datafrey.freymessenger.model.StorageNodeNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ChatViewModel : ViewModel() {

    private val messagesDatabaseReference = FirebaseDatabase.getInstance()
        .reference.child(DatabaseNodeNames.MESSAGES_DB_NODE_NAME)

    private val chatImagesStorageReference by lazy {
        FirebaseStorage.getInstance()
            .reference.child(StorageNodeNames.CHAT_IMAGES_STORAGE_NODE_NAME)
    }

    private var messages = mutableListOf<Message>()
    private var messageAdapter = MessageAdapter()

    private var senderUserId = FirebaseAuth.getInstance()
        .currentUser?.uid.toString()
    private lateinit var recipientUserId: String

    fun setRecipientUserId(recipientUserId: String) {
        this.recipientUserId = recipientUserId
    }

    fun getMessageListSize() = messages.size

    fun getMessageAdapter() = messageAdapter

    init {
        messageAdapter.submitList(messages)
        attachMessagesDatabaseChildEventListener()
    }

    private fun attachMessagesDatabaseChildEventListener() {
        messagesDatabaseReference.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)!!

                if (message.senderId == senderUserId && message.recipientId == recipientUserId) {
                    message.isMine = true
                    messages.add(message)
                } else if (message.recipientId == senderUserId && message.senderId == recipientUserId) {
                    message.isMine = false
                    messages.add(message)
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }

    fun sendText(text: String) {
        val messageDatabaseReference = messagesDatabaseReference.push()
        val message = Message(
            id = messageDatabaseReference.key.toString(),
            text = text,
            senderId = senderUserId,
            recipientId = recipientUserId
        )

        messageDatabaseReference.setValue(message)
    }

    fun sendImage(selectedImageUri: Uri?) {
        val imageStorageReference = chatImagesStorageReference
            .child(selectedImageUri?.lastPathSegment.toString())

        val uploadTask = imageStorageReference.putFile(selectedImageUri!!)

        uploadTask.continueWithTask { imageStorageReference.downloadUrl }
            .addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val messageDatabaseReference = messagesDatabaseReference.push()
                    val imageDownloadUrl = p0.result.toString()
                    val message = Message(
                        id = messageDatabaseReference.key.toString(),
                        imageUrl = imageDownloadUrl,
                        senderId = senderUserId,
                        recipientId = recipientUserId
                    )

                    messageDatabaseReference.setValue(message)
                }
            }
    }

    class ChatViewModelFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}