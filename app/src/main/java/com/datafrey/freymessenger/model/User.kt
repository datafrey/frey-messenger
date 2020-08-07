package com.datafrey.freymessenger.model

data class User(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var bio: String = "",
    var profilePictureUrl: String = ""
)
