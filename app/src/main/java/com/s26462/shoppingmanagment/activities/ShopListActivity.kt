package com.s26462.shoppingmanagment.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import com.s26462.shoppingmanagment.models.Shop
import com.s26462.shoppingmanagment.models.ShoppingList
import com.s26462.shoppingmanagment.models.User
import com.s26462.shoppingmanagment.utils.Constants
import com.shoppingmanagment.adapters.MemberListItemsAdapter
import com.shoppingmanagment.adapters.ShopListAdapter
import kotlinx.android.synthetic.main.activity_create_shopping_list.*
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_members.toolbar_members_activity
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_shop_list.*
import kotlinx.android.synthetic.main.dialog_add_shop.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class ShopListActivity : BaseActivity() {
// TODO do przerobienia, żeby w tym miejscu wybierać listę sklepów, a tworzyć z głównego menu
//    private lateinit var mShop: ShoppingList
//    private lateinit var mShopList: ArrayList<Shop>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_list)
        setupActionBar()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_shop, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_shop -> {
                Toast.makeText(this,"add shop", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AddShopActivity::class.java)
                startActivity(intent)
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
//
//    //  ustawienie listy użytkowników
//    fun shopList(list: ArrayList<Shop>){
//        mShopList = list
//        hideProgressDialog()
//
//        rv_shops_list.layoutManager = LinearLayoutManager(this)
//        rv_shops_list.setHasFixedSize(true)
//
//        val adapter = ShopListAdapter(this, list)
//        rv_shops_list.adapter = adapter
//    }
//
////  wywołanie po udanym dodaniu sklepu, ustawienie go do zmiennej i odświeżenie widoku
////    fun shopAddSuccess(shop: Shop){
//////    TODO getShopList w FirestoreClass()
////        FirestoreClass().getShopList(this, mShopList.Id)
////    }
//
////  dodanie sklepu do listy zakupów i do bazy danych
////    fun shopDetails(shop: Shop){
////        mShopList.shopList.add(shop.id)
////        FirestoreClass().createShop(this,shop)
////    }
//
//    //  obsługa dialog
//    private fun dialogSearchShop(){
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.dialog_add_shop)
//        dialog.tv_add_shop.setOnClickListener {
//            val name = dialog.et_name_shop.text.toString()
//            val description = dialog.et_description_shop.text.toString()
//            val radius = dialog.et_radius_shop.text.toString()
//
//            var shopList = Shop(
//                name,
//                description,
//                radius
//            )
//            if(name.isNotEmpty() && description.isNotEmpty() && radius.isNotEmpty()){
//                dialog.dismiss()
//                showProgressDialog(resources.getString(R.string.please_wait))
//                FirestoreClass().createShop(this,shopList)
//                Toast.makeText(this,"Dodano sklep do bazy danych",Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this,"Dane nie mogą być puste.",Toast.LENGTH_SHORT).show()
//            }
//        }
//        dialog.tv_cancel_shop.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//    //  wywołanie po udanym dodaniu sklepu, ustawienie go do zmiennej i odświeżenie widoku
//    fun shopAddSuccess(shop: Shop){
//        hideProgressDialog()
//        mShopList.add(shop)
//        shopList(mShopList)
//    }

}