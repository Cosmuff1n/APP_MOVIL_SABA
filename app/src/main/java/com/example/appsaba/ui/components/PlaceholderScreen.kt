package com.example.appsaba.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Pantalla temporal reutilizable: un título centrado y, si quieres, un botón debajo.
// Se borra cuando cada pantalla real esté lista.
@Composable
fun PlaceholderScreen(
    title: String,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title)
        content()
    }
}
