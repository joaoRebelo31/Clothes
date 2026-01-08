package com.example.clothes.ui.closet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes.model.Cloth
import com.example.clothes.model.data.DataBaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClosetViewModel : ViewModel() {

    private val _clothes = MutableLiveData<List<Cloth?>>()
    val clothes: LiveData<List<Cloth?>> get() = _clothes

    fun fetchClothes(userKey: String, category: String, list: String) {

        viewModelScope.launch(Dispatchers.Default) {

            DataBaseHelper.getClothes(userKey) { clothes ->
                val filteredClothes = ArrayList(clothes)
                if(category != "All"){ filteredClothes.removeAll{it?.category != category} }
                if(list != "All"){ filteredClothes.removeAll{it?.list != list} }
                _clothes.postValue(filteredClothes.reversed())
            }
        }
    }
}