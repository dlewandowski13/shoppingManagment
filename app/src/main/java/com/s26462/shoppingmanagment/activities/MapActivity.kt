package com.s26462.shoppingmanagment.activities

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import com.s26462.shoppingmanagment.models.Shop
import com.s26462.shoppingmanagment.utils.Constants
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : BaseActivity(), OnMapReadyCallback {

    private var mShopDetail: Shop? = null
    private var mShopList: ArrayList<Shop>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
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

        val supportMapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.e(this.javaClass.simpleName, "mShopList: $mShopList")
        if(mShopList != null){
            for(i in mShopList!!.indices) {
                Log.e(this.javaClass.simpleName, "mShopList!![i].latitude: ${mShopList!![i].latitude}")
                Log.e(this.javaClass.simpleName, "mShopList!![i].longitude: ${mShopList!![i].longitude}")
                val position = LatLng(mShopList!![i].latitude, mShopList!![i].longitude)
                val name = mShopList!![i].name

                googleMap!!.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(name)
                )

                val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, Constants.ZOOM_MAP_LIST)
                googleMap.animateCamera(newLatLngZoom)

            }
        } else if (mShopDetail != null) {
            val position = LatLng(mShopDetail!!.latitude, mShopDetail!!.longitude)
            val radius = mShopDetail!!.radius.toDouble()

            googleMap!!.addCircle(
                CircleOptions()
                    .center(position)
                    .radius(radius)
            )

            val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, Constants.ZOOM_MAP)
            googleMap.animateCamera(newLatLngZoom)
        }
    }
}
