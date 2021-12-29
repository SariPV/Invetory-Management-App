package com.example.manageyourinventoryapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import kotlinx.android.synthetic.main.activity_create_product.*
import android.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_product.category
import kotlinx.android.synthetic.main.activity_create_product.addPrice
import kotlinx.android.synthetic.main.activity_create_product.createButton
import java.util.*

private const val REQUEST_CODE = 100
private const val PICK_IMAGE = 200

class CreateProduct : AppCompatActivity() {
    private var imageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)

        auth = FirebaseAuth.getInstance()


        val popupMenu = PopupMenu(this, addImage)
        popupMenu.menuInflater.inflate(R.menu.image_option, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->

            val id = menuItem.itemId
            if (id == R.id.uploadImage) {
                val photo = Intent(Intent.ACTION_PICK)
                photo.type = "image/*"
                startActivityForResult(photo, PICK_IMAGE)
            } else if (id == R.id.takePhoto) {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePicture.resolveActivity(this.packageManager) != null) {
                    startActivityForResult(takePicture, REQUEST_CODE)
                } else {
                    Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
                }
            }
            true

        }
        addImage.setOnClickListener {
            popupMenu.show()
        }





        createButton.setOnClickListener {
            uploadImage()


        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null){

            imageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            addImage.setImageBitmap(bitmap)


        }
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
//            val takenImage = data?.extras?.get("data") as Bitmap
            addImage.setImageBitmap(data.extras?.get("data") as Bitmap)
        //            addImage.setImageURI(data?.data)
        }
       else{
            super.onActivityResult(requestCode, resultCode, data)
        }



    }
    private fun uploadImage(){
        if (imageUri == null)return
        val filename = UUID.randomUUID().toString()
        storageRef = FirebaseStorage.getInstance().getReference("/product/$filename")
        storageRef.putFile(imageUri!!).addOnSuccessListener {
            Toast.makeText(this,"Profile successfully upload",Toast.LENGTH_SHORT).show()
            storageRef.downloadUrl.addOnSuccessListener {
                saveProductToDatabase(it.toString())
            }
        }.addOnFailureListener{
            Toast.makeText(this,"Fail to upload image",Toast.LENGTH_SHORT).show()

        }

    }

    private fun saveProductToDatabase(imgUrl: String){
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().reference.child("/Users/$uid/product")
        val productName = addName.text.toString()
        val price = addPrice.text.toString().toDouble()
        val id = addID.text.toString()
        val amount = addAmount.text.toString().toInt()
        val category = category.text.toString()

        val product = Product(imgUrl, id, productName, price, category, amount)

        ref.child(id).setValue(product).addOnSuccessListener {
            Log.d("CreateProduct","Suucesfully create product")
        }.addOnFailureListener {
                Toast.makeText(this, "Failed to create product", Toast.LENGTH_SHORT).show()
        }

        var intent = Intent(this, InventoryActivity::class.java)
        startActivity(intent)
    }




}