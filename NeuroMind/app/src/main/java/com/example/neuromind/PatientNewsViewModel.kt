package com.example.neuromind

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


// holds ui state for patient news screen, and manages fetching of data by patientNewsRepository

// gets StateFlows for loading/error/success state, and the article list
// also runs network calls in viewModelScope
class PatientNewsViewModel : ViewModel() {

    // wraps retrofit calls and returns result
    private val repository = PatientNewsRepository()


    // ui state used to decide what to render
    sealed class NewsState {
        object Idle : NewsState() // not requested yet
        object Loading : NewsState() // show spinner
        object Success : NewsState()// show list
        data class Error(val message: String) : NewsState() // show message
    }

    // backing mutable state and read only public state
    private val _newsState = MutableStateFlow<NewsState>(NewsState.Idle)
    val newsState: StateFlow<NewsState> = _newsState

    private val _articles = MutableStateFlow<List<PatientArticle>>(emptyList())
    val articles: StateFlow<List<PatientArticle>> = _articles

    // loads news from backend
    // daysBack: how many days back to search
    // refresh: if true, forces backend to fetch fresh articles
    fun loadNews(daysBack: Int = 7, refresh: Boolean = false) {
        viewModelScope.launch {
            _newsState.value = NewsState.Loading

            val result = repository.getPatientNews(daysBack, refresh)

            result.fold(
                onSuccess = { response ->
                    _articles.value = response.articles
                    _newsState.value = NewsState.Success
                },
                onFailure = { error ->
                    _newsState.value = NewsState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    // used by refresh icon in top bar, re runs loadNews with refresh=true
    fun refreshNews(daysBack: Int = 7) {
        loadNews(daysBack = daysBack, refresh = true)
    }
}
