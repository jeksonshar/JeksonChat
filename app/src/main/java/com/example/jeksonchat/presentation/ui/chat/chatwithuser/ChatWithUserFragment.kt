package com.example.jeksonchat.presentation.ui.chat.chatwithuser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.jeksonchat.databinding.FragmentChatWithUserBinding
import com.example.jeksonchat.presentation.ui.chat.ChatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatWithUserFragment : Fragment() {

    private var _binding: FragmentChatWithUserBinding? = null
    val binding: FragmentChatWithUserBinding
        get() = _binding!!

    private val viewModel: ChatWithUserViewModel by viewModels()

    private val adapter by lazy {
        ChatWithUserMessagesAdapter()
    }

    private lateinit var toolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatWithUserBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.rvChatMessages.adapter = adapter

        toolbar = (requireActivity() as ChatActivity).toolbar

        (requireActivity() as ChatActivity).binding.ivVideoCall.setOnClickListener {
            findNavController().navigate(ChatWithUserFragmentDirections.actionChatWithUserFragmentToVideoCallingFragment())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.messageText.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.etMessage.text?.clear()
            }
        }

        viewModel.messageList.observe(viewLifecycleOwner) {
            Log.d("TAG", "onViewCreated: message list count: ${it.size}")
            adapter.submitList(it)
//            TODO ?????? ??????????????, ?????????????????????? ???????????? ???? ?????????????????? ?????? ???????? (notifyDataSetChanged())
            adapter.notifyDataSetChanged()
            binding.rvChatMessages.scrollToPosition(it.size-1)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.setMessageListener()
    }

    override fun onStop() {
        viewModel.clearMessageListener()
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}