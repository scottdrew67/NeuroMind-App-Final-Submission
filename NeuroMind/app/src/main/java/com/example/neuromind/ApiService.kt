package com.example.neuromind

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


// retrofit interface, defines endpoints
interface ApiService {

    // sends assessment data to backend, receives risk prediction
    // uses json request body
    // returns retrofit call for asynchronous execution with enqueue
    @POST("/predict")
    fun getPrediction(@Body request: PredictionRequest): Call<PredictResponse>


    // sends questionnaire data to backend
    @POST("/questionnaire")
    suspend fun createQuestionnaire(
        @Body data: Map<String, Any>
    ): Response<QuestionnaireIdResponse>


    //uploads audio file to backend for speech to text and analysis
    // returns transcript and summary
    @Multipart
    @POST("/upload-audio")
    fun uploadSpeech(
        @Part audio: MultipartBody.Part
    ): Call<SpeechUploadResponse>



    // response model returned by /upload audio
    // transcript - full text transcription from whisper
    // nullable, backend might fail to produce or return partial results
    data class SpeechUploadResponse(
        val transcript: String?,
        val summary: String?
    )
}
