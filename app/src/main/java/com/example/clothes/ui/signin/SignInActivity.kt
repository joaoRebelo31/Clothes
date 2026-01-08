package com.example.clothes.ui.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.clothes.R
import com.example.clothes.databinding.ActivitySigninBinding
import com.example.clothes.model.data.DataBaseHelper
import com.example.clothes.model.data.StateManager
import com.example.clothes.ui.closet.ClosetActivity

class SignInActivity: AppCompatActivity() {

    lateinit var binding : ActivitySigninBinding

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, ClosetActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin) as ActivitySigninBinding

        prepareIcons()
        prepareButtons()
    }

    private fun prepareIcons(){

        binding.backIcon.setOnClickListener{
            startActivity(Intent(this, ClosetActivity::class.java))
        }
    }

    private fun prepareButtons(){

        binding.loginButton.setOnClickListener{
            binding.passwordError.visibility = View.INVISIBLE
            binding.usernameError.visibility = View.INVISIBLE

            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()

            DataBaseHelper.checkLogin(username, password){ login ->
                if(login == true){
                    DataBaseHelper.getUserKey(username){ userKey ->
                        StateManager.getInstance(this).setUserKey(userKey!!)
                        StateManager.getInstance(this).setLogin(true)
                        finish()
                    }
                }
                else{
                    binding.passwordError.visibility = View.VISIBLE
                }
            }
        }

        binding.createButton.setOnClickListener{
            binding.passwordError.visibility = View.INVISIBLE
            binding.usernameError.visibility = View.INVISIBLE

            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val userKey = StateManager.getInstance(this).getUserKey()

            DataBaseHelper.createAccount(username, password, userKey).thenAccept { success ->
                if(success) {
                    StateManager.getInstance(this).setLogin(true)
                    finish()
                }
                else {
                    binding.usernameError.visibility = View.VISIBLE
                }
            }
        }
    }
}