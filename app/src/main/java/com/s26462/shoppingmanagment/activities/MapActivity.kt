package com.s26462.shoppingmanagment.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.database.*
import com.google.protobuf.DescriptorProtos
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import com.s26462.shoppingmanagment.interfaces.OnLoadLocationListener
import com.s26462.shoppingmanagment.jobs.ReminderJobService
import com.s26462.shoppingmanagment.models.Shop
import com.s26462.shoppingmanagment.receivers.GeofenceReceiver
import com.s26462.shoppingmanagment.utils.Constants
import kotlinx.android.synthetic.main.activity_map.*
import java.util.*
import kotlin.collections.ArrayList

class MapActivity : BaseActivity(), OnMapReadyCallback, OnLoadLocationListener,
    GeoQueryEventListener {

    private var mShopDetail: Shop? = null
    private var mShopList: ArrayList<Shop>? = null
    private val GEOFENCE_LOCATION_REQUEST_CODE = 123

    private lateinit var geofencingClient: GeofencingClient

    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentMarker: Marker
    private var mShopLatLngList: MutableList<LatLng> = ArrayList()
    private var mLatLngList: MutableList<LatLng> = ArrayList()
    private lateinit var myLocationRef:DatabaseReference
    private lateinit var listener: OnLoadLocationListener
    private var radius = 500.0

    private lateinit var myShop: DatabaseReference
    private lateinit var lastLocation: Location
    private var geoQuery: GeoQuery? = null
    private lateinit var geoFire: GeoFire

    private val mPermissionMessage = "Brak wymaganych uprawnień do dostępu do lokalizacji. " +
            "Możesz dodać je później w ustawieniach aplikacji, albo przejść do nich teraz."


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        FirestoreClass().getShopList(this)

        Log.e(this.javaClass.simpleName, "intent.hasExtra(Constants.SHOPS): ${
            intent.hasExtra(
                Constants.SHOPS
            )
        }")
        if(intent.hasExtra(Constants.SHOP_DETAIL)){
            mShopDetail = intent.getParcelableExtra(Constants.SHOP_DETAIL)
        } else if (intent.hasExtra(Constants.SHOPS)){
            mShopList = intent.getParcelableArrayListExtra(Constants.SHOPS)
            Log.e(this.javaClass.simpleName, "mShopList: $mShopList")
        }

        if(mShopDetail != null) {
            setSupportActionBar(toolbar_map)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mShopDetail!!.name

            toolbar_map.setNavigationOnClickListener {
                onBackPressed()
            }
        }

        geofencingClient = LocationServices.getGeofencingClient(this)

        val supportMapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        supportMapFragment.getMapAsync(this)

        isLocationPermissionGranted()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.e(this.javaClass.simpleName, "mShopList: $mShopList")
        mMap = googleMap
        if(mShopList != null){
            for(i in mShopList!!.indices) {
//                Log.e(this.javaClass.simpleName, "mShopList!![i].latitude: ${mShopList!![i].latitude}")
//                Log.e(this.javaClass.simpleName, "mShopList!![i].longitude: ${mShopList!![i].longitude}")
                val position = LatLng(mShopList!![i].latitude, mShopList!![i].longitude)
                val name = mShopList!![i].name

                mMap!!.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(name)
                )

                val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, Constants.ZOOM_MAP_LIST)
                mMap.animateCamera(newLatLngZoom)

            }
        } else if (mShopDetail != null) {
            val position = LatLng(mShopDetail!!.latitude, mShopDetail!!.longitude)
            val radius = mShopDetail!!.radius.toDouble()

            mMap!!.addCircle(
                CircleOptions()
                    .center(position)
                    .radius(radius)
            )

            val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, Constants.ZOOM_MAP)
            mMap.animateCamera(newLatLngZoom)
        }
//        scheduleJob()

        mMap!!.uiSettings.isZoomControlsEnabled = true
        if (fusedLocationProviderClient != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                fusedLocationProviderClient!!.requestLocationUpdates(locationRequest,locationCallback!!,
                    Looper.myLooper())
                Log.e(this.javaClass.simpleName, "addCircle() mShopLatLngList: ${mShopLatLngList}")
//                if (mShopLatLngList != null) {
//                    addCircleArea()
//                }
            }
        }
    }

    override fun onLocationLoadSucces(latLngs: List<Shop>) {
        mShopLatLngList = ArrayList()
        for (myLatLng in latLngs) {
            val convert = LatLng(myLatLng.latitude,myLatLng.longitude)
            mShopLatLngList!!.add(convert)
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (mMap != null) {
            mMap!!.clear()
            addUserMarker()
            addCircleArea()
        }

    }

    private fun addCircleArea() {
        if (geoQuery != null) {
            geoQuery!!.removeGeoQueryEventListener(this@MapActivity)
            geoQuery!!.removeAllListeners()
        }

        Toast.makeText(this@MapActivity, "adasdasdasdasdasdasdasdasd",Toast.LENGTH_SHORT).show()

        Toast.makeText(this@MapActivity, "mShopLatLngList: $mShopLatLngList",Toast.LENGTH_SHORT).show()

        for (latLng in mShopLatLngList!!){
            mMap!!.addCircle(CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f))

            geoQuery = geoFire!!.queryAtLocation(GeoLocation(latLng.latitude, latLng.longitude),0.5)
            Toast.makeText(this@MapActivity, "geoQuery: $geoQuery",Toast.LENGTH_SHORT).show()
            geoQuery!!.addGeoQueryEventListener(this@MapActivity)
        }

    }

    override fun onLocationLoadFailed(message: String) {
        Toast.makeText(this," $message", Toast.LENGTH_SHORT).show()
    }

    private fun buildLocationCallback(){
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (mMap != null){
                    lastLocation = locationResult!!.lastLocation
                    addUserMarker()
                }
            }
        }
    }

    private fun addUserMarker() {
        geoFire!!.setLocation("Ty", GeoLocation(lastLocation!!.latitude,
        lastLocation!!.longitude)) {
            _,_ -> if(currentMarker != null) {
                currentMarker.remove()
            }
            currentMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(lastLocation!!.longitude,
            lastLocation!!.longitude))
                .title("Ty"))

            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker!!.position, Constants.ZOOM_MAP))
        }
    }

    private fun buildLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 3000
        locationRequest!!.smallestDisplacement = 10f

    }

    fun GeofenceList(shopsLists : ArrayList<Shop>){

//        val sLists: ArrayList<LatLng> = ArrayList()
        for(i in shopsLists){
//            val id = i.id
            val location = LatLng(i.latitude, i.longitude)
            Log.e(this.javaClass.simpleName, "GeofenceList location: $location")
//            radius = i.radius.toDouble()
            mShopLatLngList.add(location)
//            sLists.add(location)
            Log.e(this.javaClass.simpleName, "GeofenceList mShopLatLngList: ${mShopLatLngList}")
        }
        addCircleArea()
    }

    private fun isLocationPermissionGranted() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if(report!!.areAllPermissionsGranted()){
                    buildLocationRequest()
                    buildLocationCallback()
                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(this@MapActivity)
                    initArea()
                    settingGeoFire()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>,
                                                            token: PermissionToken
            ) {
                showRationalDialogForPermissions(mPermissionMessage)
            }
        }).onSameThread().check()
    }

    private fun settingGeoFire() {
        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation")
        geoFire = GeoFire(myLocationRef)
    }

    private fun initArea() {
        myShop = FirebaseDatabase.getInstance()
            .getReference("ShopArea")
            .child("Sklep")

        listener = this

        myShop!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                mShopList = ArrayList<Shop>()
                for(locationSnapshot in snapshot.children) {
                    val latLng = locationSnapshot.getValue(Shop::class.java)
                    mShopList!!.add(latLng!!)
                }
                listener!!.onLocationLoadSucces(mShopList!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendNotification(title: String, content: String){
        Toast.makeText(this,"content: $content", Toast.LENGTH_SHORT).show()

        val NOTIFICATION_CHANNEL_ID = "multiple_location"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "MyNotification",NotificationManager.IMPORTANCE_DEFAULT)

            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED

            notificationManager.createNotificationChannel(notificationChannel)

            var builder = NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
            builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_launch)

            val notification = builder.build()
            notificationManager.notify(Random().nextInt(),notification)
        }
    }

    override fun onStop(){
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
        super.onStop()
    }

    override fun onKeyEntered(key: String?, location: GeoLocation?) {
        sendNotification("ShoppingManagment", String.format("Znalazłeś się w strefie $key"))
    }

    override fun onKeyExited(key: String?) {
        sendNotification("ShoppingManagment", String.format("Wyszedłeś ze sklepu $key"))
    }

    override fun onKeyMoved(key: String?, location: GeoLocation?) {
        sendNotification("ShoppingManagment", String.format("poruszyłeś się $key"))
    }

    override fun onGeoQueryReady() {
        TODO("Not yet implemented")
    }

    override fun onGeoQueryError(error: DatabaseError?) {
        Toast.makeText(this,"${error!!.message}", Toast.LENGTH_SHORT).show()
    }
}
