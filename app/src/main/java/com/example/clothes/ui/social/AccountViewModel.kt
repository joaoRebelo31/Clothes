package com.example.clothes.ui.social

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

class AccountViewModel : ViewModel() {

    private val _profile = MutableLiveData<Profile>()
    val profile: LiveData<Profile> get() = _profile

    fun fetchProfile(profileName: String) {

        viewModelScope.launch(Dispatchers.Default) {
            DataBaseHelper.getProfileByUsername(profileName){profile ->
                _profile.postValue(profile!!)
            }
        }
    }
}