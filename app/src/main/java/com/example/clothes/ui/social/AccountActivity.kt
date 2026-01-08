package com.example.clothes.ui.social

import SocialAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.clothes.R
import com.example.clothes.databinding.ActivityAccountBinding
import com.example.clothes.databinding.ActivitySocialBinding
import com.example.clothes.domain.AccountAdapter
import com.example.clothes.domain.ClosetAdapter
import com.example.clothes.model.Post
import com.example.clothes.model.Profile
import com.example.clothes.ui.BottomNavActivity

class AccountActivity : BottomNavActivity(), AccountAdapter.OnItemClickListener {

    private val viewModel: AccountViewModel by viewModels()
    private lateinit var accountBinding: ActivityAccountBinding
    private lateinit var profileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        accountBinding = binding as ActivityAccountBinding

        profileName = intent.getSerializableExtra("profileName") as String

        prepareInfo()
        prepareIcons()

    }

    fun prepareInfo(){

        viewModel.fetchProfile(profileName)
        viewModel.profile.observe(this) {
            accountBinding.postsRecycler.layoutManager = GridLayoutManager(this, 3)
            accountBinding.postsRecycler.adapter = it?.let {it1 -> AccountAdapter(profile = it, postsList = it.posts!!, context = this, itemClickedListener = this) }
            Glide.with(this).load(it.image).into(accountBinding.profileImage)
            accountBinding.profileName.text = it.username
        }
    }

    fun prepareIcons(){

        accountBinding.iconBack.setOnClickListener{finish()}
    }

    override fun invoke(position: Int, profile: Profile) {

        val intent = Intent(this, AccountPostsActivity::class.java)
        intent.putExtra("profile", profile)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    override val contentViewId: Int get() = R.layout.activity_account
    override val navigationMenuItemId: Int get() = R.id.navigation_social
}