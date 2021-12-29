package com.example.manageyourinventoryapp

data class Invoice(var customerId: Int, var customerName: String, var productName: String, var quantity: Int, var category: String, var productid: String, var price: Double, var paymentDate: String){
    constructor(): this(0,"","",0,"","",0.0,"")
}
