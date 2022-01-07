package com.s26462.shoppingmanagment.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.s26462.shoppingmanagment.MapsFragment
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.adapters.ShoppingListItemAdapter
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import com.s26462.shoppingmanagment.models.Shop
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
    private val mPermissionMessage = "Brak wymaganych uprawnień do dostępu do lokalizacji. " +
            "Możesz dodać je później w ustawieniach aplikacji, albo przejść do nich teraz."

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
    if (shoppingList.size > 0) {
        rv_spnglists_list.visibility = View.VISIBLE
        no_spnglists_are_available.visibility = View.GONE
        rv_spnglists_list.layoutManager = LinearLayoutManager(this)
        rv_spnglists_list.setHasFixedSize(true)

        val adapter = ShoppingListItemAdapter(this, shoppingList)
        rv_spnglists_list.adapter = adapter

        adapter.setOnClickListener(object: ShoppingListItemAdapter.OnClickListener{
            override fun onClick(position: Int, model: ShoppingList) {
                val intent = Intent(this@MainActivity, ItemListActivity::class.java)
                intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                startActivity(intent)
            }
        })

    } else {
        rv_spnglists_list.visibility = View.GONE
        no_spnglists_are_available.visibility = View.VISIBLE
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

            R.id.nav_favourite_shops -> {
                val intent = Intent(this, ShopListActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_maps -> {
                isLocationPermissionGranted()
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

    fun loadShopListToMap(shopsLists: ArrayList<Shop>) {
        val intent = Intent(this, MapActivity::class.java)
        Log.e(this.javaClass.simpleName, "shopList: $shopsLists")
        intent.putExtra(Constants.SHOPS, shopsLists)
        startActivity(intent)
    }

    private fun isLocationPermissionGranted() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if(report!!.areAllPermissionsGranted()){
                    FirestoreClass().getShopList(this@MainActivity)
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>,
                                                            token: PermissionToken
            ) {
                showRationalDialogForPermissions(mPermissionMessage)
            }
        }).onSameThread().check()
    }
}