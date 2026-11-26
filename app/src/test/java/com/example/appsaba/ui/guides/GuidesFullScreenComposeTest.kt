package com.example.appsaba.ui.guides

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.appsaba.ui.common.ScreenTestViewModelOwner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GuidesFullScreenComposeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun pantallaMuestraGuiasYPermiteExpandir() {
        val owner = ScreenTestViewModelOwner(GuidesViewModel())
        compose.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) { GuidesScreen() }
        }
        compose.onNodeWithText("Centro de soporte y guías").assertExists()
        compose.onNodeWithText("Reinicio de cajeros").performClick()
        compose.onNodeWithText("1. Verifica que el cajero no tenga una operación o pago en curso.").assertExists()
    }
}
