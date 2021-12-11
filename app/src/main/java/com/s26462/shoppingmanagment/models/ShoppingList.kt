package com.s26462.shoppingmanagment.models

import android.os.Parcel
import android.os.Parcelable

data class ShoppingList (
    val name: String = "",
    val image: String = "", //TODO spróbować załadować obrazek z użytkownika, który stworzył listę
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeString(name)
        writeString(image)
        writeString(createdBy)
        writeStringList(assignedTo)
        writeString(documentId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShoppingList> {
        override fun createFromParcel(parcel: Parcel): ShoppingList {
            return ShoppingList(parcel)
        }

        override fun newArray(size: Int): Array<ShoppingList?> {
            return arrayOfNulls(size)
        }
    }
}