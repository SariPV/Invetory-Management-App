package com.example.manageyourinventoryapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_product.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap

class SignUp : AppCompatActivity() {

    private var imageUri: Uri? = null
    private lateinit var auth:FirebaseAuth
    private lateinit var users:DatabaseReference
    private var userID: String = ""
    private lateinit var storageReferrence: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        Loginlink.setOnClickListener {
            var loginpage = Intent(this, LoginActivity::class.java)
            startActivity(loginpage)

        }
        auth = FirebaseAuth.getInstance()
        signUpButton.setOnClickListener {
            registerUser()
        }
        logprofileImage.setOnClickListener {
            val photo = Intent(Intent.ACTION_PICK)
            photo.type = "image/*"

            startActivityForResult(photo, 0)
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            imageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            logprofileImage.setImageBitmap(bitmap)
//            logprofileImage.alpha = 0f

        }


    }
    private fun registerUser(){
        val username: String = username.text.toString()
        val email: String = email.text.toString()
        val password: String = password.text.toString()
        val confirmPassword: String = conpassword.text.toString()



        if (username.equals("")){
            Toast.makeText(this,"Please enter your Username",Toast.LENGTH_LONG).show()
        }else if(email.equals("")){
            Toast.makeText(this,"Please enter your Email",Toast.LENGTH_LONG).show()


        }else if (password.equals("")){
            Toast.makeText(this,"Please enter your Password",Toast.LENGTH_LONG).show()

        }else if (confirmPassword.equals("")){
            Toast.makeText(this,"Please confirm your Password",Toast.LENGTH_SHORT).show()

        }else if (confirmPassword != password) {
            Toast.makeText(this,"The password confirmation does not match",Toast.LENGTH_SHORT).show()

        }else{

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task->
                if (task.isSuccessful){
                    userID = auth.currentUser!!.uid
                    users = FirebaseDatabase.getInstance().reference.child("Users").child(userID)

                    val userHashMap = HashMap<String,Any>()
                    userHashMap["uid"] = userID
                    userHashMap["username"] = username
                    userHashMap["email"] = email
                    uploadImage()

                    users.updateChildren(userHashMap).addOnCompleteListener { task->
                        if (task.isSuccessful){

                            var main = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(main)
                            finish()
                            Toast.makeText(this@SignUp,"you have sucessfully create account",Toast.LENGTH_SHORT).show()
                        }
                    }


                }else{
                    Toast.makeText(this@SignUp,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()

                }
            }

        }
    }

    private fun uploadImage(){
        if (imageUri == null)return
        val filename = UUID.randomUUID().toString()
        storageReferrence = FirebaseStorage.getInstance().getReference("/image/$filename")
        storageReferrence.putFile(imageUri!!).addOnSuccessListener {
            Toast.makeText(this@SignUp,"Profile successfully upload",Toast.LENGTH_SHORT).show()
                storageReferrence.downloadUrl.addOnSuccessListener {
                    saveToDatabase(it.toString())
                }
            }.addOnFailureListener{
                Toast.makeText(this@SignUp,"Fail to upload image",Toast.LENGTH_SHORT).show()

            }

    }

    private fun saveToDatabase(imgUrl: String){
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        val username = username.text.toString()
        val email = email.text.toString()
        val user = User(uid,username,imgUrl,email)
        ref.setValue(user).addOnSuccessListener {
            Log.d("SignUp","Save to user database")
        }
    }

}
class User(val uid: String, val username:String, val profileUrl: String, val email: String){
    constructor(): this("","","","")

}