package com.example.jeksonchat.business.domain.singletons

import com.example.jeksonchat.business.domain.models.User

object UserCompanionSingleton {
    var userCompanion: User? = null

    fun clear() {
        userCompanion = null
    }
}