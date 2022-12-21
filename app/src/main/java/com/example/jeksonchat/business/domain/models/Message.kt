package com.example.jeksonchat.business.domain.models

data class Message(
    val messageUserId: String? = "",
    val messageCompanionUserId: String? = "",
    val messageText: String? = "",
    val messageTime: String? = "time",
    val messageId: String? = "id",
)