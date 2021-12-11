package com.s26462.shoppingmanagment.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.s26462.shoppingmanagment.activities.*
import com.s26462.shoppingmanagment.models.ShoppingList
import com.s26462.shoppingmanagment.models.User
import com.s26462.shoppingmanagment.utils.Constants

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
//utworzenie listy zakupów w bazie
    fun createShoppingList(activity: CreateShoppingListActivity, spngList: ShoppingList) {
        mFireStore.collection(Constants.SPNGLIST)
            .document()
            .set(spngList, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Shopping List created successfully.")
                Toast.makeText(activity, "Stworzono nową listę zakupów", Toast.LENGTH_SHORT).show()
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
    Toast.makeText(activity,"getShoppingList", Toast.LENGTH_SHORT).show()
        mFireStore.collection(Constants.SPNGLIST)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val shoppingLists: ArrayList<ShoppingList> = ArrayList()
                for(i in document.documents){
                    val shoppingList = i.toObject(ShoppingList::class.java)!!
                    shoppingList.documentId = i.id
                    shoppingLists.add(shoppingList)
                }
                Toast.makeText(activity,"Jest lista", Toast.LENGTH_SHORT).show()
                activity.populateShoppingListToUI(shoppingLists)
            }
            .addOnFailureListener {
                e ->
                Toast.makeText(activity,"nie ma listy", Toast.LENGTH_SHORT).show()
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a ShoppingLists.", e)
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
                    is MyProfileActivity ->
                        activity.setUserDataInUI(loggedInUser)

                    is CreateShoppingListActivity ->
                        activity.loadUserImage(loggedInUser)
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
}