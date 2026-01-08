package com.example.clothes.ui.closet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.example.clothes.databinding.ActivityCombinationdetailBinding
import com.example.clothes.domain.PickPieceAdapter
import com.example.clothes.model.Combination
import com.example.clothes.model.data.DataBaseHelper
import com.example.clothes.model.data.StateManager
import com.example.clothes.ui.BottomNavActivity
import com.example.clothes.ui.social.NewPostActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class CombinationDetailActivity() : BottomNavActivity() {

    private val viewModel: CombinationDetailViewModel by viewModels()
    private lateinit var combinationDetailBinding: ActivityCombinationdetailBinding
    private var selectedImage: String? = null
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try {
            if (it != null) {
                selectedImage = it.toString()
                Glide.with(this).load(it).transform(CenterCrop(), RoundedCorners(30)).into(combinationDetailBinding.combinationImage)
            }
        }catch(e:Exception){ e.printStackTrace() }
    }
    private var selectedPiece1: Cloth? = null
    private var selectedPiece2: Cloth? = null
    private lateinit var pieces: List<Cloth>
    private lateinit var userKey: String
    private var cloth: Cloth? = null
    private lateinit var combination: Combination

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        combinationDetailBinding = binding as ActivityCombinationdetailBinding

        userKey = StateManager.getInstance(this).getUserKey()

        if(intent.hasExtra("cloth")) { cloth = intent.getSerializableExtra("cloth") as Cloth }

        prepareInputs()
        if(cloth != null) {prepareInfo()}
        prepareButtons()
        prepareIcons()
    }

    private fun prepareButtons(){

        combinationDetailBinding.editCombinationIcon.setOnClickListener{galleryLauncher.launch("image/*")}
        combinationDetailBinding.editPiece1Icon.setOnClickListener{ pickPiece(1) }
        combinationDetailBinding.editPiece2Icon.setOnClickListener{ pickPiece(2) }
        combinationDetailBinding.deleteButton.setOnClickListener{deleteCombination()}
        combinationDetailBinding.confirmButton.setOnClickListener{saveCombination()}
    }

    private fun prepareInputs(){

        viewModel.fetchPieces(userKey)
        viewModel.pieces.observe(this) {
            pieces = it as List<Cloth>
        }
    }

    private fun prepareIcons(){

        combinationDetailBinding.iconBack.setOnClickListener{finish()}

        if(cloth != null){
            combinationDetailBinding.iconShare.visibility = View.VISIBLE
        }
    }

    private fun prepareInfo() {

        viewModel.fetchCombinationDetail(userKey, cloth!!)

        viewModel.combinationDetail.observe(this) {

            combinationDetailBinding.iconShare.setOnClickListener{it1 -> postCombination(it!!)}

            selectedPiece1 = it?.piece1
            selectedPiece2 = it?.piece2

            Glide.with(this).load(it!!.image).transform(CenterCrop(), RoundedCorners(40)).into(combinationDetailBinding.combinationImage)
            if(selectedPiece1?.id != null){
                Glide.with(this).load(selectedPiece1?.image).transform(CenterCrop(), RoundedCorners(40)).into(combinationDetailBinding.piece1Image)
            }
            else {
                Glide.with(this).load(R.drawable.image_icon).transform(CenterCrop(), RoundedCorners(40)).into(combinationDetailBinding.piece1Image)
            }
            if(selectedPiece2?.id != null){
                Glide.with(this).load(selectedPiece2?.image).transform(CenterCrop(), RoundedCorners(40)).into(combinationDetailBinding.piece2Image)
            }
            else{
                Glide.with(this).load(R.drawable.image_icon).transform(CenterCrop(), RoundedCorners(40)).into(combinationDetailBinding.piece2Image)
            }

            combinationDetailBinding.piece1Image.setOnClickListener{ redirectToPiece(selectedPiece1!!) }
            combinationDetailBinding.piece2Image.setOnClickListener{ redirectToPiece(selectedPiece2!!) }
        }
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
                    Glide.with(this).load(selectedCloth.image).transform(CenterCrop(), RoundedCorners(40)).into(combinationDetailBinding.piece1Image)
                }
                if(position == 2){
                    selectedPiece2 = selectedCloth
                    Glide.with(this).load(selectedCloth.image).transform(CenterCrop(), RoundedCorners(40)).into(combinationDetailBinding.piece2Image)
                }
                combinationDetailBinding.piece1Image.setOnClickListener{ redirectToPiece(selectedPiece1!!) }
                combinationDetailBinding.piece2Image.setOnClickListener{ redirectToPiece(selectedPiece2!!) }

                dialog.dismiss()
            }
            imageRecyclerView.adapter = adapter

            dialog.show()
    }

    private fun redirectToPiece(piece: Cloth) {

        val intent = Intent(applicationContext, PieceDetailActivity::class.java)
        intent.putExtra("cloth", piece)
        startActivity(intent)
    }

    private fun postCombination(combination: Combination) {

        val intent = Intent(applicationContext, NewPostActivity::class.java)
        intent.putExtra("combination", combination)
        startActivity(intent)
    }

    private fun saveCombination() {

        var list = "Wishlist"
        if(selectedPiece1?.list == "Owned" && selectedPiece2?.list == "Owned"){
            list = "Owned"
        }

        if(cloth == null) {
            if(selectedPiece1 != null && selectedPiece2 != null){
                if(selectedImage != null){
                    DataBaseHelper.setCombination(
                        userKey,
                        selectedPiece1?.id!!,
                        selectedPiece2?.id!!,
                        list,
                        "Combination",
                        selectedImage!!
                    ).thenAccept { success ->
                        if (success) { finish() }
                    }
                }
            }
        }
        else{
            if(selectedPiece1?.id != null && selectedPiece2?.id != null) {
                cloth!!.id?.let {
                    DataBaseHelper.updateCombination(
                        userKey,
                        selectedPiece1?.id!!,
                        selectedPiece2?.id!!,
                        list,
                        "Combination",
                        selectedImage,
                        it,
                    ).thenAccept { success ->
                        if (success) {
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun deleteCombination() {
        if(cloth == null){
            finish()
        }
        else{
            cloth!!.id?.let {
                DataBaseHelper.deleteCombination(
                    userKey,
                    it,
                ).thenAccept { success ->
                    if (success) { finish() }
                }
            }
        }
    }

    override val contentViewId: Int get() = R.layout.activity_combinationdetail
    override val navigationMenuItemId: Int get() = R.id.navigation_closet
}