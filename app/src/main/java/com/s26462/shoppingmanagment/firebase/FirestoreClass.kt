package com.s26462.shoppingmanagment.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.s26462.shoppingmanagment.activities.*
import com.s26462.shoppingmanagment.models.Shop
import com.s26462.shoppingmanagment.models.ShoppingList
import com.s26462.shoppingmanagment.models.User
import com.s26462.shoppingmanagment.utils.Constants
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    //  utworzenie użytkownika w bazie
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            } .addOnFailureListener {
                    e->
                Log.e(activity.javaClass.simpleName,"Error")
            }
    }

    //Utworzenie pozycji listy zakupów
    fun getShoppingListItems(activity: Activity, documentId: String) {
        mFireStore.collection(Constants.SPNGLIST)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, "document: ${document.toString()}")
                val item = document.toObject(ShoppingList::class.java)!!
                item.documentId = document.id
                when(activity) {
                    is ItemListActivity -> {
                        activity.itemList(item)
                    }
                    is ShopListActivity -> {
                        activity.updateShopingList(item)
                    }
                }
            }
            .addOnFailureListener {
                    e ->
                when(activity) {
                    is ItemListActivity -> {
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while loading shopping list.", e)
                    }
                    is ShopListActivity -> {
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while loading shopping list.", e)
                    }
                }
            }
    }

    //utworzenie listy zakupów w bazie
    fun createShoppingList(activity: CreateShoppingListActivity, spngList: ShoppingList) {
        mFireStore.collection(Constants.SPNGLIST)
            .document()
            .set(spngList, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Shopping List created successfully.")
//                Toast.makeText(activity, "Stworzono nową listę zakupów", Toast.LENGTH_SHORT).show()
                activity.spngListCreatedSuccessfully()
            }
            .addOnFailureListener {
                    exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating ShoppingList.", exception)
            }
    }
    //  pobranie listy zakupów, w zależności od uprawnień użytkownika
    fun getShoppingList(activity: MainActivity){
        mFireStore.collection(Constants.SPNGLIST)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                    document ->
//                Log.i(activity.javaClass.simpleName, "documents: ${document.documents.toString()}")
                val shoppingLists: ArrayList<ShoppingList> = ArrayList()
                for(i in document.documents){
                    val shoppingList = i.toObject(ShoppingList::class.java)!!
                    shoppingList.documentId = i.id
                    shoppingLists.add(shoppingList)
                }
                activity.populateShoppingListToUI(shoppingLists)
            }
            .addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a ShoppingLists.", e)
            }
    }

    //  dodanie kategorii do bazy
    fun addUpdateItemList(activity: ItemListActivity, shoppingList: ShoppingList) {
        val itemListHashMap = HashMap<String, Any>()
        itemListHashMap[Constants.ITEM_LIST] = shoppingList.itemList

        mFireStore.collection(Constants.SPNGLIST)
            .document(shoppingList.documentId)
            .update(itemListHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Item updates successfully.")

                activity.addUpdateItemSuccess()
            }
            .addOnFailureListener {
                    exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating an Item.", exception)
            }
    }

    //metoda do dodania nowego sklepu do bazy danych, wykorzystuję HashMap
    fun createShop(activity: AddShopActivity, shopHashMap: HashMap<String, Any>) {
        Toast.makeText(activity, "uniqueID = ${getUniqueId()}",Toast.LENGTH_LONG).show()
        shopHashMap[Constants.SHOP_ID] = getUniqueId()
        mFireStore.collection(Constants.SHOPS)
            .document()
            .set(shopHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Shop created successfully!")
                Toast.makeText(activity,"Sklep został dodany!", Toast.LENGTH_LONG).show()
                val name = shopHashMap[Constants.SHOP_NAME].toString()
                activity.shopCreatedSuccess(name)
            }
            .addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating shop!",e)
                Toast.makeText(activity,"Nie udało się dodać sklepu!",Toast.LENGTH_LONG).show()
            }
    }

    //metoda do aktualizacji danych o użytkowniku, wykorzystuję HashMap
    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile Data update successfully!")
                Toast.makeText(activity,"Twoje dane zostały zaktualizowane!", Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error updating the profile!",e)
                Toast.makeText(activity,"Nie udało się zaktualizować Twoich danych.",Toast.LENGTH_LONG).show()
            }
    }

    fun loadUserData(activity: Activity, readShoppingList: Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!
                when(activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readShoppingList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }

                    is CreateShoppingListActivity -> {
                        activity.loadUserImage(loggedInUser)
                    }
                }

            } .addOnFailureListener {
                    e->
                when(activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignInUser","Nie udało się zalogować.",e)
            }

    }

    //pobranie aktualnego usera
    fun getCurrentUserId(): String {

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    //  generowanie unikalnego ID
    fun getUniqueId(): String{
        var uniqueID = UUID.randomUUID().toString()
        return uniqueID
    }

    //  pobranie z bazy wszystkich członków danej listy i przygotowanie listy userów
    fun getAssignedMembersListDetails(activity: MembersActivity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName,"Members List: ${document.documents.toString()}")

                val usersList : ArrayList<User> = ArrayList()

                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }
                activity.setupMemberList(usersList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting members.",e)
            }
    }

    //  pobranie z bazy wszystkich członków danej listy i przygotowanie listy userów
    fun getAssignedShopsListDetails(activity: ShopListActivity, shopList: ArrayList<String>){
        mFireStore.collection(Constants.SHOPS)
            .whereIn(Constants.ID, shopList)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName,"Members List: ${document.documents.toString()}")

                val shopsList : ArrayList<Shop> = ArrayList()

                for(i in document.documents){
                    val shop = i.toObject(Shop::class.java)!!
                    shopsList.add(shop)
                }
                activity.setupShopList(shopsList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting members.",e)
            }
    }

    //  pobranie sklepu
    fun getShopDetails(activity: AddShopActivity, name: String) {
        mFireStore.collection(Constants.SHOPS)
            .whereEqualTo(Constants.SHOP_NAME, name)
            .get()
            .addOnSuccessListener {
                    document ->
                if(document.documents.size > 0) {
                    val shop = document.documents[0].toObject(Shop::class.java)!!
                    Toast.makeText(activity,"shop: $shop",Toast.LENGTH_LONG).show()
                    activity.assigneeShop(shop)
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("Nie znaleziono takiego sklepu.")
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting user details.",e)
                activity.showErrorSnackBar("Wystąpił problem podczas pobierania danych sklepu, spróbuj jeszcze raz.")
            }

    }

    //  pobranie użytkownika
    fun getMemberDetails(activity: MembersActivity, email: String) {
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                    document ->
                if(document.documents.size > 0) {
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("Nie znaleziono takiego użytkownika.")
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting user details.",e)
                activity.showErrorSnackBar("Wystąpił problem podczas pobierania danych użytkownika, spróbuj jeszcze raz.")
            }

    }
    //  przypisanie użytkownika do listy zakupów
    fun assignMemberToShoppingList(activity: MembersActivity, shoppingList: ShoppingList, user: User) {
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = shoppingList.assignedTo

        mFireStore.collection(Constants.SPNGLIST)
            .document(shoppingList.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName,"Error while updating user.",e)
            }
    }

    //  przypisanie sklepu do listy zakupów
    fun assignShopToShoppingList(activity: AddShopActivity, shoppingList: ShoppingList) {
        val shopHashMap = HashMap<String, Any>()
        shopHashMap[Constants.SHOP_LIST] = shoppingList.shopList
        Toast.makeText(activity,"shopHashMap: $shopHashMap",Toast.LENGTH_LONG).show()
        mFireStore.collection(Constants.SPNGLIST)
            .document(shoppingList.documentId)
            .update(shopHashMap)
            .addOnSuccessListener {
                activity.shopAssignedSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName,"Error while updating shops.",e)
            }
    }
//    TODO usuwanie użytkowników z listy
//    TODO usuwanie produktów z listy
//    TODO dodanie sklepu do listy
}