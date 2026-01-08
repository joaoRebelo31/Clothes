package com.example.clothes.ui.closet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.clothes.R
import com.example.clothes.databinding.ActivityPiecedetailBinding
import com.example.clothes.model.Cloth
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.clothes.model.data.DataBaseHelper
import com.example.clothes.model.data.StateManager
import com.example.clothes.ui.BottomNavActivity

class PieceDetailActivity: BottomNavActivity() {

    private val viewModel: PieceDetailViewModel by viewModels()
    private lateinit var pieceDetailBinding: ActivityPiecedetailBinding
    private var selectedImage: String? = null
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try{ if(it != null) {
            selectedImage = it.toString()
            Glide.with(this).load(it).transform(CenterCrop(), RoundedCorners(30)).into(pieceDetailBinding.pieceImage)
        } }catch(e:Exception){ e.printStackTrace() } }
    var cloth: Cloth? = null
    private lateinit var userKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        pieceDetailBinding = binding as ActivityPiecedetailBinding
        userKey = StateManager.getInstance(this).getUserKey()

        if(intent.hasExtra("cloth")) { cloth = intent.getSerializableExtra("cloth") as Cloth }

        prepareInputs()
        prepareButtons()
        prepareIcons()
        if(cloth != null) {prepareInfo()}
    }

    private fun prepareInputs(){

        ArrayAdapter.createFromResource(this, R.array.list_items, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                pieceDetailBinding.listInput.adapter = adapter
            }
    }

    private fun prepareIcons(){

        pieceDetailBinding.iconBack.setOnClickListener{finish()}
    }

    private fun prepareButtons(){

        pieceDetailBinding.editPieceIcon.setOnClickListener{galleryLauncher.launch("image/*")}
        pieceDetailBinding.deleteButton.setOnClickListener{deletePiece()}
        pieceDetailBinding.confirmButton.setOnClickListener{savePiece()}
    }

    private fun prepareInfo() {

        viewModel.fetchPieceDetail(userKey, cloth!!)

        viewModel.pieceDetail.observe(this) {

            it!!.image?.let { it1 -> Log.d("Piece aaa", it1) }

            Glide.with(this).load(it!!.image).transform(CenterCrop(), RoundedCorners(40)).into(pieceDetailBinding.pieceImage)
            pieceDetailBinding.brandInput.setText(it.brand)
            pieceDetailBinding.typeInput.setText(it.type)
            pieceDetailBinding.listInput.setSelection(resources.getStringArray(R.array.list_items).indexOf(it.list))
            pieceDetailBinding.colorInput.setText(it.color)
            pieceDetailBinding.sizeInput.setText(it.size)
        }
    }

    private fun savePiece() {

        if(cloth == null) {
            if(selectedImage != null){
                DataBaseHelper.setPiece(
                    userKey,
                    pieceDetailBinding.typeInput.text.toString(),
                    pieceDetailBinding.brandInput.text.toString(),
                    pieceDetailBinding.colorInput.text.toString(),
                    pieceDetailBinding.sizeInput.text.toString(),
                    pieceDetailBinding.listInput.selectedItem.toString(),
                    selectedImage,
                    "Piece"
                ).thenAccept { success ->
                    if (success) { finish() }
                }
            }
        }
        else{

            cloth!!.id?.let {
                DataBaseHelper.updatePiece(
                    userKey,
                    pieceDetailBinding.typeInput.text.toString(),
                    pieceDetailBinding.brandInput.text.toString(),
                    pieceDetailBinding.colorInput.text.toString(),
                    pieceDetailBinding.sizeInput.text.toString(),
                    pieceDetailBinding.listInput.selectedItem.toString(),
                    selectedImage,
                    "Piece",
                    it,
                ).thenAccept { success ->
                    if (success) { finish() }
                }
            }
        }
    }

    private fun deletePiece() {
        if(cloth == null){
            finish()
        }
        else{
            cloth!!.id?.let {
                DataBaseHelper.deletePiece(
                    userKey,
                    it,
                ).thenAccept { success ->
                    if (success) { finish() }
                }
            }
        }
    }

    override val contentViewId: Int get() = R.layout.activity_piecedetail
    override val navigationMenuItemId: Int get() = R.id.navigation_closet
}