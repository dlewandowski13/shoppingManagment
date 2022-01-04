package com.s26462.shoppingmanagment.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.adapters.ShoppingListItemAdapter
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import com.s26462.shoppingmanagment.models.Shop
import com.s26462.shoppingmanagment.models.ShoppingList
import com.s26462.shoppingmanagment.utils.Constants
import com.shoppingmanagment.adapters.ShopListAdapter
import kotlinx.android.synthetic.main.activity_shop_list.*

class ShopListActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    // TODO do przerobienia, żeby w tym miejscu wybierać listę sklepów, a tworzyć z głównego menu
    private lateinit var mShopingList: ShoppingList
    private lateinit var mShopList: ArrayList<Shop>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_list)

//      w zależności od miejsca, z którego wywoływany jest ten intent
//      albo pobieramy do listy zakupów, albo do użytkownika
        if(intent.hasExtra(Constants.SHOPPINGLIST_DETAIL)){
            mShopingList = intent.getParcelableExtra(Constants.SHOPPINGLIST_DETAIL)!!
//
            FirestoreClass().getShoppingListItems(this,mShopingList.documentId)
        } else {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getShopList(this)
        }
        setupActionBar()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_shop, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_shop -> {
//                TODO jakoś ładniej zrobić to odświeżanie niż przez finish()
//                Toast.makeText(this@ShopListActivity, "mShop: $mShop",Toast.LENGTH_LONG).show()
                val intentAddShop = Intent(this, AddShopActivity::class.java)
                if(intent.hasExtra(Constants.SHOPPINGLIST_DETAIL)) {
                    intentAddShop.putExtra(Constants.SHOPPINGLIST_DETAIL, mShopingList)
                }
                startActivity(intentAddShop)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //
//Ustawienia actionBar
    private fun setupActionBar(){
        setSupportActionBar(toolbar_shops_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.shops)
        }

        toolbar_shops_activity.setNavigationOnClickListener { onBackPressed() }
    }


    //  wyświetlenie listy sklepów
    fun setupShopList(list: ArrayList<Shop>){
        mShopList = list
        hideProgressDialog()

        rv_shops_list.layoutManager = LinearLayoutManager(this)
        rv_shops_list.setHasFixedSize(true)

        val adapter = ShopListAdapter(this, list)
        rv_shops_list.adapter = adapter

        adapter.setOnClickListener(object: ShopListAdapter.OnClickListener {
            override fun onClick(position: Int, model: Shop) {
                val intent = Intent(this@ShopListActivity, MapActivity::class.java)
                intent.putExtra(Constants.SHOP_DETAIL, model)
                startActivity(intent)
            }
        })
    }

    fun getShopList(shoppingList: ShoppingList){
        mShopingList = shoppingList

        if (mShopingList.shopList.isNotEmpty()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedShopsListDetails(this, mShopingList.shopList)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.action_add_shop -> {
////                TODO jakoś ładniej zrobić to odświeżanie niż przez finish()
////                Toast.makeText(this@ShopListActivity, "mShop: $mShop",Toast.LENGTH_LONG).show()
//                val intentAddShop = Intent(this, AddShopActivity::class.java)
//                intentAddShop.putExtra(Constants.SHOPPINGLIST_DETAIL, mShopingList)
//                startActivity(intentAddShop)
//                finish()
//                return true
//            }
//        }
        return super.onOptionsItemSelected(item)
    }
}