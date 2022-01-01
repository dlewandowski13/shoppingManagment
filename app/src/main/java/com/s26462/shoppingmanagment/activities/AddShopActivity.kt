package com.s26462.shoppingmanagment.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.firebase.FirestoreClass
import com.s26462.shoppingmanagment.models.Shop
import com.s26462.shoppingmanagment.models.ShoppingList
import com.s26462.shoppingmanagment.utils.Constants
import kotlinx.android.synthetic.main.activity_add_shop.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_shop_list.*
import java.io.IOException

class AddShopActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }
    private var mSelectedImageFileUri: Uri? = null
    private var mShopImageURL : String = ""
    private lateinit var mItem: ShoppingList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shop)
//
//        Toast.makeText(this,"mIntent: ${intent.hasExtra(Constants.SHOPPINGLIST_DETAIL)}",
//        Toast.LENGTH_SHORT).show()

        if(intent.hasExtra(Constants.SHOPPINGLIST_DETAIL)){
            mItem = intent.getParcelableExtra(Constants.SHOPPINGLIST_DETAIL)!!
        }

        setupActionBar()

        iv_shop_image.setOnClickListener(this)
        btn_create_shop.setOnClickListener(this)

        FirestoreClass().loadUserData(this, false)
    }

//  wszystkie akcje kliknięcia
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.iv_shop_image -> {
                val pictureDilog = AlertDialog.Builder(this)
                pictureDilog.setTitle("Wybierz źródło")
                val pictureDialogItems = arrayOf("Otwórz galerię")
//                val pictureDialogItems = arrayOf("Otwórz galerię", "Otwórz aparat")
                pictureDilog.setItems(pictureDialogItems){
                    _, which ->
                    when(which){
                        0 -> {
                            choosePhotoFromGallery()
                        }
//                        TODO znaleźć rozwiązania pobrania URI, albo najpierw zapisać obraz lokalnie
//                        1 -> {
//                            takePhotoFromCamera()
//                        }
                    }
                }
                pictureDilog.show()
            }
            R.id.btn_create_shop -> {
                uploadShopImage()
            }
        }
    }

//Ustawienia actionBar
    private fun setupActionBar(){
        setSupportActionBar(toolbar_add_shop)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.add_Shop)
        }

    toolbar_add_shop.setNavigationOnClickListener { onBackPressed() }
    }
//  Załadowanie obrazka z galerii
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                if (data != null){
                    mSelectedImageFileUri  = data.data
                    try {
                        val selectetImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, mSelectedImageFileUri)
                        iv_shop_image.setImageBitmap(selectetImageBitmap)
                    } catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddShopActivity, "Nieudane załadowanie pliku z galerii", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if(requestCode == CAMERA_REQUEST_CODE) {
                val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap
                mSelectedImageFileUri = data!!.data
                iv_shop_image.setImageBitmap(thumbnail)
            }
        }
    }

//  dodanie obrazka bezpośrednio z kamery
    private fun takePhotoFromCamera(){
    Dexter.withContext(this).withPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    ).withListener(object: MultiplePermissionsListener {
        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
            if(report!!.areAllPermissionsGranted()){
                showCamera()
            }
        }
        override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>,
                                                        token: PermissionToken) {
            showRationalDialogForPermissions()
        }
    }).onSameThread().check()
    }

//  dodanie obrazka z galerii
    private fun choosePhotoFromGallery(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if(report!!.areAllPermissionsGranted()){
                    showImageChooser()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>,
                                                            token: PermissionToken) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }
    private fun showRationalDialogForPermissions(){
//        Toast.makeText(this@AddShopActivity, "galeria", Toast.LENGTH_SHORT).show()
        AlertDialog.Builder(this)
            .setMessage("Brak wymaganych uprawnień do skorzystania z tej funkcji. " +
                    "Możesz dodać je później w ustawieniach aplikacji, albo przejść do nich teraz.")
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

    private fun showImageChooser(){
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun showCamera(){
        var galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        startActivityForResult(galleryIntent, CAMERA_REQUEST_CODE)
    }

//  zapisanie pliku do storage w Firebase + logi pomocnicze plus wywołanie funkcji aktualizującej dane w bazie
    private fun uploadShopImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
//        Toast.makeText(this@AddShopActivity, "mSelectedImageFileUri: $mSelectedImageFileUri", Toast.LENGTH_LONG).show()
        if(mSelectedImageFileUri != null) {
            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "SHOP_IMAGE" + System.currentTimeMillis() + "."
                            + getFileExtention(mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    mShopImageURL = uri.toString()
                    Log.i("Downloadable Image URL", mShopImageURL)
                    addShop()
                }
            }.addOnFailureListener{
                    exception ->
                Toast.makeText(this@AddShopActivity, exception.message, Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
        addShop()
    }

//  rozszerzenie pliku
    private fun getFileExtention(uri: Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

//aktualizacja danych użytkownika - stworzenie HashMap dla users
    private fun addShop(){
        val shopHashMap = HashMap<String, Any>()
        val shopName = et_shop_name.text.toString()
        val shopDescription = et_shop_descrioption.text.toString()
        val shopRadius = et_shop_radius.text.toString()
//    TODO cała obsługa lokalizacji zostawiam, jak do tego dojdę
//        val shopLocation = et_shop_location.text.toString()
        var anyChangesMade = false

        if (mShopImageURL.isNotEmpty() && shopName.isNotEmpty() && shopDescription.isNotEmpty()
            && shopRadius.isNotEmpty()) {
            shopHashMap[Constants.SHOP_NAME] = shopName
            shopHashMap[Constants.SHOP_DESCRIPTION] = shopDescription
            shopHashMap[Constants.SHOP_RADIUS] = shopRadius
            shopHashMap[Constants.SHOP_IMAGE] = mShopImageURL
            anyChangesMade = true
        }

        if (anyChangesMade) {
            FirestoreClass().createShop(this, shopHashMap)
        } else {
            Toast.makeText(this@AddShopActivity, "Pola nie mogą być puste!",
            Toast.LENGTH_LONG).show()
            hideProgressDialog()
        }

    }

    fun shopCreatedSuccess(name: String){
//        mItem.shopList.add(shop.id)
        Toast.makeText(this,"name: $name",Toast.LENGTH_LONG).show()
        FirestoreClass().getShopDetails(this@AddShopActivity, name)
    }

    fun assigneeShop(shop: Shop){
        mItem.shopList.add(shop.id)
        FirestoreClass().assignShopToShoppingList(this@AddShopActivity,mItem,shop)
    }

    fun shopAssignedSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
        val intent = Intent(this, ShopListActivity::class.java)
        startActivity(intent)
    }

}