package com.example.jeksonchat.business.domain.models

data class User(
    val userId: String? = "",
    val email: String? = "",
    val userName: String? = "",
    val userImage: String? = "",
    val notificationTokens: List<String?>? = emptyList(),
)
