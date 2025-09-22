import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.widget.DatePicker
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import java.util.Calendar


// Modifica este data class para incluir la information que quieras mostrar en el banner
data class LocationData(val name: String, val latLng: LatLng, val details: String)

fun createTextMarker(context: android.content.Context, text: String): BitmapDescriptor {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        textSize = 40f
        style = Paint.Style.FILL
        textAlign = Paint.Align.LEFT
    }

    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)

    val padding = 20
    val cornerRadius = 25f // Radio de las esquinas

    val bitmap = Bitmap.createBitmap(
        bounds.width() + padding * 2,
        bounds.height() + padding * 2,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)

    // Fondo del "box" con esquinas redondeadas
    paint.color = Color.WHITE
    canvas.drawRoundRect(
        0f,
        0f,
        bitmap.width.toFloat(),
        bitmap.height.toFloat(),
        cornerRadius,
        cornerRadius,
        paint
    )

    // Texto
    paint.color = Color.BLACK
    canvas.drawText(
        text,
        padding.toFloat(),
        bounds.height() + padding.toFloat(),
        paint
    )

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()



    // Estado para la fecha seleccionada
    var selectedDate by remember { mutableStateOf("") }

    // DatePickerDialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Variable de estado para la ubicación seleccionada
    var selectedLocation by remember { mutableStateOf<LocationData?>(null) }

    val location1 = LocationData("Divina Providencia", LatLng(25.668407445212537, -100.30311518095509), "La capital de México.")
    val location2 = LocationData("Posada del Peregrino", LatLng(25.683374989284424,  -100.3454713715995), "Ciudad blanca, conocida por su cultura maya.")
    val location3 = LocationData("Cancún", LatLng(21.1619, -86.8515), "Famoso destino de playas del Caribe.")

    val locations = listOf(location1, location2, location3)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location1.latLng, 5f)
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp, // El banner inicia oculto
        sheetContent = {
            // El contenido del banner se muestra si hay una ubicación seleccionada
            selectedLocation?.let { location ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Sección de la imagen (placeholder)
                    // Puedes usar Coil, Glide o un simple Box con un color de fondo para simular la imagen.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Altura de la imagen
                            // Color de fondo del placeholder
                    ) {
                        // Indicador de carrusel (los puntos)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)

                                )
                            }
                        }
                    }

                    // Sección de información de la ubicación
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Título y detalles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = location.name,
                                    style = MaterialTheme.typography.titleLarge // Estilo para el título
                                )
                                Text(
                                    text = "1.2 miles", // Placeholder para la distancia
                                    style = MaterialTheme.typography.bodyMedium,

                                )
                            }
                            Button(
                                onClick = {
                                    // Lógica para abrir la pantalla de detalles
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = androidx.compose.ui.graphics.Color.Black,
                                    contentColor = androidx.compose.ui.graphics.Color.White    // Texto blanco
                                )
                            ) {
                                Text(text = "Detalles")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Cantidad de cupos
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "4", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Cupos", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                colors = TopAppBarDefaults.topAppBarColors(),
                actions = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // Asegura que el Row ocupe todo el espacio disponible
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround // Alinea los elementos a los extremos
                    ) {
                        // Primer elemento a la izquierda (Botón de fecha)
                        Button(
                            onClick = { datePickerDialog.show() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color.White,
                                contentColor = androidx.compose.ui.graphics.Color.Black

                            )
                        ) {
                            Row(

                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center // Centra el contenido horizontalmente
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange, // Icono de calendario
                                    contentDescription = "Seleccionar fecha"
                                )
                                Spacer(modifier = Modifier.width(4.dp)) // Espacio entre el icono y el texto
                                Text(text = "Seleccionar Fechas")
                            }
                        }

                        // Segundo elemento a la derecha (Botón de filtro)
                        IconButton(onClick = { /* Lógica para el filtro */ }) {
                            Icon(
                                imageVector = Icons.Default.Star, // Icono de filtro
                                contentDescription = "Filtros"
                            )
                        }
                    }

                }
            )
        }
    ) {
        // El contenido principal del scaffold es el mapa
        GoogleMap(
            modifier = Modifier.fillMaxWidth(),
            cameraPositionState = cameraPositionState
        ) {
            locations.forEach { location ->
                Marker(
                    state = MarkerState(location.latLng),
                    title = location.name,
                    icon = createTextMarker(context, location.name),
                    onClick = {
                        // Al hacer clic en el marcador, actualiza el estado
                        selectedLocation = location
                        scope.launch {
                            // Muestra el banner
                            scaffoldState.bottomSheetState.expand()
                        }
                        true // Devuelve true para indicar que el evento ha sido consumido
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMapScreen() {
    MapScreen()
}