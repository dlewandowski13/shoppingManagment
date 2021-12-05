package com.s26462.shoppingmanagment.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.s26462.shoppingmanagment.activities.SignInActivity
import com.s26462.shoppingmanagment.activities.SignUpActivity
import com.s26462.shoppingmanagment.models.User
import com.s26462.shoppingmanagment.utils.Constants

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

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

    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null) {
                        activity.signInSuccess(loggedInUser)
                    }
            } .addOnFailureListener {
                    e->
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