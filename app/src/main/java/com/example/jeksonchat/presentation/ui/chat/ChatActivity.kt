package com.example.jeksonchat.presentation.ui.chat

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.jeksonchat.R
import com.example.jeksonchat.business.domain.models.User
import com.example.jeksonchat.business.domain.singletons.UserCompanionSingleton
import com.example.jeksonchat.databinding.ActivityChatBinding
import com.example.jeksonchat.presentation.ui.auth.AuthActivity
import com.example.jeksonchat.utils.SENDER_ID
import com.example.jeksonchat.utils.USER_LIST_PATH
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private var _binding: ActivityChatBinding? = null
    val binding: ActivityChatBinding
        get() = _binding!!

    private lateinit var navController: NavController

    lateinit var toolbar: Toolbar

    private val navigationListener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        when (destination.id) {
            R.id.userListFragment -> {
                binding.ivVideoCall.visibility = View.GONE
            }
            R.id.chatWithUserFragment -> {
                binding.toolbar.menu.clear()
                binding.ivVideoCall.visibility = View.VISIBLE
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this, "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.lifecycleOwner = this

        toolbar = binding.toolbar

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)

        Firebase.messaging.isAutoInitEnabled = true

        binding.toolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        val data = intent.getStringExtra(SENDER_ID)
        Log.d("TAG", "app started from notification: senderId = $data")
        if (data != null) {

            val refDBUserList = Firebase.database.getReference(USER_LIST_PATH)

            refDBUserList.child(data).get().addOnSuccessListener {
                it.getValue(User::class.java)?.let { user ->
                    UserCompanionSingleton.userCompanion = user
                    val bundle = Bundle()
                    bundle.putString("userName", user.userName)
                    navController.navigate(R.id.chatWithUserFragment, bundle)
//                    navController.navigate(UserListFragmentDirections.actionUserListFragmentToChatWithUserFragment(user.userName))
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        navController.addOnDestinationChangedListener(navigationListener)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuSignOut) {
            Firebase.auth.signOut()
            startActivity(newIntent(this, AuthActivity()))
            finish()
        }
        return true
    }

//    override fun onStart() {
//        super.onStart()
//        navController.addOnDestinationChangedListener(navigationListener)
//    }

    override fun onStop() {
        navController.removeOnDestinationChangedListener(navigationListener)
        super.onStop()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        fun newIntent(context: Context, activity: AppCompatActivity): Intent {
            return Intent(context, activity::class.java)
        }
    }
}