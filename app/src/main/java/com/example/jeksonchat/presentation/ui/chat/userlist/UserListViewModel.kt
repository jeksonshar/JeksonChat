package com.example.jeksonchat.presentation.ui.chat.userlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeksonchat.business.domain.models.User
import com.example.jeksonchat.business.domain.singletons.UserCompanionSingleton
import com.example.jeksonchat.utils.USER_LIST_PATH
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserListViewModel : ViewModel() {

    private var _chatUserList = MutableLiveData<List<User>>()
    val chatUserList: LiveData<List<User>> = _chatUserList

    private val userListRef = Firebase.database.getReference(USER_LIST_PATH)

    fun getChatUserList() {
        val userList = mutableListOf<User>()
        val currentUserId = Firebase.auth.currentUser?.uid
        userListRef.get().addOnSuccessListener {
            it.children.forEach { firebaseUser ->
                firebaseUser.getValue(User::class.java)?.let { user ->
                    if (currentUserId != user.userId) {
                        userList.add(user)
                    }
                }
            }
            _chatUserList.value = userList
        }
    }

    fun setUserToUserCompanion(user: User) {
        UserCompanionSingleton.userCompanion = user
    }
}