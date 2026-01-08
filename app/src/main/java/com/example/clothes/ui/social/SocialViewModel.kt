package com.example.clothes.ui.social

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes.model.Cloth
import com.example.clothes.model.Post
import com.example.clothes.model.data.DataBaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SocialViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>?>()
    val posts: LiveData<List<Post>?> get() = _posts

    private val _usernames = MutableLiveData<List<String>?>()
    val usernames: LiveData<List<String>?> get() = _usernames

    fun fetchPosts(userKey: String) {

        viewModelScope.launch(Dispatchers.Default) {
            DataBaseHelper.getFollowsPosts(userKey){ dbPosts ->
                _posts.postValue(dbPosts)
            }
        }
    }

    fun fetchUsernames(userKey: String) {

        viewModelScope.launch(Dispatchers.Default) {
            DataBaseHelper.getUsernames { usernames ->
                DataBaseHelper.getProfile(userKey) { ownProfile ->
                    val filteredUsernames = usernames.filter { it != ownProfile?.username }
                    _usernames.postValue(filteredUsernames)
                }
            }
        }
    }
}