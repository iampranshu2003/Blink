package com.example.myapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.myapplication.Cartlistener
import com.example.myapplication.R
import com.example.myapplication.adapters.AdapterCartProducts
import com.example.myapplication.databinding.ActivityUsersMainBinding
import com.example.myapplication.databinding.BsCartProductsBinding
import com.example.myapplication.roomdb.CartProductTable
import com.example.myapplication.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class UsersMainActivity : AppCompatActivity(), Cartlistener {
    private lateinit var binding: ActivityUsersMainBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var cartProductList : List<CartProductTable>
    private lateinit var adapterCartProducts: AdapterCartProducts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()

        getTotalCartItemCount()

        onCartClicked()

        onNextButtonClicked()

    }

    private fun onNextButtonClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }

    private fun getAllCartProducts(){
        val viewModel: UserViewModel by viewModels()
        viewModel.getAll().observe(this){

                cartProductList = it


        }
    }

    private fun onCartClicked() {
        binding.llcart.setOnClickListener {
            val bsCartProductsBinding = BsCartProductsBinding.inflate(LayoutInflater.from(this))

            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductsBinding.root)

            bsCartProductsBinding.tvProductCount.text = binding.tvProductCount.text

            adapterCartProducts = AdapterCartProducts()
            bsCartProductsBinding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            bs.show()

        }

    }

    private fun getTotalCartItemCount() {
        viewModel.fetchTotolCartItemCount().observe(this){
            if (it>0){
                binding.cart.visibility = View.VISIBLE
                binding.tvProductCount.text = it.toString()
            }
            else{
                binding.cart.visibility = View.GONE
            }
        }
        }

    override fun showCartLayout(itemCount: Int) {
        val previousCount = binding.tvProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if (updatedCount > 0) {
            binding.cart.visibility = View.VISIBLE
            binding.tvProductCount.text = updatedCount.toString()

        } else {
            binding.cart.visibility = View.GONE
            binding.tvProductCount.text = "0"


        }
    }

    override fun savingCartItemCount(itemCount: Int) {

        viewModel.fetchTotolCartItemCount().observe(this){
            viewModel.savingCartItemCount(it + itemCount)
        }


    }
}