package com.example.neuromind

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


// singleton used for patient news backend
object PatientNewsRetrofit {

    // Emulator -> laptop localhost
    // use 10.0.2.2 and port 8000 for FastAPI
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // For physical device on same WiFi, replace with pc ip
    // private const val BASE_URL = "http://192.168.1.5:8000/"


    //
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()

            // how long to wait to establish connection
            .connectTimeout(30, TimeUnit.SECONDS)

            // how long to wait for server to respond
            // news pipeline can be slow, set to 3 minute timer
            .readTimeout(180, TimeUnit.SECONDS)

            // how long to wait when sending request data
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }



    // retrofit api used by repository/ViewModel
    // maps json and kotlin classes
    val api: PatientNewsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PatientNewsApi::class.java)
    }
}
