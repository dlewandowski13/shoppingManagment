package com.s26462.shoppingmanagment.models

import android.os.Parcel
import android.os.Parcelable

data class Shop (
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val radius: Long = 0,
    val image: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    val assignedTo: ArrayList<String> = ArrayList()
        ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()!!,
        parcel.readString()!!,
        parcel.readDouble()!!,
        parcel.readDouble()!!,
        parcel.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeString(id)
        writeString(name)
        writeString(description)
        writeLong(radius)
        writeString(image)
        writeDouble(latitude)
        writeDouble(longitude)
        writeStringList(assignedTo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Shop> {
        override fun createFromParcel(parcel: Parcel): Shop {
            return Shop(parcel)
        }

        override fun newArray(size: Int): Array<Shop?> {
            return arrayOfNulls(size)
        }
    }
}