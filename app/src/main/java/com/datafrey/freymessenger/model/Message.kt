package com.datafrey.freymessenger.model

data class Message(
    var id: String? = null,
    var text: String? = null,
    var imageUrl: String? = null,
    var senderId: String? = null,
    var recipientId: String? = null,
    var isMine: Boolean? = null
)