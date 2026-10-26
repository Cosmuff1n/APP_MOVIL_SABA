package com.example.appsaba.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appsaba.ui.theme.AppSabaTheme

// ------------------------------------------------------------
// PANTALLA PRINCIPAL (P06 del Figma)
// ------------------------------------------------------------
@Composable
fun GuidesScreen() {
    // Column ordena todo hacia abajo; verticalScroll permite hacer scroll.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ----- Banner azul -----
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B6BB5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AYUDA EN TERRENO", color = Color.White, fontSize = 11.sp)
                Text(
                    "Centro de soporte y guías",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Procedimientos de primera respuesta para resolver fallas comunes.",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Text("CONSULTA RÁPIDA", color = Color.Gray, fontSize = 11.sp)
        Text("Procedimientos rápidos", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // ----- Las 4 guías (cada una se abre y se cierra al tocarla) -----
        GuideCard(
            title = "Reinicio de cajeros",
            subtitle = "Recuperación segura del terminal",
            step1 = "Verifica que el cajero no tenga una operación o pago en curso.",
            step2 = "Mantén presionado el botón de encendido durante cinco segundos.",
            step3 = "Espera treinta segundos y vuelve a encender el equipo.",
            step4 = "Comprueba la conexión y realiza una operación de prueba."
        )

        GuideCard(
            title = "Contingencia por barrera bloqueada",
            subtitle = "Libera el acceso y asegura el flujo",
            step1 = "Señaliza el acceso afectado y desvía los vehículos a otra vía.",
            step2 = "Verifica que no existan objetos bajo el brazo de la barrera.",
            step3 = "Activa el modo manual desde el control del acceso.",
            step4 = "Reporta la falla si la barrera no recupera el movimiento."
        )

        GuideCard(
            title = "Falla de terminal POS",
            subtitle = "Validación de energía y conexión",
            step1 = "Confirma que el terminal tenga carga o alimentación eléctrica.",
            step2 = "Revisa el cable de red o la señal móvil disponible.",
            step3 = "Reinicia el POS y espera a que complete la sincronización.",
            step4 = "Realiza una transacción de prueba de bajo monto."
        )

        GuideCard(
            title = "Problemas de lectura LPR",
            subtitle = "Revisión rápida de cámara y patente",
            step1 = "Limpia suavemente el lente y revisa que no esté obstruido.",
            step2 = "Comprueba que la cámara apunte al centro del carril.",
            step3 = "Verifica iluminación, reflejos y visibilidad de la patente.",
            step4 = "Prueba una nueva lectura y registra evidencia si la falla continúa."
        )

        Text("AYUDA ESPECIALIZADA", color = Color.Gray, fontSize = 11.sp)
        Text("Escalado a soporte", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        SupportCard()
    }
}

// ------------------------------------------------------------
// CARD DE UNA GUÍA
// Se escribe una vez y se usa 4 veces, cambiando solo los textos.
// ------------------------------------------------------------
@Composable
fun GuideCard(
    title: String,
    subtitle: String,
    step1: String,
    step2: String,
    step3: String,
    step4: String
) {
    // remember guarda el valor mientras la pantalla se redibuja.
    // false = cerrada, true = abierta.
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded } // al tocar, cambia abierta/cerrada
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }

                // Flecha arriba si está abierta, abajo si está cerrada
                if (expanded) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Cerrar")
                } else {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Abrir")
                }
            }

            // Este bloque solo se dibuja cuando expanded es true
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "PRIMERA RESPUESTA",
                    color = Color(0xFF0B6BB5),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text("1. $step1", fontSize = 13.sp)
                Text("2. $step2", fontSize = 13.sp)
                Text("3. $step3", fontSize = 13.sp)
                Text("4. $step4", fontSize = 13.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Si el problema continúa, escala el caso al equipo de soporte.",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ------------------------------------------------------------
// CARD DE SOPORTE (con botón de llamar)
// ------------------------------------------------------------
@Composable
fun SupportCard() {
    // El contexto se necesita para abrir otra app (el teléfono).
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text("Canal SAU", color = Color.Gray, fontSize = 11.sp)
            Text("Mesa de ayuda · Atención 24/7", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Correo de soporte", color = Color.Gray, fontSize = 11.sp)
            Text("soporte@saba.cl", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // ACTION_DIAL abre el marcador con el número escrito. No pide permisos.
                    // Cambia este número por el real del canal SAU.
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+56200000000"))
                    context.startActivity(intent)
                }
            ) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Llamar a soporte")
            }
        }
    }
}

// Preview: ver la pantalla en Android Studio sin ejecutar la app
@Preview(showBackground = true)
@Composable
fun GuidesScreenPreview() {
    AppSabaTheme {
        GuidesScreen()
    }
}
