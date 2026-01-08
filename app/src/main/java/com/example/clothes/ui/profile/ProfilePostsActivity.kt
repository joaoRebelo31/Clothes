package com.example.clothes.ui.profile

import AccountPostsAdapter
import ProfilePostsAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.clothes.R
import com.example.clothes.databinding.ActivityProfilepostsBinding
import com.example.clothes.model.Post
import com.example.clothes.model.Profile
import com.example.clothes.model.data.DataBaseHelper
import com.example.clothes.model.data.StateManager
import com.example.clothes.ui.BottomNavActivity
import com.example.clothes.ui.social.AccountActivity

class ProfilePostsActivity : BottomNavActivity(), ProfilePostsAdapter.OnItemClickListener {

    private val viewModel: ProfilePostsViewModel by viewModels()
    private lateinit var profilePostsBinding: ActivityProfilepostsBinding
    private lateinit var profile: Profile
    private lateinit var userKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        profilePostsBinding = binding as ActivityProfilepostsBinding

        userKey = StateManager.getInstance(this).getUserKey()

        profile = intent.getSerializableExtra("profile") as Profile

        prepareInfo()
        prepareIcons()
    }

    fun prepareInfo(){

        viewModel.fetchPosts(profile)
        viewModel.posts.observe(this) {
            profilePostsBinding.postsRecycler.adapter = it?.let {it1 -> ProfilePostsAdapter(postsList = it1, context = this, itemClickedListener = this) }
            profilePostsBinding.postsRecycler.scrollToPosition(intent.getSerializableExtra("position") as Int)
        }
    }

    fun prepareIcons(){

        profilePostsBinding.iconBack.setOnClickListener{finish()}
    }

    override fun invokeAccount(profileName: String) {

        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("profileName", profileName)
        startActivity(intent)
    }

    override fun deletePost(postId: String) {
        DataBaseHelper.deletePost(userKey, postId).thenAccept {success ->
            if(success){
                finish()
            }

        }
    }

    override val contentViewId: Int get() = R.layout.activity_profileposts
    override val navigationMenuItemId: Int get() = R.id.navigation_profile
}