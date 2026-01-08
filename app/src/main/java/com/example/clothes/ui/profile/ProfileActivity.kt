package com.example.clothes.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.clothes.R
import com.example.clothes.databinding.ActivityAccountBinding
import com.example.clothes.databinding.ActivityProfileBinding
import com.example.clothes.domain.AccountAdapter
import com.example.clothes.domain.ProfileAdapter
import com.example.clothes.model.Profile
import com.example.clothes.model.data.DataBaseHelper
import com.example.clothes.model.data.StateManager
import com.example.clothes.ui.BottomNavActivity
import com.example.clothes.ui.closet.ClosetActivity
import com.example.clothes.ui.social.AccountPostsActivity
import com.example.clothes.ui.social.AccountViewModel

class ProfileActivity : BottomNavActivity(), ProfileAdapter.OnItemClickListener {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var profileBinding: ActivityProfileBinding
    private lateinit var userKey: String
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try{ if(it != null) {
            DataBaseHelper.updateProfileImage(userKey, it.toString())
            Glide.with(this).load(it).into(profileBinding.profileImage)
        }
        }catch(e:Exception){ e.printStackTrace() }
    }

    override fun onRestart() {
        super.onRestart()
        if(StateManager.getInstance(this).ensureLogin()){
            userKey = StateManager.getInstance(this).getUserKey()
            prepareInfo()
            prepareButtons()
            prepareIcons()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        profileBinding = binding as ActivityProfileBinding

        if(StateManager.getInstance(this).ensureLogin()){
            userKey = StateManager.getInstance(this).getUserKey()
            prepareIcons()
            prepareButtons()
            prepareInfo()
        }
    }

    fun prepareInfo(){

        viewModel.fetchProfile(userKey)
        viewModel.profile.observe(this) {
            profileBinding.postsRecycler.layoutManager = GridLayoutManager(this, 3)
            profileBinding.postsRecycler.adapter = it?.let {it1 -> ProfileAdapter(profile = it, postsList = it.posts!!, context = this, itemClickedListener = this) }
            Glide.with(this).load(it.image).into(profileBinding.profileImage)
            profileBinding.profileName.text = it.username
        }
    }

    fun prepareButtons(){

        profileBinding.editImage.setOnClickListener{editPicture()}
    }

    fun prepareIcons(){

        profileBinding.iconLogout.setOnClickListener{logout()}
    }

    fun editPicture(){
        galleryLauncher.launch("image/*")
    }

    fun logout(){
        StateManager.getInstance(this).setLogin(false)
        StateManager.getInstance(this).setUserKey("") //DEBUG
        startActivity(Intent(this, ClosetActivity::class.java))
    }

    override fun invoke(position: Int, profile: Profile) {

        val intent = Intent(this, ProfilePostsActivity::class.java)
        intent.putExtra("profile", profile)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    override val contentViewId: Int get() = R.layout.activity_profile
    override val navigationMenuItemId: Int get() = R.id.navigation_profile
}