package com.example.caritasapp.transport

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.caritasapp.navegationbar.AppBottomBar
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar
import android.net.Uri

private val Teal = Color(0xFF5D97A3)
private val CardBg = Color(0xFFD1E0D7)
private val CardStroke = Color(0x33000000)

private enum class Mode { GoTo, PickUp } // Ir a / Recoger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(navController: NavController) {
    val context = LocalContext.current
    val cal = remember { Calendar.getInstance() }

    // ------------- Leemos el albergue elegido en ReservationPage (ruta "search") -------------
    val searchHandle = remember(navController) {
        runCatching { navController.getBackStackEntry("search").savedStateHandle }.getOrNull()
    }
    val shelterName by (searchHandle?.getStateFlow("shelter_name", "") ?: MutableStateFlow("")).collectAsState("")

    // ------------- Estado del formulario -------------
    var mode by remember { mutableStateOf(Mode.GoTo) } // Ir a por defecto

    var pickup by remember { mutableStateOf("") }
    var dropoff by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    fun pickDate() {
        DatePickerDialog(
            context,
            { _, y, m, d -> date = "$d/${m + 1}/$y" },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    fun pickTime() {
        TimePickerDialog(
            context,
            { _, h, min -> time = "%02d:%02d".format(h, min) },
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show()
    }

    // ------------- Lógica para inyectar el albergue según modo -------------
    val pickupValue: String
    val pickupReadOnly: Boolean
    val dropoffValue: String
    val dropoffReadOnly: Boolean

    if (mode == Mode.GoTo) {
        // “Ir a” -> el albergue va en LUGAR DE RECOGIDA (no editable)
        pickupValue = shelterName.ifBlank { pickup }
        pickupReadOnly = shelterName.isNotBlank()
        dropoffValue = dropoff
        dropoffReadOnly = false
    } else {
        // “Recoger” -> el albergue va en LOCACIÓN FINAL (no editable)
        pickupValue = pickup
        pickupReadOnly = false
        dropoffValue = shelterName.ifBlank { dropoff }
        dropoffReadOnly = shelterName.isNotBlank()
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Reservar Transporte",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp, // ↑ título grande
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            // ===== Toggle Ir a / Recoger (dos pills que ocupan ancho completo) =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TogglePill(
                    selected = mode == Mode.GoTo,
                    onClick = { mode = Mode.GoTo },
                    label = "Ir a",
                    leading = { Icon(Icons.Filled.NearMe, null) },
                    modifier = Modifier.weight(1f)
                )
                TogglePill(
                    selected = mode == Mode.PickUp,
                    onClick = { mode = Mode.PickUp },
                    label = "Recoger",
                    leading = { Icon(Icons.Filled.LocalTaxi, null) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(18.dp))

            // -------- Tarjeta --------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardStroke, RoundedCornerShape(24.dp))
                    .background(CardBg, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Lugar de Recogida",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp, // ↑
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                CenteredOutlinedField(
                    value = pickupValue,
                    onValueChange = { pickup = it },
                    placeholder = "Locación",
                    placeholderIcon = { Icon(Icons.Filled.LocationOn, null) },
                    readOnly = pickupReadOnly,
                    textSize = 18.sp // ↑
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    "Locación Final",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp, // ↑
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                CenteredOutlinedField(
                    value = dropoffValue,
                    onValueChange = { dropoff = it },
                    placeholder = "Locación",
                    placeholderIcon = { Icon(Icons.Filled.Map, null) },
                    readOnly = dropoffReadOnly,
                    textSize = 18.sp // ↑
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ClickableField(
                        labelOrValue = date.ifBlank { "Fecha" },
                        icon = { Icon(Icons.Filled.CalendarMonth, null) },
                        onClick = { pickDate() },
                        modifier = Modifier.weight(1f),
                        textSize = 18.sp,
                        height = 64.dp
                    )
                    ClickableField(
                        labelOrValue = time.ifBlank { "Hora" },
                        icon = { Icon(Icons.Filled.AccessTime, null) },
                        onClick = { pickTime() },
                        modifier = Modifier.weight(1f),
                        textSize = 18.sp,
                        height = 64.dp
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            Button(
                onClick = {
                    val pickupArg = Uri.encode(pickupValue)
                    val dropoffArg = Uri.encode(dropoffValue)
                    val dateArg = Uri.encode(date)
                    val timeArg = Uri.encode(time)

                    navController.navigate(
                        "waiting_transport?pickup=$pickupArg&dropoff=$dropoffArg&date=$dateArg&time=$timeArg"
                    )
                },
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.padding(end = 10.dp))
                Text("Reservar", fontSize = 24.sp, textAlign = TextAlign.Center)
            }

        }

        AppBottomBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 20.dp)
        )
    }
}

/* =================== Componentes =================== */

@Composable
private fun TogglePill(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    leading: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = if (selected) Teal else Color.Transparent
    val content = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    val border = if (selected) Teal else MaterialTheme.colorScheme.outline

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = container,
        border = androidx.compose.foundation.BorderStroke(2.dp, border),
        tonalElevation = if (selected) 2.dp else 0.dp,
        modifier = modifier.height(64.dp) // altura grande
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            leading()
            Spacer(Modifier.width(10.dp))
            Text(
                label,
                color = content,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CenteredOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    placeholderIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    textSize: androidx.compose.ui.unit.TextUnit = 16.sp
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        readOnly = readOnly,
        placeholder = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                placeholderIcon?.invoke()
                if (placeholderIcon != null) Spacer(Modifier.width(8.dp))
                Text(placeholder, textAlign = TextAlign.Center, fontSize = textSize)
            }
        },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = textSize),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ClickableField(
    labelOrValue: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    height: Dp = 56.dp
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .height(height)
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(Modifier.width(8.dp))
                Text(
                    text = labelOrValue,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = textSize
                )
            }
        }
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTransportScreen() {
    TransportScreen(navController = rememberNavController())
}
