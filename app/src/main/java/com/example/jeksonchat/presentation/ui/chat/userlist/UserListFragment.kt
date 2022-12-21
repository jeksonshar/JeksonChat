package com.example.jeksonchat.presentation.ui.chat.userlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.jeksonchat.business.domain.models.User
import com.example.jeksonchat.databinding.FragmentUserListBinding

class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    val binding: FragmentUserListBinding
        get() = _binding!!

    private val viewModel: UserListViewModel by viewModels()

    private val adapter by lazy {
        UserListAdapter(
            clickListener = object : UserClickListener {
                override fun moveToChatWithUser(user: User) {
                    viewModel.setUserToUserCompanion(user)
                    findNavController().navigate(UserListFragmentDirections.actionUserListFragmentToChatWithUserFragment(user.userName))
                }
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.getChatUserList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRecyclerUserList.adapter = adapter
        viewModel.chatUserList.observe(viewLifecycleOwner) {
            Log.d("TAG", "getChatUserList: user list: $it")
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}