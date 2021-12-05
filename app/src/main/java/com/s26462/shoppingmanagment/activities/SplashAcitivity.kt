package com.s26462.shoppingmanagment.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_splash_acitivity.*

class SplashAcitivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_acitivity)
//fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

//ustawienie czcionki dla tytułu aplikacji
        val typeFace : Typeface = Typeface.createFromAsset(assets,"carbon bl.ttf")
        tv_app_name.typeface = typeFace


//wyświetlanie okna powitania i przekierowanie na właściwy Activity
        Handler(Looper.getMainLooper()).postDelayed({
            var currentUserID = FirestoreClass().getCurrentUserId()
            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this,MainActivity::class.java))
            }else {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 2500)
    }
}