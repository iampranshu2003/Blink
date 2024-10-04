package com.example.myapplication.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.adapters.AdapterCartProducts
import com.example.myapplication.databinding.ActivityOrderPlaceBinding
import com.example.myapplication.viewmodels.UserViewModel

class OrderPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderPlaceBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBars()
        backToUserMainActivity()

        getAllCartProducts()
    }

    private fun backToUserMainActivity() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }

    private fun getAllCartProducts() {
        val viewModel: UserViewModel by viewModels()
        viewModel.getAll().observe(this) { cartProductList ->
            adapterCartProducts = AdapterCartProducts()

            binding.rvCartProducts.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            var totalPrice = 0
            for (products in cartProductList) {
                var price = products.productPrice?.substring(1)?.toInt()
                var itemCount = products.productCount!!

                totalPrice += (price?.times(itemCount)!!)

            }

            binding.tvSubTotal.text = totalPrice.toString()

            if (totalPrice < 200) {
                binding.tvDeliveryCharge.text = "₹15"
                totalPrice += 15

            }

            binding.tvGrandTotal.text = totalPrice.toString()
        }
    }
    //changing colour of status bar2
    private fun setStatusBars(){
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderPlaceActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}