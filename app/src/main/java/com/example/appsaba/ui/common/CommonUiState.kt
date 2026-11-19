package com.example.appsaba.ui.common

/** Estado transversal mínimo de operaciones; cada feature tiene su propio UiState. */
data class CommonUiState(val busy: Boolean = false, val feedback: String? = null)
