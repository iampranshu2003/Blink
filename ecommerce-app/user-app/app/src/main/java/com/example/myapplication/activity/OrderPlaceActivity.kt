package com.example.myapplication.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.Utils
import com.example.myapplication.adapters.AdapterCartProducts
import com.example.myapplication.databinding.ActivityOrderPlaceBinding
import com.example.myapplication.databinding.AddressLayoutBinding
import com.example.myapplication.viewmodels.UserViewModel
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import kotlinx.coroutines.launch

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
        intializePhonePay()
        getAllCartProducts()
        onPlacedOrderClicked()
    }

    private fun intializePhonePay() {
        PhonePe.init(this,PhonePeEnvironment.SANDBOX, String merchantId, String appId)

    }

    private fun onPlacedOrderClicked() {
        binding.btnNext.setOnClickListener{
            viewModel.getAddressStatus().observe(this) { addressStatus ->
                if (addressStatus) {

                } else {
                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(alertDialog, addressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialog(this,"processing...")
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etDiscriptiveAddress.text.toString()

        val address = "$userPinCode, $userDistrict($userState), $userAddress, $userPhoneNumber"



        lifecycleScope.launch {
            viewModel.saveUserAddress(  address)
            viewModel.saveAddressStatus()
        }
        alertDialog.dismiss()
        Utils.hideDialog()

        Utils.showToast(this, "Saved..")
        alertDialog.dismiss()
        Utils.hideDialog()

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