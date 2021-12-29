package com.example.manageyourinventoryapp

data class Product(var image: String, var id: String, var name: String, var price: Double, var category: String, var amount: Int){
    constructor(): this("","","",0.0,"",0)
}