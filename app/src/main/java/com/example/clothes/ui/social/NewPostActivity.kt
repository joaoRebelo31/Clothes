package com.example.clothes.ui.social

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.clothes.R
import com.example.clothes.model.Cloth
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.clothes.databinding.ActivityNewpostBinding
import com.example.clothes.domain.PickPieceAdapter
import com.example.clothes.model.Combination
import com.example.clothes.model.Piece
import com.example.clothes.model.data.DataBaseHelper
import com.example.clothes.model.data.StateManager
import com.example.clothes.ui.BottomNavActivity
import com.example.clothes.ui.profile.ProfileActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class NewPostActivity() : BottomNavActivity() {

    private val viewModel: NewPostViewModel by viewModels()
    private lateinit var newPostBinding: ActivityNewpostBinding
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try {
            if (it != null) {
                selectedImage = it.toString()
                Glide.with(this).load(it).transform(CenterCrop(), RoundedCorners(30)).into(newPostBinding.combinationImage)
            }
        }catch(e:Exception){ e.printStackTrace() }
    }
    private var selectedPiece1: Cloth? = null
    private var selectedPiece2: Cloth? = null
    private var selectedImage: String? = null
    private lateinit var pieces: List<Cloth>
    private lateinit var userKey: String
    private var combination: Combination? = null

    override fun onResume() {
        super.onResume()
        StateManager.getInstance(this).ensureLogin()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        newPostBinding = binding as ActivityNewpostBinding

        userKey = StateManager.getInstance(this).getUserKey()

        if(intent.hasExtra("combination")) {
            combination = intent.getSerializableExtra("combination") as Combination
            selectedImage = combination?.image
            selectedPiece1 = combination?.piece1
            selectedPiece2 = combination?.piece2

            prepareInfo()
        }

        if(StateManager.getInstance(this).ensureLogin()) {
            prepareInputs()
            prepareButtons()
            prepareIcons()
        }
    }

    private fun prepareIcons(){

        newPostBinding.iconBack.setOnClickListener{finish()}
    }

    private fun prepareButtons(){

        newPostBinding.editCombinationIcon.setOnClickListener{galleryLauncher.launch("image/*")}
        newPostBinding.editPiece1Icon.setOnClickListener{pickPiece(1)}
        newPostBinding.editPiece2Icon.setOnClickListener{pickPiece(2)}
        newPostBinding.deleteButton.setOnClickListener{finish()}
        newPostBinding.confirmButton.setOnClickListener{post()}
    }

    private fun prepareInputs(){

        viewModel.fetchPieces(userKey)
        viewModel.pieces.observe(this) {
            pieces = it as List<Cloth>
        }
    }

    private fun prepareInfo() {

        Glide.with(this).load(combination?.image).transform(CenterCrop(), RoundedCorners(40)).into(newPostBinding.combinationImage)
        Glide.with(this).load(combination?.piece1?.image).transform(CenterCrop(), RoundedCorners(40)).into(newPostBinding.piece1Image)
        Glide.with(this).load(combination?.piece2?.image).transform(CenterCrop(), RoundedCorners(40)).into(newPostBinding.piece2Image)
    }

    private fun pickPiece(position: Int) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_piece_picker, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        val imageRecyclerView = dialogView.findViewById<RecyclerView>(R.id.clothes_recycler)
        imageRecyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = PickPieceAdapter(this, pieces) { selectedCloth ->
            if(position == 1){
                selectedPiece1 = selectedCloth
                Glide.with(this).load(selectedCloth.image).transform(CenterCrop(), RoundedCorners(40)).into(newPostBinding.piece1Image)
            }
            if(position == 2){
                selectedPiece2 = selectedCloth
                Glide.with(this).load(selectedCloth.image).transform(CenterCrop(), RoundedCorners(40)).into(newPostBinding.piece2Image)
            }
            dialog.dismiss()
        }
        imageRecyclerView.adapter = adapter

        dialog.show()
    }

    private fun post() {
        if((selectedImage != null && selectedPiece1 != null && selectedPiece2 != null) || (combination != null)){
            DataBaseHelper.setPost(userKey, selectedImage!!, selectedPiece1?.id!!, selectedPiece2?.id!!).thenAccept {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }
    }

    override val contentViewId: Int get() = R.layout.activity_newpost
    override val navigationMenuItemId: Int get() = R.id.navigation_social
}