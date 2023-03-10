package com.example.jeksonchat.presentation.ui.chat.chatwithuser

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeksonchat.business.domain.models.Message
import com.example.jeksonchat.business.domain.singletons.UserCompanionSingleton
import com.example.jeksonchat.datasource.NotificationsApiService
import com.example.jeksonchat.datasource.network.entities.Data
import com.example.jeksonchat.datasource.network.entities.FirebaseNotification
import com.example.jeksonchat.utils.MESSAGE_LIST_PATH
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatWithUserViewModel @Inject constructor(
    private val apiService: NotificationsApiService
) : ViewModel() {

    private var _messageText = MutableLiveData<CharSequence?>()
    val messageText: LiveData<CharSequence?> = _messageText

    private var _messageList = MutableLiveData<List<Message>>()
    val messageList: LiveData<List<Message>> = _messageList


    private val userCompanion = UserCompanionSingleton.userCompanion
    private val user = Firebase.auth.currentUser

    private val messageListRef = Firebase.database.getReference(MESSAGE_LIST_PATH)
    private lateinit var messageListener: ChildEventListener

    fun setMessageListener() {
        val mesList = mutableListOf<Message>()

        messageListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Message>().let { message ->
                    Log.d(
                        "TAG",
                        "onChildAdded: messageUserId = ${message?.messageUserId}; userId = ${user?.uid}; messageCompanionId = ${message?.messageCompanionUserId}; companionId = ${userCompanion?.userId}"
                    )
                    if ((message?.messageUserId == user?.uid && message?.messageCompanionUserId == userCompanion?.userId) ||
                        (message?.messageUserId == userCompanion?.userId && message?.messageCompanionUserId == user?.uid)
                    ) {
                        if (message != null) {
                            mesList.add(message)
                        }
                        _messageList.value = mesList
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }

        messageListRef.addChildEventListener(messageListener)
    }

    fun clearMessageListener() {
        messageListRef.removeEventListener(messageListener)
    }

    fun setMessageText(value: CharSequence?) {
        _messageText.value = value
    }

    fun sendMessage() {
        if (!messageText.value.isNullOrEmpty()) {
            val messageTime = messageCreationTime()
            val newMessageId = newMessageId()
            messageListRef.push().setValue(Message(user?.uid, userCompanion?.userId, messageText.value.toString(), messageTime, newMessageId))
                .addOnSuccessListener {
                    sendNotification()
                    _messageText.value = null
                }.addOnFailureListener {
                    // TODO ???????????????? ????????????????????????, ?????? ?????????????????? ???? ????????????????????
                }
        }
    }

    private fun sendNotification() {
        viewModelScope.launch {
            val notificationEntity = FirebaseNotification(
                Data(
                    recipientId = userCompanion?.userId,
                    senderId = user?.uid,
                    title = user?.displayName,
                    body = messageText.value.toString()
                ), registrationIds = userCompanion?.notificationTokens
            )
            apiService.sendNotifications(notificationEntity)
        }
    }

    private fun messageCreationTime(): String {
        val dateTime = LocalDateTime.now()
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
        val mesTime = formatter.print(dateTime)
        return mesTime.toString()
    }

    private fun newMessageId(): String {
        return UUID.randomUUID().toString()
    }

    override fun onCleared() {
        UserCompanionSingleton.clear()
        super.onCleared()
    }

}