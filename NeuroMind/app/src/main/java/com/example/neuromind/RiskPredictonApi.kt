package com.example.neuromind

import retrofit2.Call
import retrofit2.http.GET


// used for basic health checks against risk backend
interface RiskPredictionApi {
    // returns retrofit call so it can be executed with enqueue
    @GET("/")
    fun getStatus(): Call<ApiStatus>
}
