package com.s26462.shoppingmanagment.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {


        val geoEvent = GeofencingEvent.fromIntent(intent)
        val triggering = geoEvent.triggeringGeofences
        for( geo in triggering){
            Log.i("geofence", "Geofence z id: ${geo.requestId} aktywny.")
        }
        if(geoEvent.geofenceTransition ==
            Geofence.GEOFENCE_TRANSITION_ENTER){
            Log.i("geofences", "Wejście: ${geoEvent.triggeringLocation.toString()}")
        }else if(geoEvent.geofenceTransition ==
            Geofence.GEOFENCE_TRANSITION_EXIT){
            Log.i("geofences", "Wyjście: ${geoEvent.triggeringLocation.toString()}")
        }else{
            Log.e("geofences", "Error.")
        }
    }
}
