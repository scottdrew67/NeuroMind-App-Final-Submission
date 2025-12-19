package com.example.neuromind

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


// retrofit singleton for risk backend
object RetrofitClient {
    // emulator on host machine
    // port 5000 is where python is running
    private const val BASE_URL = "http://10.0.2.2:5000/"

    // logs http request and response details
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    // shared okHttp client
    // long timeouts (speech upload and transcription can be slow)
    // added logginerInterceptor for debugging network calls
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            // long timeouts, changed whisper from medium to small to try make it a quicker process
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }


    // core retrofit builder, maps json/kotlin classes
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // for backend status connectivity
    val instance: RiskPredictionApi by lazy {
        retrofit.create(RiskPredictionApi::class.java)
    }

    // used for /upload-audio, /predict and /questionnaire
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
