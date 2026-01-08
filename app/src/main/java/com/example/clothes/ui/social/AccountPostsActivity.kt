package com.example.clothes.ui.social

import AccountPostsAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.clothes.R
import com.example.clothes.databinding.ActivityAccountpostsBinding
import com.example.clothes.model.Post
import com.example.clothes.model.Profile
import com.example.clothes.model.data.DataBaseHelper
import com.example.clothes.model.data.StateManager
import com.example.clothes.ui.BottomNavActivity
import com.example.clothes.ui.closet.ClosetActivity

class AccountPostsActivity : BottomNavActivity(), AccountPostsAdapter.OnItemClickListener {

    private val viewModel: AccountPostsViewModel by viewModels()
    private lateinit var accountPostsBinding: ActivityAccountpostsBinding
    private lateinit var userKey: String
    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        accountPostsBinding = binding as ActivityAccountpostsBinding

        userKey = StateManager.getInstance(this).getUserKey()

        profile = intent.getSerializableExtra("profile") as Profile

        prepareInfo()
        prepareIcons()

    }

    fun prepareIcons(){

        accountPostsBinding.iconBack.setOnClickListener{finish()}
    }

    fun prepareInfo(){

        viewModel.fetchPosts(profile)
        viewModel.posts.observe(this) {
            accountPostsBinding.postsRecycler.adapter = it?.let {it1 -> AccountPostsAdapter(postsList = it1, context = this, itemClickedListener = this) }
            accountPostsBinding.postsRecycler.scrollToPosition(intent.getSerializableExtra("position") as Int)
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

    override val contentViewId: Int get() = R.layout.activity_accountposts
    override val navigationMenuItemId: Int get() = R.id.navigation_social
}