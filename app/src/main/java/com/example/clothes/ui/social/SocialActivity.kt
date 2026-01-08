package com.example.clothes.ui.social

import SocialAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.clothes.R
import com.example.clothes.databinding.ActivitySocialBinding
import com.example.clothes.model.Post
import com.example.clothes.model.data.DataBaseHelper
import com.example.clothes.ui.BottomNavActivity
import com.example.clothes.model.data.StateManager
import com.example.clothes.ui.closet.ClosetActivity

class SocialActivity : BottomNavActivity(), SocialAdapter.OnItemClickListener {

    private val viewModel: SocialViewModel by viewModels()
    private lateinit var socialBinding: ActivitySocialBinding
    private lateinit var userKey: String

    override fun onRestart() {
        super.onRestart()
        if(StateManager.getInstance(this).ensureLogin()){
            userKey = StateManager.getInstance(this).getUserKey()
            prepareInfo()
            prepareInputs()
            prepareIcons()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        socialBinding = binding as ActivitySocialBinding

        if(StateManager.getInstance(this).ensureLogin()) {
            userKey = StateManager.getInstance(this).getUserKey()
            prepareInfo()
            prepareInputs()
            prepareIcons()
        }

    }

    fun prepareIcons(){

        socialBinding.iconPost.setOnClickListener{
            startActivity(Intent(this, NewPostActivity::class.java))
        }

        socialBinding.search.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                ObjectAnimator.ofFloat(socialBinding.searchBtn, "rotation", 0f, 360f).apply {
                    duration = 500 // duration of the animation in milliseconds
                    start()
                }
            }
        }

        socialBinding.search.setOnItemClickListener { parent, view, position, id ->
            invokeAccount(parent.getItemAtPosition(position).toString())
        }
    }

    fun prepareInputs(){

        viewModel.fetchUsernames(userKey)
        viewModel.usernames.observe(this) {
            socialBinding.search.setAdapter(it?.let {it1 -> ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, it) })
        }
    }

    fun prepareInfo(){

        viewModel.fetchPosts(userKey)
        viewModel.posts.observe(this) {
            socialBinding.postsRecycler.adapter = it?.let {it1 -> SocialAdapter(postsList = it1, context = this, itemClickedListener = this) }
        }
    }

    override fun invokeAccount(profileName: String) {

        val intent = Intent(this, AccountActivity::class.java)
        intent.putExtra("profileName", profileName)
        startActivity(intent)
    }

    override fun savePost(post: Post) {
        DataBaseHelper.savePieces(userKey, post.piece1!!, post.piece2!!).thenAccept { success ->
            if(success){
                startActivity(Intent(this, ClosetActivity::class.java))
            }
        }
    }

    override val contentViewId: Int get() = R.layout.activity_social
    override val navigationMenuItemId: Int get() = R.id.navigation_social
}