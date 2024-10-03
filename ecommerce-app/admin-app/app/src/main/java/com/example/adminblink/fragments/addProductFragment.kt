package com.example.adminblink.fragments

import AdminViewModel
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.adminblink.AdminMainActivity
import com.example.adminblink.Constants
import com.example.adminblink.R
import com.example.adminblink.Utils
import com.example.adminblink.adapter.AdapterSelectedImage
import com.example.adminblink.databinding.FragmentAddProductBinding
import com.example.adminblink.model.Product
import kotlinx.coroutines.launch


class addProductFragment : Fragment() {

    private lateinit var binding: FragmentAddProductBinding
    private val ViewModel: AdminViewModel by viewModels()
    private var imageUri: ArrayList<Uri> = arrayListOf()
    val selectedImage = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listofUri->
        val fiveImages = listofUri.take(5)
        imageUri.clear()
        imageUri.addAll(fiveImages)

        binding.rvProductImages.adapter = AdapterSelectedImage(imageUri)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddProductBinding.inflate(layoutInflater)
        setStatusBars()
        setAutoCompleteTextView()
        onImageSelectClicked()
        onAddButtonClicked()

        return binding.root
    }

    private fun onAddButtonClicked() {
        binding.btnAddProduct.setOnClickListener {
            Utils.showDialog(requireContext(), "Adding Product....")
            val productTitle = binding.etProductName.text.toString()
            val productQuantity = binding.etProductQuantity.text.toString()
            val productUnit = binding.etProductUnit.text.toString()
            val productPrice = binding.etProductPrice.text.toString()
            val productStock = binding.etProductStock.text.toString()
            val productCategory = binding.etProductCategory.text.toString()
            val productType = binding.etProductType.text.toString()




            if (productTitle.isEmpty() || productCategory.isEmpty() || productType.isEmpty() || productQuantity.isEmpty() || productPrice.isEmpty() || productUnit.isEmpty()) {
                Utils.hideDialog()
                Utils.showDialog(requireContext(), "Please fill all the fields")
            } else if (imageUri.isEmpty()) {
                Utils.hideDialog()
                Utils.showDialog(requireContext(), "Please select an image")
            } else {
                val product = Product(
                    productName = productTitle,
                    productCategory = productCategory,
                    productType = productType,
                    productQuantity = productQuantity.toString(),
                    productPrice = productPrice.toInt(),
                    productUnit = productUnit,
                    productStock = productStock.toInt(),
                    itemCount = 0,
                    adminUID = Utils.getCurrentUserId(),
                    productRandomID = Utils.getRandomId()
                )
                saveImage(product)

            }
        }
    }

    private fun saveImage(product: Product) {
        ViewModel.saveImageInDB(imageUri)
        lifecycleScope.launch{
            ViewModel.isImagesUploaded.collect {
                if (it) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "image saved")
                    getUrls(product)
                }
            }

        }


    }

    private fun getUrls(product: Product) {

        Utils.showDialog(requireContext(), "Publishing Product....")
        lifecycleScope.launch {
            ViewModel.downloadUrls.collect {
                val urls = it
                product.productImageUris= urls
                saveProduct(product)

            }
        }

    }

    private fun saveProduct(product: Product) {
        ViewModel.saveProduct(product)
        lifecycleScope.launch {
            ViewModel.isImagesUploaded.collect {
                if (it) {
                    Utils.hideDialog()
                    startActivity(
                        Intent(requireContext(), AdminMainActivity::class.java))
                    Utils.showToast(requireContext(), "Product added successfully")
                }

        }


    }
    }

    private fun onImageSelectClicked() {
            binding.btnSelectImage.setOnClickListener {
                selectedImage.launch("image/*")
            }
        }

        private fun setAutoCompleteTextView() {
            val units =
                ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsofProducts)
            val category =
                ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductCategory)
            val productType =
                ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductType)
            binding.apply {
                etProductQuantity.setAdapter(units)
                etProductCategory.setAdapter(category)
                etProductType.setAdapter(productType)
            }

        }

        private fun setStatusBars() {
            activity?.window?.apply {
                val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
                statusBarColor = statusBarColors
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }



}