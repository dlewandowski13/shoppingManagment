package com.s26462.shoppingmanagment.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.s26462.shoppingmanagment.R
import kotlinx.android.synthetic.main.activity_add_shop.*
import kotlinx.android.synthetic.main.activity_shop_list.*
import kotlinx.android.synthetic.main.activity_shop_list.toolbar_shops_activity

class AddShopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shop)
        setupActionBar()
    }


//Ustawienia actionBar
    private fun setupActionBar(){
        setSupportActionBar(toolbar_add_shop)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.add_Shop)
        }

    toolbar_add_shop.setNavigationOnClickListener { onBackPressed() }
    }
}