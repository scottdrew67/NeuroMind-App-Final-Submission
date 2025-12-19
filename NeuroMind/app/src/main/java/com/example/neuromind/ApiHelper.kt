package com.example.neuromind

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

// using retrofit, makes network calls to backend
// object is a singleton, makes it easier to call across the app
object ApiHelper {

    /**
     * Make a dementia risk prediction
     *
     * @param age Patients age (e.g. 68.0)
     * @param sleepHours Hours of sleep per night (e.g. 6.5)
     * @param memoryScore Memory test score 0-100 (e.g., 72.0)
     * @param speechText Patients speech sample
     * @param onSuccess Callback when prediction succeeds
     * @param onError Callback when request fails
     */
    fun predictDementiaRisk(
        age: Double,
        sleepHours: Double,
        memoryScore: Double,
        speechText: String,
        onSuccess: (Int) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        // build json request body that the backend expects
        val request = PredictionRequest(
            age = age,
            sleep_hours = sleepHours,
            memory_score = memoryScore,
            speech_text = speechText
        )

        // debug logs
        Log.d("ApiHelper", "Making prediction request...")
        Log.d("ApiHelper", "Age: $age, Sleep: $sleepHours, Memory: $memoryScore")

        // make asynchronous request through retrofit, result comes back in onResponse/onFailure
        RetrofitClient.apiService.getPrediction(request).enqueue(object : Callback<PredictResponse> {

            // called when server responds (even when error 400/500)
            override fun onResponse(
                call: Call<PredictResponse>,
                response: Response<PredictResponse>
            ) {
                // http 200-299
                if (response.isSuccessful) {
                    // parse json -> predictResponse
                    val body = response.body()
                    if (body != null) {
                        // convert backend probability value into int score for the UI
                        // high_risk = 73.8 -> shows as 74 in ui
                        val score = body.probability.high_risk.roundToInt()
                        Log.d("ApiHelper", "✓ Prediction successful: ${body.prediction}, score=$score")

                        // send score back to screen
                        onSuccess(score)
                    }
                    else {
                        // successful http response, but invalid json
                        val error = "Empty response body"
                        Log.e("ApiHelper", "✗ $error")
                        onError?.invoke(error)
                    }
                } else {
                    // http error response, 400/500
                    // try to read servers error message
                    val errBody = response.errorBody()?.string()
                    val error = "Server error: ${response.code()} ${errBody ?: ""}".trim()
                    Log.e("ApiHelper", "✗ $error")
                    onError?.invoke(error)
                }
            }

            // used when request never reached server due to internet issue or timeout
            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                t.printStackTrace()

                // build error message
                val error = "Connection failed: ${t.localizedMessage ?: t.message ?: "unknown"}"
                Log.e("ApiHelper", "✗ $error")
                onError?.invoke(error)
            }
        })
    }


    // test function, used to verify if connected to backend
    fun testConnection(
        onSuccess: () -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        // debug log
        Log.d("ApiHelper", "Testing API connection...")

        // calls backend health endpoint
        RetrofitClient.instance.getStatus().enqueue(object : Callback<ApiStatus> {
            // server responded
            override fun onResponse(call: Call<ApiStatus>, response: Response<ApiStatus>) {
                if (response.isSuccessful) {
                    Log.d("ApiHelper", "✓ API is reachable")
                    onSuccess()
                } else {
                    val error = "API returned error: ${response.code()}"
                    Log.e("ApiHelper", "✗ $error")
                    onError?.invoke(error)
                }
            }
            // server couldnt be reached
            override fun onFailure(call: Call<ApiStatus>, t: Throwable) {
                val error = "Cannot reach API: ${t.message}"
                Log.e("ApiHelper", "✗ $error")
                onError?.invoke(error)
            }
        })
    }
}