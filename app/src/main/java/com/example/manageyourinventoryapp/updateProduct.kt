package com.example.manageyourinventoryapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import com.chayangkoon.champ.glide.ktx.clear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_product.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import kotlin.collections.HashMap

private const val REQUEST_CODE = 100
private const val PICK_IMAGE = 200

class updateProduct : AppCompatActivity() {
    private lateinit var databaseref: DatabaseReference
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product)

        createButton.setOnClickListener {
            val id = addID.text.toString()
            val name = addName.text.toString()
            val upprice = addPrice.text.toString().toDouble()
            val amount = addAmount.text.toString().toInt()
            val upcategory = category.text.toString()
            val imgUrl = addImage.toString()

            if (imageUri != null) {


                val filename = UUID.randomUUID().toString()
                storageRef = FirebaseStorage.getInstance().getReference("/product/$filename")
                storageRef.putFile(imageUri!!).addOnSuccessListener {
                    Toast.makeText(this, "Profile successfully upload", Toast.LENGTH_SHORT).show()
                    storageRef.downloadUrl.addOnSuccessListener {
                        updateProduct(it.toString(),id, name, upprice, upcategory, amount)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Fail to upload image", Toast.LENGTH_SHORT).show()

                }
            }
            val inventory = Intent(this, InventoryActivity::class.java)
            startActivity(inventory)




        }

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null){

            imageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            addImage.setImageBitmap(bitmap)


        }
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val takenImage = data.extras?.get("data") as Bitmap
          //  addImage.setImageBitmap(data.extras?.get("data") as Bitmap)
            addImage.setImageBitmap(takenImage)
            //addImage.setImageURI(data?.data)
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }



    }

    private fun updateProduct(
        imgUrl: String,
        id: String,
        name: String,
        upprice: Double,
        upcategory: String,
        amount: Int
    ) {
        val uid = FirebaseAuth.getInstance().uid ?: ""

        databaseref = FirebaseDatabase.getInstance().getReference("Users/$uid/product")
        val product = HashMap<String, Any>()
        product["id"] = id
        product["name"] = name
        product["price"] = upprice
        product["amount"] = amount
        product["category"] = upcategory
        product["image"] = imgUrl



            databaseref.child(id).updateChildren(product).addOnSuccessListener {


                addID.text.clear()
                addName.text.clear()
                addPrice.text.clear()
                addAmount.text.clear()
                category.text.clear()
                addImage.clear()
                Toast.makeText(this, "Successfuly Updated", Toast.LENGTH_SHORT).show()


            }.addOnFailureListener {

                Toast.makeText(this, "Failed to Update", Toast.LENGTH_SHORT).show()

            }


    }
}


