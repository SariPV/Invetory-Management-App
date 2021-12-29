package com.example.manageyourinventoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.product_list.view.*


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private lateinit var user: ArrayList<User>
    private val uid = FirebaseAuth.getInstance().uid?: ""





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate() called")

        databaseref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        user = ArrayList()

        if (uid.isNotEmpty()) {
            userProfile()
        }


        InventoryCard.setOnClickListener {
            val inventoryPage = Intent(this, InventoryActivity::class.java)
            startActivity(inventoryPage)

        }

        InvoiceCard.setOnClickListener {
            val invoicePage = Intent(this, InvoiceActivity::class.java)
            startActivity(invoicePage)

        }

        SalesCard.setOnClickListener {

            val salesReportPage = Intent(this, SalesReport::class.java)
            startActivity(salesReportPage)
            finish()
        }

        LogOutCard.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val logoutPage = Intent(this, LoginActivity::class.java)
            startActivity(logoutPage)
        }

    }
    private fun userProfile() {
        databaseref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(User::class.java)!!
                UserName.setText(post.username)
                Glide.with(this@MainActivity).load(post.profileUrl).into(logprofileImage)

                // ...
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }




    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

}



