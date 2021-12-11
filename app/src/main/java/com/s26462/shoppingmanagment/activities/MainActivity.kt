package com.s26462.shoppingmanagment.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.adapters.ShoppingListItemAdapter
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import com.s26462.shoppingmanagment.models.ShoppingList
import com.s26462.shoppingmanagment.models.User
import com.s26462.shoppingmanagment.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    private lateinit var mUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()

        nav_view.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)

        fab_create_spnglist.setOnClickListener {
            val intent = Intent(this, CreateShoppingListActivity::class.java)
            intent.putExtra(Constants.NAME, mUsername)
            startActivity(intent)
        }

    }
//  wyświetlenie listy zakupów na UI
    fun populateShoppingListToUI(shoppingList: ArrayList<ShoppingList>) {
    hideProgressDialog()
    Toast.makeText(this, "shopping list size = ${shoppingList.size}", Toast.LENGTH_SHORT).show()
    if (shoppingList.size > 0) {
        rv_spnglists_list.visibility = View.VISIBLE
        tv_no_spnglists_available.visibility = View.GONE
Toast.makeText(this, "populate shopping list", Toast.LENGTH_SHORT).show()
        rv_spnglists_list.layoutManager = LinearLayoutManager(this)
        rv_spnglists_list.setHasFixedSize(true)

        val adapter = ShoppingListItemAdapter(this, shoppingList)
        rv_spnglists_list.adapter = adapter
    } else {
        rv_spnglists_list.visibility = View.GONE
        tv_no_spnglists_available.visibility = View.VISIBLE
    }
}
//    Utworzenie paska
    private fun setupActionBar(){
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }
//wysunięcie paska menu
    private fun toggleDrawer() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }
//obsługa przycisku wstecz, pojedyncze kliknięcie zwija menu (jeżeli rozwinięte), a podwójne wychodzi z aplikacji
    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }
//wczytanie ustawień użytkownika
    fun updateNavigationUserDetails(user: User, readShoppingList: Boolean){
        mUsername = user.name
//        https://github.com/bumptech/glide
//        ustawienie awataru
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_image);
//        ustawienie podpisu pod awatarem
        tv_username.text = user.name

        if(readShoppingList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getShoppingList(this)
        }
    }
//aktualizacja awataru i podpisu
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        } else {
            Log.e("profileUpdateSuccess", "Not successed!")
        }
    }

//Obsługa przycisków menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}