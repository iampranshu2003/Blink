package com.example.myapplication.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.Constants
import com.example.myapplication.R
import com.example.myapplication.Utils
import com.example.myapplication.adapters.AdapterCartProducts
import com.example.myapplication.databinding.ActivityOrderPlaceBinding
import com.example.myapplication.databinding.AddressLayoutBinding
import com.example.myapplication.viewmodels.UserViewModel
import com.phonepe.intent.sdk.api.B2BPGRequest
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


class OrderPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderPlaceBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private lateinit var b2BPGRequest: B2BPGRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBars()
        backToUserMainActivity()
        getAllCartProducts()
        intializePhonePay()
        onPlacedOrderClicked()
    }

    private fun intializePhonePay() {
        val data = JSONObject()
        PhonePe.init(this, PhonePeEnvironment.SANDBOX, Constants.MERCHANTID, "")

        // Adding log messages
        Log.d("OrderPlaceActivity", "Initializing PhonePe with merchant ID: ${Constants.MERCHANTID}")

        data.put("merchantId", Constants.MERCHANTID)
        data.put("merchantTransactionId", Constants.merchantTransactionId)
        data.put("amount", 200)
        data.put("mobileNumber", "9999999999")
        data.put("callbackUrl", "https://webhook.site/callback-url")

        Log.d("OrderPlaceActivity", "Payment data: $data")

        val paymentInstrument = JSONObject()
        paymentInstrument.put("type", "UPI_INTENT")
        paymentInstrument.put("targetApp", "com.phonepe.simulator")

        data.put("paymentInstrument", paymentInstrument)

        val deviceContext = JSONObject()
        deviceContext.put("deviceOS", "ANDROID")
        data.put("deviceContext", deviceContext)

        // Log data before encoding
        Log.d("OrderPlaceActivity", "Complete data before encoding: $data")

        val payloadBase64 = Base64.encodeToString(
            data.toString().toByteArray(Charset.defaultCharset()),
            Base64.NO_WRAP
        )

        Log.d("OrderPlaceActivity", "Base64 Encoded Payload: $payloadBase64")

        // Generating checksum and adding logs to validate it
        val checksum = sha256(payloadBase64 + Constants.apiEndPoint + Constants.SALT_KEY) + "###1"
        Log.d("OrderPlaceActivity", "Generated Checksum: $checksum")

        b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checksum)
            .setUrl(Constants.apiEndPoint)
            .build()
    }

    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold(""){ str, it -> str + "%02x".format(it) }

    }

    private fun onPlacedOrderClicked() {
        binding.btnNext.setOnClickListener {
            Log.d("OrderPlaceActivity", "Place Order button clicked")

            viewModel.getAddressStatus().observe(this) { addressStatus ->
                Log.d("OrderPlaceActivity", "Address status: $addressStatus")

                if (addressStatus) {
                    getPaymentView()
                } else {
                    Log.d("OrderPlaceActivity", "Address not found, showing address dialog")
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


    val phonePayView = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.d("OrderPlaceActivity", "PhonePe activity result received, result code: ${it.resultCode}")

        if (it.resultCode == RESULT_OK) {
            Log.d("OrderPlaceActivity", "Payment success, checking status")
            checkStatus()
            Utils.showToast(this, "Payment Success")
        } else {
            Log.e("OrderPlaceActivity", "Payment failed")
            Utils.showToast(this, "Payment Failed")
        }
    }

    private fun checkStatus() {
        val xVerify = sha256("/pg/v1/status/${Constants.MERCHANTID}/${Constants.merchantTransactionId}" + Constants.SALT_KEY) + "###1"

        Log.d("OrderPlaceActivity", "Checking payment status with X-Verify: $xVerify")

        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to xVerify,
            "X-MERCHANT-ID" to Constants.MERCHANTID
        )

        Log.d("OrderPlaceActivity", "Headers: $headers")

        lifecycleScope.launch {
            viewModel.checkPayment(headers)
            viewModel.paymentStatus.collect { status ->
                Log.d("OrderPlaceActivity", "Payment status received: $status")
                if (status) {
                    Utils.showToast(this@OrderPlaceActivity, "Payment Success")
                    startActivity(Intent(this@OrderPlaceActivity, UsersMainActivity::class.java))
                    finish()
                } else {
                    Utils.showToast(this@OrderPlaceActivity, "Payment Failed")
                }
            }
        }
    }

    private fun getPaymentView() {
        try {
            Log.d("OrderPlaceActivity", "Launching PhonePe payment view")
            PhonePe.getImplicitIntent(this, b2BPGRequest, "com.phonepe.simulator").let {
                phonePayView.launch(it)
            }
        } catch (e: Exception) {
            Log.e("OrderPlaceActivity", "Error launching PhonePe payment: ${e.message}")
            Utils.showToast(this, e.message.toString())
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