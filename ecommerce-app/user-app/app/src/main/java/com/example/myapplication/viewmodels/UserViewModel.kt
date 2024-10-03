package com.example.myapplication.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.models.Product
import com.example.myapplication.roomdb.CartProductDao
import com.example.myapplication.roomdb.CartProductTable
import com.example.myapplication.roomdb.CartProductsDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application) : AndroidViewModel(application) {

    //initialization
    val sharedPreferences: SharedPreferences = application.getSharedPreferences("MyPref", MODE_PRIVATE)
    val cartProductDao: CartProductDao = CartProductsDatabase.getDatabaseInstance(application).cartProductsDao()

    //room database
    suspend fun insertCartProduct(products: CartProductTable) {
        cartProductDao.insertCartProduct(products)
    }

    fun getAll(): LiveData<List<CartProductTable>> {
        return cartProductDao.getAllCartProducts()
    }

    suspend fun updateCartProduct(products: CartProductTable) {
        cartProductDao.updateCartProduct(products)
    }

    suspend fun deleteCartProduct(productId: String) {
        cartProductDao.deleteCartProduct(productId)
    }


    //firebase
    fun fetchAllProducts(): Flow<List<Product>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)



                }
                trySend(products)

            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }

        }
        db.addValueEventListener(eventListener)

        awaitClose{
            db.removeEventListener(eventListener)
        }
    }
    fun getCategoryProducts(category: String?) : Flow<List<Product>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${category}")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)



                }
                trySend(products)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)

        awaitClose{
            db.removeEventListener(eventListener)
        }

    }

    fun updateItemCount(product : Product, itemCount: Int){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomID}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productRandomID}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productRandomID}").child("itemCount").setValue(itemCount)
    }
    //shared preferences
    fun savingCartItemCount(itemCount: Int){
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }
    fun fetchTotolCartItemCount(): MutableLiveData<Int> {
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreferences.getInt("itemCount", 0)
        return totalItemCount
    }
}