package com.example.jeksonchat.presentation.ui.chat.userlist

import com.example.jeksonchat.business.domain.models.User

interface UserClickListener {

    fun moveToChatWithUser(user: User)
}