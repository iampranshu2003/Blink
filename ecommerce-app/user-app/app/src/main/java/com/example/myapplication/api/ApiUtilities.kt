package com.example.myapplication.api

object ApiUtilities {
    val statusAPI: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl("https")
    }

}