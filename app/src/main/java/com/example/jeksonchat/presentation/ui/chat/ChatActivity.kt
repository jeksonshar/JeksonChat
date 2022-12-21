package com.example.jeksonchat.presentation.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.jeksonchat.R
import com.example.jeksonchat.databinding.ActivityChatBinding
import com.example.jeksonchat.presentation.ui.auth.AuthActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private var _binding: ActivityChatBinding? = null
    private val binding: ActivityChatBinding
        get() = _binding!!

    private lateinit var navController: NavController

    lateinit var toolbar: Toolbar

    private val navigationListener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        when (destination.id) {
            R.id.userListFragment -> {}
            R.id.chatWithUserFragment -> {
                binding.toolbar.menu.clear()
            }
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

        binding.toolbar.setNavigationOnClickListener {
            navController.navigateUp()
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