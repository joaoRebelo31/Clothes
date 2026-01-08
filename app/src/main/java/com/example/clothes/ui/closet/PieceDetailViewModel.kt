package com.example.clothes.ui.closet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes.model.Piece
import com.example.clothes.model.Cloth
import com.example.clothes.model.data.DataBaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PieceDetailViewModel : ViewModel() {

    private val _pieceDetail = MutableLiveData<Piece?>()
    val pieceDetail: MutableLiveData<Piece?>
        get() = _pieceDetail

    fun fetchPieceDetail(userKey: String, cloth: Cloth) {
        viewModelScope.launch(Dispatchers.Default) {
            cloth.id?.let { DataBaseHelper.getPiece(userKey, it){ piece ->
                    _pieceDetail.postValue(piece)
                }
            }
        }
    }
}