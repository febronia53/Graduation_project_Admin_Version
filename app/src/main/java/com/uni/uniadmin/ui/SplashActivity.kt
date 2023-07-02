package com.uni.uniadmin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.uni.uniadmin.R
import com.uni.uniadmin.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySplashBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.splash_status_color)
        
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }, 2000)


    }



}