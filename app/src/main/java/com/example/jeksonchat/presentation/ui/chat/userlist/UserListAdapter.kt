package com.example.jeksonchat.presentation.ui.chat.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeksonchat.business.domain.models.User
import com.example.jeksonchat.databinding.UserListItemBinding

class UserListAdapter(private val clickListener: UserClickListener) : ListAdapter<User, UserListViewHolder>(UsersComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        return UserListViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class UserListViewHolder(
    private val binding: UserListItemBinding,
    clickListener: UserClickListener
) : RecyclerView.ViewHolder(binding.root) {

    private var localUser: User? = null

    init {
        binding.root.setOnClickListener {
            localUser?.let { user -> clickListener.moveToChatWithUser(user) }
        }
    }

    fun bind(user: User) {
        localUser = user
        binding.user = user
    }

    companion object {
        fun from(parent: ViewGroup, clickListener: UserClickListener): UserListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = UserListItemBinding.inflate(inflater, parent, false)
            return UserListViewHolder(binding, clickListener)
        }
    }

}

class UsersComparator : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.userId == newItem.userId
    }

}