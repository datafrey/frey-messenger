package com.datafrey.freymessenger.presenters

import android.net.Uri
import android.widget.ListView
import com.datafrey.freymessenger.activities.ChatActivity
import com.datafrey.freymessenger.adapters.MessageAdapter
import com.datafrey.freymessenger.model.DbNodeNames
import com.datafrey.freymessenger.model.Message
import com.datafrey.freymessenger.model.StorageNodeNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ChatPresenter(private var view: ChatActivity?) {

    private var database = FirebaseDatabase.getInstance()
    private var messagesDatabaseReference = database.reference
        .child(DbNodeNames.MESSAGES_DB_NODE_NAME)

    private var storage = FirebaseStorage.getInstance()
    private var chatImagesStorageReference = storage.reference
        .child(StorageNodeNames.CHAT_IMAGES_STORAGE_NODE_NAME)

    private var messageListView: ListView? = null
    private var messageListViewAdapter: MessageAdapter? = null

    private var senderUserId = FirebaseAuth.getInstance()
        .currentUser?.uid.toString()
    private lateinit var recipientUserId: String

    fun detachView() {
        view = null
        messageListView = null
        messageListViewAdapter = null
    }

    fun setMessageListView(messageListView: ListView) {
        this.messageListView = messageListView
    }

    fun setMessageListViewAdapter(messageListViewAdapter: MessageAdapter) {
        this.messageListViewAdapter = messageListViewAdapter
    }

    fun setRecipientUserId(recipientUserId: String) {
        this.recipientUserId = recipientUserId
    }

    fun sendText(text: String) {
        val message = Message(
            text = text,
            senderId = senderUserId,
            recipientId = recipientUserId
        )

        messagesDatabaseReference.push().setValue(message)
    }

    fun sendImage(selectedImageUri: Uri?) {
        val imageReference = chatImagesStorageReference
            .child(selectedImageUri?.lastPathSegment.toString())

        val uploadTask = imageReference.putFile(selectedImageUri!!)

        uploadTask.continueWithTask { imageReference.downloadUrl }
            .addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val downloadUri = p0.result
                    val message = Message(
                        imageUrl = downloadUri.toString(),
                        senderId = senderUserId,
                        recipientId = recipientUserId
                    )

                    messagesDatabaseReference.push().setValue(message)
                }
            }
    }

    fun attachMessagesDatabaseReferenceChildEventListener() {
        messagesDatabaseReference.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                with(message!!) {
                    if (senderId == senderUserId && recipientId == recipientUserId) {
                        isMine = true
                        messageListViewAdapter!!.add(this)
                    } else if (recipientId == senderUserId && senderId == recipientUserId) {
                        isMine = false
                        messageListViewAdapter!!.add(this)
                    }
                }

                messageListView!!.post { messageListView!!.setSelection(messageListView!!.count) }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }

}