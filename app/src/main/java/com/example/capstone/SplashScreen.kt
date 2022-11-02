package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if(supportActionBar!=null){
            this.supportActionBar!!.hide()
        }

        val iv_logo = findViewById<ImageView>(R.id.iv_logo)
        iv_logo.alpha = 0F
        iv_logo. animate().setDuration(3000).alpha(1f).withEndAction{
            val i = Intent(this, SignInActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }
}