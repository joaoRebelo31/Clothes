package com.example.clothes.ui.closet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes.model.Cloth
import com.example.clothes.model.Combination
import com.example.clothes.model.Piece
import com.example.clothes.model.data.DataBaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CombinationDetailViewModel : ViewModel() {

    private val _combinationDetail = MutableLiveData<Combination?>()
    val combinationDetail: MutableLiveData<Combination?> get() = _combinationDetail

    private val _pieces = MutableLiveData<List<Cloth?>>()
    val pieces: LiveData<List<Cloth?>> get() = _pieces

    fun fetchCombinationDetail(userKey:String, cloth: Cloth) {
        viewModelScope.launch(Dispatchers.Default) {
            cloth.id?.let {
                DataBaseHelper.getCombination(userKey, it) { combination ->
                    _combinationDetail.postValue(combination)
                }
            }
        }
    }

    fun fetchPieces(userKey:String){
        viewModelScope.launch(Dispatchers.Default) {
            DataBaseHelper.getClothes(userKey) { clothes ->
                val pieces = ArrayList(clothes)
                pieces.removeAll{it?.category != "Piece"}
                _pieces.postValue(pieces.reversed())
            }
        }
    }
}