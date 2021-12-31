package com.s26462.shoppingmanagment.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.s26462.shoppingmanagment.R
import kotlinx.android.synthetic.main.activity_add_shop.*
import kotlinx.android.synthetic.main.activity_shop_list.*
import kotlinx.android.synthetic.main.activity_shop_list.toolbar_shops_activity

class AddShopActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shop)
        setupActionBar()

        iv_shop_image.setOnClickListener(this)
    }

//  wszystkie akcje kliknięcia
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.iv_shop_image -> {
                val pictureDilog = AlertDialog.Builder(this)
                pictureDilog.setTitle("Wybierz źródło")
                val pictureDialogItems = arrayOf("Otwórz galerię", "Otwórz aparat")
                pictureDilog.setItems(pictureDialogItems){
                    dialog, which ->
                    when(which){
                        0 -> Toast.makeText(this@AddShopActivity,"galeria",Toast.LENGTH_SHORT).show()
                        1 -> Toast.makeText(this@AddShopActivity,"aparat",Toast.LENGTH_SHORT).show()
                    }
                }
                pictureDilog.show()
            }
        }
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