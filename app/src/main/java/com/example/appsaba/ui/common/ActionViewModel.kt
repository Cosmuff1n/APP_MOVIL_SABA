package com.example.appsaba.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Mecanismo transversal de ejecución; no concentra lógica de ninguna funcionalidad. */
data class ActionStatus(val busy: Boolean = false, val feedback: String? = null)

abstract class ActionViewModel : ViewModel() {
    private val _status = MutableStateFlow(ActionStatus())
    protected val status: StateFlow<ActionStatus> = _status.asStateFlow()

    fun feedbackShown() { _status.value = _status.value.copy(feedback = null) }
    protected fun reject(message: String) {
        _status.value = _status.value.copy(feedback = message)
    }

    protected fun execute(message: String, operation: suspend () -> Unit) {
        if (_status.value.busy) return
        _status.value = _status.value.copy(busy = true, feedback = null)
        viewModelScope.launch {
            try {
                operation()
                _status.value = _status.value.copy(feedback = message)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _status.value = _status.value.copy(
                    feedback = "Operación no realizada: ${error.localizedMessage ?: "revisa los datos"}"
                )
            } finally {
                _status.value = _status.value.copy(busy = false)
            }
        }
    }
}
