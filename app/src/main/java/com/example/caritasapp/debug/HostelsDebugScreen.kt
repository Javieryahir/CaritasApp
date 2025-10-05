// app/src/main/java/com/example/caritasapp/debug/HostelsDebugScreen.kt
package com.example.caritasapp.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caritasapp.reservations.HostelsLoadState
import com.example.caritasapp.reservations.ReservationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelsDebugScreen(
    vm: ReservationsViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    // lanza una carga de prueba (ajusta fechas a lo que tu backend espera)
    LaunchedEffect(Unit) {
        vm.loadHostels(
            startDate = "2025-10-04",
            endDate   = "2025-10-16",
            filtersCsv = null // o "laundry,meal,breakfast"
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Prueba de Hostels") }) }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is HostelsLoadState.Idle -> Text("Idle…")
                is HostelsLoadState.Loading -> CircularProgressIndicator(Modifier.padding(24.dp))
                is HostelsLoadState.Error -> Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                is HostelsLoadState.Success -> {
                    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
                        items(s.items) { h ->
                            ElevatedCard(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(h.name, style = MaterialTheme.typography.titleLarge)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Cupos disponibles: ${h.availableSpaces}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
