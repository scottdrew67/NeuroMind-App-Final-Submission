package com.example.neuromind


// model for a single news article returned by patient news
// actionable_tips is set to emptylist to avoid crashes when backend returned null or missing fields
// some fields set as optional, backend didnt always provide them
data class PatientArticle(
        val title: String,
        val source: String,
        val url: String,
        val date: String,
        val description: String,
        val summary: String,
        val relevance_score: Int? = null,
        val category: String? = null,
        val readability_score: Int? = null,
        val actionable_tips: List<String> = emptyList() // was causing crashes with backend.py, set to empty list
)


// response returned when app requests latest news
data class PatientNewsResponse(
        val type: String,
        val timestamp: String,
        val articles: List<PatientArticle>, // list shown on screen
        val total_articles: Int? = null,
        val categories: Map<String, Int>?// breakdown by category (optional)
)


// returned by a refresh endpoint, forces backend to fetch new articles
data class RefreshResponse(
        val message: String,
        val timestamp: String,
        val articles_found: Int,
        val categories: Map<String, Int>?
)


// checks if news service is responding
data class HealthResponse(
        val status: String,
        val timestamp: String
)
