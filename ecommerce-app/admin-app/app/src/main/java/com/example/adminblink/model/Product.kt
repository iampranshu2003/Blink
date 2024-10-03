package com.example.adminblink.model

import java.util.UUID

data class Product(
    var productRandomID: String? = null,
    var productName: String? = null,
    var productQuantity: String? = null,
    var productUnit: String? = null,
    var productPrice: Int? = null,
    var productStock: Int? = null,
    var productCategory: String? = null,
    var productType: String? = null,
    var itemCount: Int? = null,
    var adminUID: String? = null,
    var productImageUris : ArrayList<String?>? = null


)
