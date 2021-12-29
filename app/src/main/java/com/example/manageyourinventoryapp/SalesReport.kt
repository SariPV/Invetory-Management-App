package com.example.manageyourinventoryapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_create_invoice.*
import kotlinx.android.synthetic.main.activity_create_product.*
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.android.synthetic.main.activity_invoice.*



import kotlinx.android.synthetic.main.activity_sales_report.*
import kotlinx.android.synthetic.main.invoice_list.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class SalesReport : AppCompatActivity() {
    private lateinit var listInvoice: ArrayList<Invoice>
    private lateinit var listProduct: ArrayList<Product>
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    private var itemleft = 0
    private var quantityProduct = 0
    private var quantityInvoice = 0
    private val date = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_report)


        auth = FirebaseAuth.getInstance()

        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        toolbar?.setTitle("Sales Report")
        toolbar?.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        toolbar!!.setNavigationOnClickListener(View.OnClickListener {
            var mainpage = Intent(this, MainActivity::class.java)
            startActivity(mainpage)
        })

        databaseref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        listInvoice = arrayListOf<Invoice>()
        listProduct = arrayListOf<Product>()

        if (uid.isNotEmpty()) {

            sales()
        }
    }
    private fun sales() {


        val currentTime: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()
        Toast.makeText(this,"$currentTime",Toast.LENGTH_SHORT).show()
        var todaysales = 0.0
        var lastweek = 0.0
        var lastmonth = 0.0
        val week: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(-7)).toString()
        val month: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(-30)).toString()



        databaseref.child("product").addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                for (product in snapshot.children) {
                    val products = product.getValue(Product::class.java)
                    quantityProduct += products?.amount!!
                }
                quantityLeft.setText(quantityProduct.toString())

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        currentdate.setText(currentTime)
        databaseref.child("Invoice").addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                for (invoice in snapshot.children) {
                    val invoices = invoice.getValue(Invoice::class.java)
                    quantityInvoice += invoices?.quantity!!
                    if (currentTime == invoices.paymentDate){
                        todaysales += invoices.price
                    }


                    if (week < invoices.paymentDate && invoices.paymentDate < currentTime){
                            lastweek += invoices.price
                    }


                    if (month < invoices.paymentDate && invoices.paymentDate < currentTime){
                        lastmonth += invoices.price
                    }


                }
                quantitySold.setText(quantityInvoice.toString())
                Todayrevenue.setText(todaysales.toString())
                weekRevenue.setText(lastweek.toString())
                monthRevenue.setText(lastmonth.toString())

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        val leftitem = findViewById<TextView>(R.id.quantityLeft)
        leftitem.setText((quantityProduct - quantityInvoice).toString())


    }


}




