package com.s26462.shoppingmanagment.activities

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.models.Shop
import com.s26462.shoppingmanagment.models.User
import com.s26462.shoppingmanagment.utils.Constants
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {
    //zamknięcie aplikacji
    private var doubleBackToExitPressedOnce = false

    lateinit var geofencingClient: GeofencingClient

    //pasek postępu
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        geofencingClient = LocationServices.getGeofencingClient(this)
    }


//wyświetlenie okna postępu
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.tv_progress_text.text = text

        mProgressDialog.show()
    }

    //ukrycie okna postępu
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    //pobranie ID aktualnie zalogowanego użytkowika
    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    //zamknięcie activity po dwóch kliknięciach w odstępie 2 sek
    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    //osbługa błędów
    fun showErrorSnackBar(message: String) {
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)

        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.snackbar_error_color))
        snackbar.show()

    }

    fun showRationalDialogForPermissions(message: String){
//        Toast.makeText(this@AddShopActivity, "galeria", Toast.LENGTH_SHORT).show()
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("PRZEJDŹ DO USTAWIEŃ"){
                    _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Zamknij"){
                    dialog, _ ->
                dialog.dismiss()
            }.show()
    }
//  utworzenie geofence
    fun GeofenceList(shopsLists : ArrayList<Shop>){
    var geofenceList: Geofence?
        for(i in shopsLists){
                Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(i.id)
                // Set the circular region of this geofence.
                .setCircularRegion(
                    i.latitude,
                    i.longitude,
                    i.radius.toFloat()
                )
                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                // Create the geofence.
                .build()
        }
    }

    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(0)
            .addGeofences(listOf(geofence))
            .build()
    }

//    fun add(reminder: Reminder,
//            success: () -> Unit,
//            failure: (error: String) -> Unit) {
//        // 1
//        val geofence = buildGeofence(reminder)
//        if (geofence != null
//            && ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // 2
//            geofencingClient
//                .addGeofences(buildGeofencingRequest(geofence), geofencePendingIntent)
//                // 3
//                .addOnSuccessListener {
//                    saveAll(getAll() + reminder)
//                    success()
//                }
//                // 4
//                .addOnFailureListener {
//                    failure("Error")
//                }
//        }
//    }
}