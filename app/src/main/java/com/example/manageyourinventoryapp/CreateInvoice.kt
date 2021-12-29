package com.example.manageyourinventoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_invoice.*
import kotlinx.android.synthetic.main.activity_create_invoice.CustomerName
import kotlinx.android.synthetic.main.activity_create_product.category
import kotlinx.android.synthetic.main.activity_create_product.createButton

class CreateInvoice : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private lateinit var listProduct: ArrayList<Product>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_invoice)
        auth = FirebaseAuth.getInstance()

        createButton.setOnClickListener {

            val uid = FirebaseAuth.getInstance().uid?: ""

            val customerName = CustomerName.text.toString()
            val quantity = Quantity.text.toString().toInt()
            val id = InvoiceID.text.toString().toInt()
            val payment = PaymentDate.text.toString()
            val category = category.text.toString()
            val productID = ProductID.text.toString()
            val totalPrice = addPrice.text.toString().toDouble()
            val itemName = productNameIn.text.toString()

            databaseref = FirebaseDatabase.getInstance().getReference("/Users/$uid/Invoice")
            val invoice = Invoice(id,customerName,itemName,quantity,category ,productID, totalPrice,payment )

                    databaseref.child(id.toString()).setValue(invoice).addOnSuccessListener {
                        var intent = Intent(this, InvoiceActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Successfully create product", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to create product", Toast.LENGTH_SHORT).show()
                    }

                    var intent = Intent(this, InventoryActivity::class.java)
                    startActivity(intent)
           }


    }



}
