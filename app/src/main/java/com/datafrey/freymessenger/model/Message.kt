package com.datafrey.freymessenger.model

data class Message(
    var text: String? = null,
    var imageUrl: String? = null,
    var senderId: String? = null,
    var recipientId: String? = null,
    var isMine: Boolean? = null
)