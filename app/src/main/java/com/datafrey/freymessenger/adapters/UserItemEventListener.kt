package com.datafrey.freymessenger.adapters

import com.datafrey.freymessenger.model.User

interface UserItemEventListener {
    fun onClick(clickedItemUserInfo: User)
}