package com.example.jeksonchat.presentation.ui.chat.chatwithuser

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeksonchat.business.domain.models.Message
import com.example.jeksonchat.business.domain.singletons.UserCompanionSingleton
import com.example.jeksonchat.databinding.ChatMessageUserCompanionItemBinding
import com.example.jeksonchat.databinding.ChatMessageUserItemBinding

class ChatWithUserMessagesAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessagesComparator()) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).messageUserId == UserCompanionSingleton.userCompanion?.userId) {
            COMPANION
        } else USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("TAG", "onCreateViewHolder: ")
        return when (viewType) {
            USER -> UserMessagesViewHolder.from(parent)
            else -> CompanionMessagesViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is UserMessagesViewHolder -> {
                holder.bind(getItem(position))
            }
            is CompanionMessagesViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    companion object {
        const val USER = 0
        const val COMPANION = 1
    }

}

class UserMessagesViewHolder(
    private val binding: ChatMessageUserItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message) {
        binding.message = message
    }

    companion object {
        fun from(parent: ViewGroup): UserMessagesViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ChatMessageUserItemBinding.inflate(inflater, parent, false)
            return UserMessagesViewHolder(binding)
        }
    }
}

class CompanionMessagesViewHolder(
    private val binding: ChatMessageUserCompanionItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message) {
        binding.message = message
    }

    companion object {
        fun from(parent: ViewGroup): CompanionMessagesViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ChatMessageUserCompanionItemBinding.inflate(inflater, parent, false)
            return CompanionMessagesViewHolder(binding)
        }
    }
}

class MessagesComparator : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.messageId == newItem.messageId
    }

}