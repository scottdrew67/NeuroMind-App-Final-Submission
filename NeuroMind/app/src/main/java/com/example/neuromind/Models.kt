package com.example.neuromind



// request body thats sent to POST /predict
data class PredictionRequest(
    val age: Double,
    val sleep_hours: Double,
    val memory_score: Double,
    val speech_text: String
)


// response returned from POST /predict
// prediction: "high_risk" or "low_risk"
// probabilities: used to compute the ui score
data class PredictResponse(
    val prediction: String,
    val probability: Probability
)


// probability values returned by backend
// ui now uses probability.high_risk to build the score
data class Probability(
    val low_risk: Double,
    val high_risk: Double
)


// health check, used by /status endpoint
// checks if api is reachable and responding
data class ApiStatus(
    val status: String,
    val message: String
)


// returned with POST /questionnaire
data class QuestionnaireIdResponse(
    val id: String
)

