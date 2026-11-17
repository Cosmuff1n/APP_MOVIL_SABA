package com.example.appsaba.ui.guides

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class GuidesViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(GuidesUiState())
    val uiState = _uiState.asStateFlow()

    fun toggle(title: String) = _uiState.update { current ->
        current.copy(expandedGuides = if (title in current.expandedGuides)
            current.expandedGuides - title else current.expandedGuides + title)
    }
}
