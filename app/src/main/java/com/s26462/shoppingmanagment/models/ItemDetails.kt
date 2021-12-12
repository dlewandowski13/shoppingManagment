package com.s26462.shoppingmanagment.models

import android.os.Parcel
import android.os.Parcelable

data class ItemDetails (
    val name: String = "",
    val amount: String = "",
    val price: String = "",
    val bought: Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeString(name)
        writeString(amount)
        writeString(price)
        writeByte(if (bought) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemDetails> {
        override fun createFromParcel(parcel: Parcel): ItemDetails {
            return ItemDetails(parcel)
        }

        override fun newArray(size: Int): Array<ItemDetails?> {
            return arrayOfNulls(size)
        }
    }
}