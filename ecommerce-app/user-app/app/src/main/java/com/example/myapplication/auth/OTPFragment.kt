package com.example.myapplication.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.Utils
import com.example.myapplication.activity.UsersMainActivity
import com.example.myapplication.databinding.FragmentOTPBinding
import com.example.myapplication.models.Users
import com.example.myapplication.viewmodels.AuthViewModel
import kotlinx.coroutines.launch


class OTPFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding:FragmentOTPBinding
    private lateinit var userNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)
        getUserNumber()
        customizingEnteringOTP()
        onBackButtonClicked()
        sendingOTP()
        onLoginButtonClicked()
        onBackButtonClicked()
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.btnLogin.setOnClickListener {
            val editTexts = arrayOf(binding.etOTP1,binding.etOTP2,binding.etOTP3,binding.etOTP4,binding.etOTP5,binding.etOTP6)
            val otp = editTexts.joinToString("") { it.text.toString() }
            if (otp.length == 6){
                editTexts.forEach {
                    it.text?.clear()
                    it.clearFocus()
                }
                Utils.showDialog(requireContext(), "Verifying OTP")
                viewModel.apply {
                    verifyOTP(otp)
                }
            } else {
                Utils.showToast(requireContext(), "Enter valid OTP")
            }
        }
    }

    private fun verifyOTP(otp: String) {
        val user = Users(uid = Utils.getCurrentUserId(), userPhoneNumber = userNumber, userAddress = " ")
        viewModel.signInWithPhoneAuthCredential(otp, userNumber, user)
        lifecycleScope.launch {
            viewModel.signInSuccess.collect {
                if (it) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "OTP Verified")
                    startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                    requireActivity().finish()
                }
                else {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Invalid OTP")
                }
            }
        }


    }

    private fun sendingOTP() {
        Utils.showDialog(requireContext(), "Sending OTP")
        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                otpSent.collect {
                    if (it) {
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), "OTP Sent")

                    }
                }

            }
        }

    }

    private fun onBackButtonClicked() {
        binding.tbOTPFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
        }

    }

    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(binding.etOTP1,binding.etOTP2,binding.etOTP3,binding.etOTP4,binding.etOTP5,binding.etOTP6)
        for (i in editTexts.indices){
            editTexts[i].addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0?.length == 1){
                        if (i<editTexts.size-1){
                            editTexts[i+1].requestFocus()
                        }
                    } else if (p0?.length == 0){
                        if(i>0){
                            editTexts[i-1].requestFocus()
                        }
                    }
                }

            })

        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()

        binding.tvPhoneNumber.text = userNumber

    }


}