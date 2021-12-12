package com.s26462.shoppingmanagment.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.adapters.ItemListAdapter
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import com.s26462.shoppingmanagment.models.Item
import com.s26462.shoppingmanagment.models.ShoppingList
import com.s26462.shoppingmanagment.utils.Constants
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.text.FieldPosition

class ItemListActivity : BaseActivity() {

    private lateinit var mItem: ShoppingList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        var spngListDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            spngListDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getShoppingListItems(this, spngListDocumentId)
    }
//Ustawienia actionBar
    private fun setupActionBar() {
        setSupportActionBar(toolbar_item_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mItem.name
        }
        toolbar_item_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
//  pobranie kategorii zakupów z FirestoreClass
    fun itemList(shoppinList: ShoppingList){
        mItem = shoppinList

        hideProgressDialog()
        setupActionBar()

        val addItemList = Item(resources.getString(R.string.add_list))
        shoppinList.itemList.add(addItemList)

        rv_item_list.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)
        rv_item_list.setHasFixedSize(true)

        val adapter = ItemListAdapter(this, shoppinList.itemList)
        rv_item_list.adapter = adapter
    }
//
    fun addUpdateItemSuccess(){
//TODO do rozważenia, jeżeli progresDialog będzie za długi, żeby go tu zamknąć i otworzyć
        FirestoreClass().getShoppingListItems(this, mItem.documentId)
    }
//utworzenie kategorii na gui
    fun createItemList(itemlistName: String){
        val item = Item(itemlistName, FirestoreClass().getCurrentUserId())

        mItem.itemList.add(0,item)
        mItem.itemList.removeAt(mItem.itemList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateItemList(this, mItem)
    }

    fun updateItemList(position: Int, listName: String, model: Item){
        val item = Item(listName, model.createdBy)

        mItem.itemList[position] = item
        mItem.itemList.removeAt(mItem.itemList.size -1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateItemList(this, mItem)
    }

    fun deleteItemList(position: Int){
        mItem.itemList.removeAt(position)
        mItem.itemList.removeAt(mItem.itemList.size -1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateItemList(this, mItem)
    }
}