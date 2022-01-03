package com.s26462.shoppingmanagment.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.models.Shop
import com.s26462.shoppingmanagment.utils.Constants
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : BaseActivity(), OnMapReadyCallback {

    private var mShopDetail: Shop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if(intent.hasExtra(Constants.SHOP_DETAIL)){
            mShopDetail = intent.getParcelableExtra(Constants.SHOP_DETAIL)
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
        val position = LatLng(mShopDetail!!.latitude,mShopDetail!!.longitude)
        val radius = mShopDetail!!.radius.toDouble()

        googleMap!!.addCircle(CircleOptions()
            .center(position)
            .radius(radius))

        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position,Constants.ZOOM_MAP)
        googleMap.animateCamera(newLatLngZoom)

    }
}
