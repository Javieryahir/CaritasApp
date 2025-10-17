package com.example.caritasapp.reserve

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.composables.HyperlinkText

private val Teal = Color(0xFF5D97A3)

@Composable
fun HealthFormsScreen(navController: NavController, count: Int) {
    val names         = remember(count) { MutableList(count) { "" } }
    val allergiesList = remember(count) { MutableList(count) { "" } }
    val disabilities  = remember(count) { MutableList(count) { "" } }
    val medsList      = remember(count) { MutableList(count) { "" } }

    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(horizontal = 28.dp, vertical = 20.dp)
            .imePadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            // Área scrolleable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "FORMULARIO DE SALUD",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                // ===== línea blanca debajo del título =====
                WhiteDivider()
                Spacer(Modifier.height(12.dp))

                repeat(count) { idx ->
                    // ===== separador entre formularios (no antes del primero) =====
                    if (idx > 0) {
                        Spacer(Modifier.height(12.dp))
                        WhiteDivider()
                        Spacer(Modifier.height(12.dp))
                    }

                    PersonFormCard(
                        index = idx,
                        total = count,
                        name = names[idx],
                        onName = { names[idx] = it },
                        allergies = allergiesList[idx],
                        onAllergies = { allergiesList[idx] = it },
                        disabilities = disabilities[idx],
                        onDisabilities = { disabilities[idx] = it },
                        meds = medsList[idx],
                        onMeds = { medsList[idx] = it }
                    )
                }

                Spacer(Modifier.height(18.dp))

                HyperlinkText(
                    fullText = "Política de privacidad",
                    hyperlinks = mapOf(
                        "Política de privacidad" to
                                "https://tecmx-my.sharepoint.com/:b:/r/personal/a01782862_tec_mx/Documents/AppMovil/PoliticasdePrivacidad/AVISO%20DE%20PRIVACIDAD%202025.pdf?csf=1&web=1&e=Tum1V9"
                    ),
                    fontSize = 22.sp,
                    textColor = Color(0xFFE7E7E7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))
            }

            // Botones fijos abajo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp)),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = { navController.popBackStack() },
                    shape = CircleShape,
                    modifier = Modifier.size(84.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Teal,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Button(
                    onClick = { navController.navigate("confirm") },
                    shape = RoundedCornerShape(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Teal
                    ),
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    modifier = Modifier
                        .height(84.dp)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Reservación",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 12.dp)
                    )
                    Text("Reservación", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/* ---------- Línea blanca reutilizable ---------- */
@Composable
private fun WhiteDivider() {
    // No toca bordes: respeta 8.dp de “aire” extra sobre el padding de pantalla
    HorizontalDivider(
        color = Color.White.copy(alpha = 0.85f),
        thickness = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(2.dp))
    )
}

/* ---------- UI de un formulario por persona ---------- */

@Composable
private fun PersonFormCard(
    index: Int,
    total: Int,
    name: String,
    onName: (String) -> Unit,
    allergies: String,
    onAllergies: (String) -> Unit,
    disabilities: String,
    onDisabilities: (String) -> Unit,
    meds: String,
    onMeds: (String) -> Unit
) {
    Text(
        text = "Persona ${index + 1} de $total",
        color = Color.White,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    SectionLabel("Nombre")
    CenteredField(value = name, onChange = onName, placeholder = "Nombre")
    Spacer(Modifier.height(18.dp))

    SectionLabel("Listado de Alergias")
    CenteredField(value = allergies, onChange = onAllergies, placeholder = "Alergia")
    Spacer(Modifier.height(18.dp))

    SectionLabel("Discapacidades")
    CenteredField(value = disabilities, onChange = onDisabilities, placeholder = "Discapacidad")
    Spacer(Modifier.height(18.dp))

    SectionLabel("Medicamentos Actuales")
    CenteredField(value = meds, onChange = onMeds, placeholder = "Medicamento")
}

/* ---------- Helpers existentes ---------- */

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        color = Color.White,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CenteredField(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        placeholder = {
            Text(
                placeholder,
                fontSize = 22.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        textStyle = LocalTextStyle.current.copy(
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            cursorColor = Teal
        )
    )
}
