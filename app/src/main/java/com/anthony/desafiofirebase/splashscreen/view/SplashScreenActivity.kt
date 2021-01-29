package com.anthony.desafiofirebase.splashscreen.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.anthony.desafiofirebase.MainActivity
import com.anthony.desafiofirebase.R
import com.anthony.desafiofirebase.authentication.view.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref =
                getSharedPreferences(getString(R.string.preference_remember), MODE_PRIVATE)
            val isRememberChecked = sharedPref.getBoolean(getString(R.string.remember), false)
            val user = auth.currentUser

            val myIntent = if (user != null && isRememberChecked) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, AuthenticationActivity::class.java)
            }

            startActivity(myIntent)
            finish()
        }, 3000)
    }
}