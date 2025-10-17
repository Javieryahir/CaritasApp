package com.example.caritasapp.reservations

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

private val Accent = Color(0xFF009CA6)

@Composable
fun HealthFormsScreen(navController: NavController, count: Int) {
    val names         = remember(count) { MutableList(count) { mutableStateOf("") } }
    val ages          = remember(count) { MutableList(count) { mutableStateOf("") } }
    val allergiesList = remember(count) { MutableList(count) { mutableStateOf("") } }
    val disabilities  = remember(count) { MutableList(count) { mutableStateOf("") } }
    val medsList      = remember(count) { MutableList(count) { mutableStateOf("") } }

    val scroll = rememberScrollState()

    // Validación global
    val areNamesValid = names.all { it.value.isNotBlank() }
    val areAgesValid = ages.all { state ->
        val n = state.value.toIntOrNull()
            n != null && n in 1..120
    }
    val isFormValid = areNamesValid && areAgesValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Accent)
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
                        name = names[idx].value,
                        onName = { names[idx].value = it },
                        age = ages[idx].value,
                        onAge = { value -> if (value.all { ch -> ch.isDigit() } && value.length <= 3) ages[idx].value = value },
                        allergies = allergiesList[idx].value,
                        onAllergies = { allergiesList[idx].value = it },
                        disabilities = disabilities[idx].value,
                        onDisabilities = { disabilities[idx].value = it },
                        meds = medsList[idx].value,
                        onMeds = { medsList[idx].value = it }
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
                        tint = Accent,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Button(
                    onClick = { navController.navigate("confirm") },
                    shape = RoundedCornerShape(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Accent,
                        disabledContainerColor = Color.White.copy(alpha = 0.7f),
                        disabledContentColor = Accent.copy(alpha = 0.7f)
                    ),
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    modifier = Modifier
                        .height(84.dp)
                        .weight(1f)
                    ,
                    enabled = isFormValid
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Reservación",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 12.dp)
                    )
                    Text("Reservación", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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
    age: String,
    onAge: (String) -> Unit,
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
    CenteredField(
        value = name,
        onChange = onName,
        placeholder = "Nombre",
        isError = false
    )
    Spacer(Modifier.height(18.dp))

    SectionLabel("Edad")
    CenteredField(
        value = age,
        onChange = onAge,
        placeholder = "Edad",
        isError = age.isNotBlank() && (age.toIntOrNull()?.let { it in 1..120 } != true)
    )
    Spacer(Modifier.height(18.dp))

    SectionLabel("Listado de Alergias")
    NoneOrField(
        noneLabel = "Ninguna",
        value = allergies,
        onChange = onAllergies,
        placeholder = "Alergia"
    )
    Spacer(Modifier.height(18.dp))

    SectionLabel("Discapacidades")
    NoneOrField(
        noneLabel = "Ninguna",
        value = disabilities,
        onChange = onDisabilities,
        placeholder = "Discapacidad"
    )
    Spacer(Modifier.height(18.dp))

    SectionLabel("Medicamentos Actuales")
    NoneOrField(
        noneLabel = "Ninguno",
        value = meds,
        onChange = onMeds,
        placeholder = "Medicamento"
    )
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
    placeholder: String,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        isError = isError,
        enabled = enabled,
        placeholder = {
            Text(
                placeholder,
                fontSize = 22.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.6f),
            focusedBorderColor = Accent,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = Accent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun NoneOrField(
    noneLabel: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String
) {
    var noneChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Marca '" + noneLabel + "' si no aplica",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp
        )

        AssistChip(
            onClick = {
                noneChecked = !noneChecked
                if (noneChecked) onChange("")
            },
            label = { Text(noneLabel) },
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor = if (noneChecked) Accent else MaterialTheme.colorScheme.outline
            ),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (noneChecked) Color.White else MaterialTheme.colorScheme.surface,
                labelColor = if (noneChecked) Accent else MaterialTheme.colorScheme.onSurface
            )
        )
    }

    Spacer(Modifier.height(8.dp))

    CenteredField(
        value = if (noneChecked) "" else value,
        onChange = { if (!noneChecked) onChange(it) },
        placeholder = if (noneChecked) "No aplica" else placeholder,
        isError = false,
        enabled = !noneChecked
    )
}
