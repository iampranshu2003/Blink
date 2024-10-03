package com.example.myapplication.auth

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.Constants
import com.example.myapplication.R
import com.example.myapplication.adapters.AdapterCategory
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.models.Category
import com.example.myapplication.viewmodels.UserViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBars()
        setAllCategories()
        navigatingToSearchFragment()
        get()

        return binding.root
    }

    private fun get(){
        val viewModel: UserViewModel by viewModels()
        viewModel.getAll().observe(viewLifecycleOwner){
            for (i in it){
                Log.d("vvv", i.productName.toString())
                Log.d("vvv", i.productCount.toString())

            }

        }
    }

    private fun navigatingToSearchFragment() {
        binding.searchCv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

    }

    private fun setAllCategories() {
        val allProductCategory = Constants.allProductCategory
        val allProductCategoryImages = Constants.allProductCategoryImages

        val categoryList = ArrayList<Category>()
        for (i in allProductCategory.indices) {
            val category = Category(allProductCategory[i], allProductCategoryImages[i])
            categoryList.add(category)
        }
        binding.categoryRv.adapter = AdapterCategory(categoryList, ::onCategoryIconClicked)
    }

    fun onCategoryIconClicked(category: Category) {

        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)

    }


    //changing colour of status bar2
    private fun setStatusBars(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}