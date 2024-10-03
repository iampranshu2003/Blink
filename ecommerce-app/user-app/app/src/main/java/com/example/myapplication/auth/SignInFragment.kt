package com.example.myapplication.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.Utils
import com.example.myapplication.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        //changing colour of status bar
        setStatusBars()
        setUserNumber()
        onContinueClick()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun onContinueClick() {
        binding.btnContinue.setOnClickListener {
            val number = binding.etPhoneNumber.text.toString()
            if (number.isEmpty() || number.length != 10) {
                Utils.showToast(requireContext(), "Enter a valid number")

            }else{
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment, bundle)
            }

        }
    }

    private fun setUserNumber() {
        binding.etPhoneNumber.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(number: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val len = number?.length

                if (len == 10) {
                    binding.btnContinue.isEnabled = true
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    )
                } else {
                    binding.btnContinue.isEnabled = false
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey
                        )
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


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