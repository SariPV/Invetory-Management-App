package com.example.manageyourinventoryapp

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.media.CamcorderProfile.get
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.Os.remove
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.android.synthetic.main.product_list.*
import kotlinx.android.synthetic.main.product_list.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference
import java.lang.reflect.Array.get
import com.google.firebase.database.Exclude
import com.squareup.picasso.Picasso


class InventoryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private lateinit var listProduct: ArrayList<Product>

    private val uid = FirebaseAuth.getInstance().uid ?: ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)


        auth = FirebaseAuth.getInstance()
        val User = auth.currentUser


        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        toolbar?.setTitle("Inventory")
        toolbar?.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        toolbar!!.setNavigationOnClickListener(View.OnClickListener {
            var nextpage = Intent(this, MainActivity::class.java)
            startActivity(nextpage)
        })

        val uid = FirebaseAuth.getInstance().uid ?: ""
        databaseref = FirebaseDatabase.getInstance().getReference("/Users/$uid/product")

        plist.layoutManager = LinearLayoutManager(this)
        listProduct = ArrayList()
        plist.setHasFixedSize(true)
        plist.adapter = ProductAdapter(listProduct)
        getProductData()


    }


    private fun getProductData() {
        databaseref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        listProduct.add(product!!)
                    }
                    plist.adapter = ProductAdapter(listProduct)
                }
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@InventoryActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

    val productList = ArrayList<Product>()
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.search_bar, menu)


        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO()
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listProduct.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    listProduct.forEach() {
                        if (it.name?.toLowerCase(Locale.getDefault())!!.contains(searchText)) {
                            listProduct.add(it)
                        }
                    }

                    plist.adapter?.notifyDataSetChanged()
                } else {
                    listProduct.clear()
                    listProduct.addAll(ArrayList<Product>())
                    plist.adapter!!.notifyDataSetChanged()

                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.CreateNew) {
            val intent = Intent(this, CreateProduct::class.java)
            startActivity(intent)
            return false
        }

        return super.onOptionsItemSelected(item)
    }


    private inner class ProductHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName = itemView.productName

        //        val productImage = itemView.productImage
        val id = itemView.ID
        val amount = itemView.amount
        val price = itemView.Productprice

        val optionBthn = itemView.options




        lateinit var product: Product


        fun bind(product: Product) {
            this.product = product
            productName.text = product.name
            price.text = product.price.toString()
            id.text = product.id
            amount.text = product.amount.toString()

            //     Picasso.get().load(product.image).into(itemView.productImage)
//            Glide.with(this@InventoryActivity).load(product.image).into(itemView.productImage)


        }


    }


    private inner class ProductAdapter(var products: ArrayList<Product>) :
        RecyclerView.Adapter<ProductHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
            val view = layoutInflater.inflate(R.layout.product_list, parent, false)
            return ProductHolder(view)

        }

        override fun onBindViewHolder(
            holder: ProductHolder, @SuppressLint("RecyclerView") position: Int
        ) {
            val product = products[position]


            Glide.with(holder.itemView).load(product.image).into(holder.itemView.productImage)

            holder.bind(product)


            holder.optionBthn.setOnClickListener {
                val popup = PopupMenu(this@InventoryActivity, holder.optionBthn)
                popup.inflate(R.menu.modify_product)
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        when (item.getItemId()) {
                            R.id.UpdateProduct -> {
                                val update = Intent(this@InventoryActivity, updateProduct::class.java)
                                startActivity(update)
                                return true
                            }


                            R.id.DeleteProduct -> {

                                products.get(position).let { databaseref.child(product.id).removeValue() }
//                                delete(position)
                                products.removeAt(position)
                                notifyItemRemoved(position)
                                notifyDataSetChanged()
                                Toast.makeText(
                                    this@InventoryActivity,
                                    "The product is deleted",
                                    Toast.LENGTH_SHORT).show()
                                return true
                            }


                            else ->
                                return true
                        }
                    }
                })
                popup.show()

            }


        }


        override fun getItemCount(): Int {
            return products.size
        }


    }




}









