package com.example.manageyourinventoryapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_invoice.view.*
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.android.synthetic.main.activity_invoice.*
import kotlinx.android.synthetic.main.invoice_list.view.*
import java.util.*
import kotlin.collections.ArrayList

class InvoiceActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var databaseref: DatabaseReference
    private lateinit var listInvoice: ArrayList<Invoice>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        auth = FirebaseAuth.getInstance()




        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        toolbar?.setTitle("Inventory")
        toolbar?.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        //       toolbar!!.setNavigationOnClickListener { finish()
        //       }
        toolbar!!.setNavigationOnClickListener(View.OnClickListener {
            var nextpage = Intent(this,MainActivity::class.java)
            startActivity(nextpage)
        })

        var adapter: InvoiceAdapter? = null


        InvoiceList.layoutManager = LinearLayoutManager(this)
        // val invoices = sampleInvoice()
        // InvoiceList.adapter = InvoiceAdapter(listInvoice)
        listInvoice = arrayListOf<Invoice>()
        InvoiceList.setHasFixedSize(true)
        getInvoiceData()



    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

//
        menuInflater.inflate(R.menu.search_bar, menu)






        var adapter: InvoiceAdapter? = null
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    Log.d("onQueryTextChange", "query: " + query)
                    adapter?.filter?.filter(query)
                    return true
                }
            })

            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    adapter?.filter?.filter("")
                  //  showToast("Action Collapse")
                    return true
                }

                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                 //   showToast("Action Expand")
                    return true
                }
            })

            return super.onCreateOptionsMenu(menu)

//            override fun onQueryTextSubmit(query: String?): Boolean {
//                TODO()
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                listInvoice.clear()
//                val searchText = newText!!.toLowerCase(Locale.getDefault())
//                if (searchText.isNotEmpty()) {
//                    ArrayList<Invoice>().forEach() {
//
//                        if (it.customerName?.toLowerCase(Locale.getDefault())!!.contains(searchText)) {
//                            ArrayList<Invoice>().add(it)
//                        }
//                    }
//
//                    InvoiceList.adapter?.notifyDataSetChanged()
//                } else {
//                    listInvoice.clear()
//                    listInvoice.addAll(ArrayList<Invoice>())
//                    InvoiceList.adapter!!.notifyDataSetChanged()
//
//                }
//                return false
//            }
//        })
//        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.CreateNew) {
            val intent = Intent(this,CreateInvoice::class.java)
            startActivity(intent)
            return false
        }

        return super.onOptionsItemSelected(item)
    }








    private inner class InvoiceHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val productName = itemView.findViewById<TextView>(R.id.productName)
        val id = itemView.findViewById<TextView>(R.id.ID)
        val quantity = itemView.findViewById<TextView>(R.id.amount)
        val price = itemView.findViewById<TextView>(R.id.Productprice)
        val customerid = itemView.findViewById<TextView>(R.id.CustomerID)
        val customername = itemView.findViewById<TextView>(R.id.CustomerName)
        val paydate = itemView.findViewById<TextView>(R.id.payment)


        lateinit var invoice: Invoice


        fun bind(invoice: Invoice) {
            this.invoice = invoice

            customername.text = invoice.customerName
            customerid.text = invoice.customerId.toString()
            quantity.text = invoice.quantity.toString()
            id.text = invoice.productid
            price.text = invoice.price.toString()
            paydate.text = invoice.paymentDate
            productName.text = invoice.productName


        }




    }


    private inner class InvoiceAdapter(val invoices: ArrayList<Invoice>) :
        RecyclerView.Adapter<InvoiceHolder>(), Filterable {

        var invoiceFilterList = ArrayList<Invoice>()
        init{
            invoiceFilterList = invoices
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceHolder {
            val view = layoutInflater.inflate(R.layout.invoice_list, parent, false)
            return InvoiceHolder(view)

        }

        override fun onBindViewHolder(holder: InvoiceHolder, position: Int){
            val invoice = invoices[position]
            holder.bind(invoice)
//            holder.customername.text = invoice.customerName
//            holder.customerid.text = invoice.customerId.toString()
//            holder.quantity.text = invoice.quantity.toString()
//            holder.id.text = invoice.productid
//            holder.price.text = invoice.price.toString()
//            holder.paydate.text = invoice.paymentDate
//            holder.productName.text = invoice.productName


        }


        override fun getItemCount(): Int {
            return invoices.size
        }

        override fun getFilter(): Filter {

            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val charSearch = constraint.toString()
                    if (charSearch.isEmpty()) {
                        invoiceFilterList = invoices
                    } else {
                        val resultList = ArrayList<Invoice>()
                        for (row in invoices) {
                            if (row.customerName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                resultList.add(row)
                            }
                        }
                        invoiceFilterList = resultList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = invoiceFilterList
                    return filterResults
                }

                @Suppress("UNCHECKED_CAST")
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    invoiceFilterList = results?.values as ArrayList<Invoice>
                    notifyDataSetChanged()
                }

            }
        }




    }

    private fun getInvoiceData() {

        val uid = FirebaseAuth.getInstance().uid?: ""
        databaseref = FirebaseDatabase.getInstance().getReference("/Users/$uid/Invoice")

        databaseref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (invoiceSnapshot in snapshot.children) {
                        val invoice = invoiceSnapshot.getValue(Invoice::class.java)
                        listInvoice.add(invoice!!)
                    }
                    InvoiceList.adapter = InvoiceAdapter(listInvoice)
                }
            }



            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@InvoiceActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        })


    }
}