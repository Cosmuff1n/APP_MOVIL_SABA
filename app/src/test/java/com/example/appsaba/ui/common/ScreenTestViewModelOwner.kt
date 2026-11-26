package com.example.appsaba.ui.common

import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras

/** Owner exclusivo para pruebas; nunca se utiliza en producción. */
internal class ScreenTestViewModelOwner(vararg instances: ViewModel) :
    ViewModelStoreOwner, HasDefaultViewModelProviderFactory {

    private val viewModels = instances.toList()
    override val viewModelStore: ViewModelStore = ViewModelStore()
    override val defaultViewModelCreationExtras: CreationExtras = CreationExtras.Empty
    override val defaultViewModelProviderFactory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                viewModels.firstOrNull { modelClass.isInstance(it) } as? T
                    ?: error("No se ha registrado ViewModel ${modelClass.name} en el test")

            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                create(modelClass)
        }
}
