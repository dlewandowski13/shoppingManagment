package com.s26462.shoppingmanagment.interfaces

import com.s26462.shoppingmanagment.models.Shop

interface OnLoadLocationListener {
    fun onLocationLoadSucces(latLngs: List<Shop>)
    fun onLocationLoadFailed(message: String)

}