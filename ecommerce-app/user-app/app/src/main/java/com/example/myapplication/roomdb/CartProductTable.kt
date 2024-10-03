package com.example.myapplication.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CartProductTable")
data class CartProductTable(


    //primary
    @PrimaryKey
    var productID: String = "random",


    var productName: String? = null,
    var productQuantity: String? = null,
    var productPrice: String? = null,
    var productCount: Int? = null,
    var productStock: Int? = null,
    var productImage: String? = null,
    var productCategory: String? = null,
    var adminUID: String? = null,
)