package com.s26462.shoppingmanagment.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import com.s26462.shoppingmanagment.models.ShoppingList
import com.s26462.shoppingmanagment.models.User
import com.s26462.shoppingmanagment.utils.Constants
import kotlinx.android.synthetic.main.activity_create_shopping_list.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class CreateShoppingListActivity : BaseActivity() {

    private lateinit var mUserDetails: User
    private lateinit var mUserName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shopping_list)
        setupActionBar()
        FirestoreClass().loadUserData(this)

        if(intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        btn_create.setOnClickListener {
            createShoppingList()
        }

    }

    private fun createShoppingList(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        var shoppingList = ShoppingList(
            et_spnglist_name.text.toString(),
            mUserDetails.image,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createShoppingList(this,shoppingList)
    }

    fun spngListCreatedSuccessfully(){
        hideProgressDialog()
        finish()
    }

    //Ustawienia actionBar
    private fun setupActionBar(){
        setSupportActionBar(toolbar_create_spnglist_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.spnglist_name)
        }

        toolbar_create_spnglist_activity.setNavigationOnClickListener { onBackPressed() }
    }

//  załadowanie obrazka użytkownika do jego listy
    fun loadUserImage(user: User) {
        mUserDetails = user
//        https://github.com/bumptech/glide
//        ustawienie awataru
        Glide
            .with(this@CreateShoppingListActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_spnglist_place_holder)
            .into(iv_spnglist_image);

    }
}