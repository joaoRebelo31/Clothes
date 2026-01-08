package com.example.clothes.model.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.clothes.ui.signin.SignInActivity

class StateManager private constructor(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    private val userKeyName: String = "userKey"
    private val loginName: String = "login"

    fun getUserKey(): String {

        var userKey = sharedPreferences.getString(userKeyName, "")
        if(userKey == "" || userKey == null){
            userKey = DataBaseHelper.getNewUserKey()
            val editor = sharedPreferences.edit()
            editor.putString(userKeyName, userKey)
            editor.apply()
        }
        return userKey
    }

    fun setUserKey(userKey: String) {

        val editor = sharedPreferences.edit()
        editor.putString(userKeyName, userKey)
        editor.apply()
    }

    fun ensureLogin(): Boolean {

        val login = sharedPreferences.getBoolean(loginName, false)
        if(!login){
            context.startActivity(Intent(context, SignInActivity::class.java))
        }
        Log.d("Login", login.toString() + "a")
        return login
    }

    fun setLogin(state: Boolean){

        val editor = sharedPreferences.edit()
        editor.putBoolean(loginName, state)
        editor.apply()
    }


    companion object {
        @Volatile
        private var INSTANCE: StateManager? = null

        fun getInstance(context: Context): StateManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StateManager(context).also { INSTANCE = it }
            }
        }
    }
}