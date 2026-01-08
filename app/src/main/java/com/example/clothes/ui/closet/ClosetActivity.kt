package com.example.clothes.ui.closet

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.clothes.R
import com.example.clothes.databinding.ActivityClosetBinding
import com.example.clothes.domain.ClosetAdapter
import com.example.clothes.model.Cloth
import com.example.clothes.ui.BottomNavActivity
import com.example.clothes.model.data.StateManager

class ClosetActivity : BottomNavActivity(), ClosetAdapter.OnItemClickListener {

    private val viewModel: ClosetViewModel by viewModels()
    private lateinit var closetBinding: ActivityClosetBinding
    private lateinit var userKey: String

    override fun onRestart() {
        super.onRestart()
        prepareInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        closetBinding = binding as ActivityClosetBinding
        userKey = StateManager.getInstance(this).getUserKey()

        prepareIcons()
        prepareInputs()
        prepareButtons()
        prepareInfo()

    }

    private fun prepareButtons(){

        closetBinding.addPieceButton.setOnClickListener{
            startActivity(Intent(this, PieceDetailActivity::class.java))
        }

        closetBinding.addCombinationPiece.setOnClickListener{
            startActivity(Intent(this, CombinationDetailActivity::class.java))
        }
    }

    private fun prepareIcons(){

        closetBinding.iconAdd.setOnClickListener{

            ObjectAnimator.ofFloat(closetBinding.iconAdd, "rotation", closetBinding.iconAdd.rotation, closetBinding.iconAdd.rotation + 45).apply {
                duration = 500 // duration of the animation in milliseconds
                start()
            }

            if(closetBinding.addFrame.visibility == View.GONE) {
                closetBinding.addFrame.visibility = View.VISIBLE
                closetBinding.addFrame.translationY = 0f
                ObjectAnimator.ofFloat(closetBinding.addFrame, "translationY", 0f, 250f).apply {
                    duration = 500
                    start()
                }
            }
            else{
                closetBinding.addFrame.translationY = 0f
                ObjectAnimator.ofFloat(closetBinding.addFrame, "translationY", 250f, 0f).apply {
                    duration = 500
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            closetBinding.addFrame.visibility = View.GONE
                        }
                    })
                    start()
                }
            }
        }

        closetBinding.iconFilter.setOnClickListener{

            ObjectAnimator.ofFloat(closetBinding.iconFilter, "rotation", closetBinding.iconFilter.rotation, closetBinding.iconFilter.rotation + 180).apply {
                duration = 500 // duration of the animation in milliseconds
                start()
            }

            if(closetBinding.filterFrame.visibility == View.GONE) {
                closetBinding.filterFrame.visibility = View.VISIBLE
                closetBinding.filterFrame.translationY = 0f
                ObjectAnimator.ofFloat(closetBinding.filterFrame, "translationY", 0f, 250f).apply {
                    duration = 500
                    start()
                }
            }
            else{
                closetBinding.filterFrame.translationY = 0f
                ObjectAnimator.ofFloat(closetBinding.filterFrame, "translationY", 250f, 0f).apply {
                    duration = 500
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            closetBinding.filterFrame.visibility = View.GONE
                        }
                    })
                    start()
                }
            }
        }
    }

    private fun prepareInputs(){

        ArrayAdapter.createFromResource(this, R.array.list_filter_items, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                closetBinding.listInput.adapter = adapter
            }

        ArrayAdapter.createFromResource(this, R.array.category_filter_items, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                closetBinding.categoryInput.adapter = adapter
            }

        closetBinding.categoryInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) { prepareInfo() }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        closetBinding.listInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) { prepareInfo() }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun prepareInfo(){

        closetBinding.clothesRecycler.layoutManager = GridLayoutManager(this, 3)

        val category = closetBinding.categoryInput.selectedItem.toString()
        val list = closetBinding.listInput.selectedItem.toString()

        viewModel.fetchClothes(userKey, category, list)
        viewModel.clothes.observe(this) {
            closetBinding.clothesRecycler.adapter = it?.let {it1 -> ClosetAdapter(clothesList = it1, context = this, itemClickedListener = this) }
        }
    }

    override fun invoke(cloth: Cloth) {

        val intent: Intent
        if(cloth.category == "Piece") {intent = Intent(this, PieceDetailActivity::class.java)}
        else {intent = Intent(this, CombinationDetailActivity::class.java)}

        intent.putExtra("cloth", cloth)
        startActivity(intent)
    }

    override val contentViewId: Int get() = R.layout.activity_closet
    override val navigationMenuItemId: Int get() = R.id.navigation_closet
}