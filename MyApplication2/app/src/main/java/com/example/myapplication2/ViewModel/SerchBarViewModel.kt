package com.example.myapplication2.ViewModel

import com.example.myapplication2.Data.AllUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SerchBarViewModel() {
    private val uiState = MutableStateFlow(UiState())
    private val _uiState = uiState.asStateFlow()
    private  val history = MutableStateFlow<List<AllUser>>()
    private  val _history = history.asStateFlow()

}

data class UiState(
    val query: String = "",
    val isQuerying: Boolean = false,
    val searchHistory: List<AllUser> = _history,
)