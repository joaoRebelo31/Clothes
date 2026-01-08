package com.example.clothes.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.clothes.R
import com.example.clothes.ui.closet.ClosetActivity
import com.example.clothes.ui.profile.ProfileActivity
import com.example.clothes.ui.social.SocialActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BottomNavActivity : AppCompatActivity() {
    private lateinit var navigationView: BottomNavigationView
    lateinit var binding : ViewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this.binding = DataBindingUtil.setContentView(this, contentViewId)

        prepareNavigation()
    }

    private fun prepareNavigation(){

        navigationView = findViewById(R.id.bottom_nav)
        navigationView.itemIconTintList = null
        navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_closet -> {
                    startActivity(Intent(this, ClosetActivity::class.java))
                    true
                }
                R.id.navigation_social -> {
                    startActivity(Intent(this, SocialActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateNavigationBarState()
    }

    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0,0)
    }

    private fun updateNavigationBarState() {
        val actionId = navigationMenuItemId
        selectBottomNavigationBarItem(actionId)
    }

    private fun selectBottomNavigationBarItem(itemId: Int) {
        val item = navigationView.menu.findItem(itemId)
        item.setChecked(true)
    }

    abstract val contentViewId: Int
    abstract val navigationMenuItemId: Int
}