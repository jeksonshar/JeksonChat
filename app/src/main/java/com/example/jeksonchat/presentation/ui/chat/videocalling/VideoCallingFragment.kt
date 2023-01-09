package com.example.jeksonchat.presentation.ui.chat.videocalling

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jeksonchat.R
import com.example.jeksonchat.databinding.FragmentChatVideoCallingBinding
import com.example.jeksonchat.presentation.ui.chat.ChatActivity
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas
import java.util.*

class VideoCallingFragment : Fragment() {

    private var _binding: FragmentChatVideoCallingBinding? = null
    val binding: FragmentChatVideoCallingBinding
        get() = _binding!!

    private lateinit var appId: String
    private val channelName = "testChannel1"
    private lateinit var token: String
    private val userId = UUID.randomUUID().hashCode()
    private var agoraEngine: RtcEngine? = null
    private var isJoined: Boolean = false
    private var remoteSurfaceView: SurfaceView? = null
    private var localSurfaceView: SurfaceView? = null

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            requireActivity().runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            isJoined = true
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            requireActivity().runOnUiThread { remoteSurfaceView?.visibility = View.GONE }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatVideoCallingBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        appId = getString(R.string.agora_app_id)
        token = getString(R.string.agora_app_token)

        (requireActivity() as ChatActivity).binding.ivVideoCall.visibility = View.GONE

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(requireActivity(), REQUESTED_PERMISSION, PERMISSION_REQ_ID)
        }

        setupVideoSDKEngine()

        binding.fabLeave.setOnClickListener {
            leaveChannel()
            findNavController().navigateUp()
        }

//        binding.fabSwitchCamera.setOnClickListener {
//            if (isJoined) {
//                agoraEngine?.switchCamera()
//            }
//        }
//
//        binding.fabMute.setOnClickListener {
//
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        joinChannel()
    }

    private fun setupVideoSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = requireActivity().baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.enableVideo()
        } catch (e: Exception) {
            showMessage(e.toString())
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(requireActivity().baseContext)
        remoteSurfaceView?.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContent.addView(remoteSurfaceView)
        agoraEngine?.setupRemoteVideo(VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        remoteSurfaceView?.visibility = View.VISIBLE
    }

    private fun showMessage(message: String) {
        requireActivity().runOnUiThread { Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show() }
    }

    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(requireActivity().baseContext, REQUESTED_PERMISSION[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireActivity().baseContext, REQUESTED_PERMISSION[1]) != PackageManager.PERMISSION_GRANTED)
    }

    private fun setupLocalVideo() {
        localSurfaceView = SurfaceView(requireActivity().baseContext)
        binding.localVideoViewContent.addView(localSurfaceView)
        agoraEngine?.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    private fun joinChannel() {
        if (checkSelfPermission()) {
            val options = ChannelMediaOptions()
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurfaceView?.visibility = View.VISIBLE
            agoraEngine?.startPreview()
            agoraEngine?.joinChannel(token, channelName, userId, options)
            Log.d("TAG", "joinChannel: audioTrackCount = ${agoraEngine?.audioTrackCount}")
        } else {
            showMessage("Permission was not granted")
        }
    }

    private fun leaveChannel() {
        if (!isJoined) {
            showMessage("Doesn't have joined channel")
        } else {
            agoraEngine?.stopPreview()
            agoraEngine?.leaveChannel()
            showMessage("you left the channel")
            if (remoteSurfaceView != null) remoteSurfaceView?.visibility = View.GONE
            if (localSurfaceView != null) localSurfaceView?.visibility = View.GONE
            isJoined = false
        }
    }

    override fun onDestroyView() {
        leaveChannel()
        RtcEngine.destroy()
        agoraEngine = null
        remoteSurfaceView = null
        localSurfaceView = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val PERMISSION_REQ_ID = 23
        private val REQUESTED_PERMISSION = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
    }
}