package com.example.clothes.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes.model.Cloth
import com.example.clothes.model.Post
import com.example.clothes.model.Profile
import com.example.clothes.model.data.DataBaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _profile = MutableLiveData<Profile>()
    val profile: LiveData<Profile> get() = _profile

    fun fetchProfile(userKey: String) {

        viewModelScope.launch(Dispatchers.Default) {
            DataBaseHelper.getProfile(userKey){profile ->
                _profile.postValue(profile!!)
            }
        }
    }
}