package com.example.caritasapp.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.navegationbar.AppBottomBar
import com.example.caritasapp.utils.QRCodeGenerator
import java.text.SimpleDateFormat
import java.util.*

private val Accent = Color(0xFF009CA6)
private val LightAccent = Color(0xFFE0F7F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceConfirmationScreen(navController: NavController) {
    // Get service reservation data from navigation state
    val serviceName = navController.currentBackStackEntry?.savedStateHandle?.get<String>("service_name")
    val peopleCount = navController.currentBackStackEntry?.savedStateHandle?.get<Int>("people_count")
    val orderDate = navController.currentBackStackEntry?.savedStateHandle?.get<String>("order_date")
    val serviceState = navController.currentBackStackEntry?.savedStateHandle?.get<String>("service_state")
    val reservationId = navController.currentBackStackEntry?.savedStateHandle?.get<String>("qr_code")
    
    // Debug logging with more details
    println("🔍 ServiceConfirmationScreen - Navigation state data:")
    println("  service_name: $serviceName")
    println("  people_count: $peopleCount")
    println("  order_date: $orderDate")
    println("  service_state: $serviceState")
    println("  qr_code: $reservationId")
    println("🔍 ServiceConfirmationScreen - BackStackEntry: ${navController.currentBackStackEntry}")
    println("🔍 ServiceConfirmationScreen - SavedStateHandle: ${navController.currentBackStackEntry?.savedStateHandle}")
    
    // Check if we have valid data
    val hasValidData = serviceName != null && peopleCount != null && orderDate != null && serviceState != null && reservationId != null
    println("🔍 ServiceConfirmationScreen - Has valid data: $hasValidData")
    
    // Generate QR code URL with proper format
    val qrCodeUrl = remember(reservationId) {
        "https://caritas.automvid.store/confirm-reservation?id=$reservationId"
    }
    
    // Generate QR code bitmap
    val qrCodeBitmap = remember(qrCodeUrl) {
        QRCodeGenerator.generateQRCode(qrCodeUrl, 400, 400)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Su Reservación",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                navController = navController,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // QR Code Card
                Card(
                    modifier = Modifier
                        .size(280.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = LightAccent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // QR Code visual representation
                        Card(
                            modifier = Modifier
                                .size(200.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (qrCodeBitmap != null) {
                                    androidx.compose.foundation.Image(
                                        bitmap = qrCodeBitmap.asImageBitmap(),
                                        contentDescription = "QR Code",
                                        modifier = Modifier
                                            .size(180.dp)
                                            .padding(8.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    // Fallback to text if QR code generation fails
                                    Text(
                                        text = "QR CODE",
                                        color = Color.Black,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Service Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = serviceName ?: "Servicio",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Accent
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        Text(
                            text = "Para ${peopleCount ?: 1} persona${if ((peopleCount ?: 1) > 1) "s" else ""}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        
                        Spacer(Modifier.height(4.dp))
                        
                        Text(
                            text = formatServiceDate(orderDate ?: "2025-10-18"),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                // Service Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Estado de la Reservación",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = getServiceStateText(serviceState ?: "PENDING"),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        
                        Surface(
                            color = getServiceStateColor(serviceState ?: "PENDING"),
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = getServiceStateBadge(serviceState ?: "PENDING"),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Instructions Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Instrucciones",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        Text(
                            text = "Muestra este código al personal en su respectivo lugar. Se puede descargar para acceso offline.",
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.8f),
                            lineHeight = 24.sp
                        )
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Download QR Code Button
                    Button(
                        onClick = { 
                            // Handle QR code download
                            // In real implementation, this would save the QR code to device storage
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Descargar Código QR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                // Extra spacing to ensure buttons are visible
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// Helper functions
private fun getServiceStateText(state: String): String {
    return when (state) {
        "PENDING" -> "Esperando confirmación"
        "ACTIVE" -> "Reservación activa"
        "COMPLETED" -> "Servicio completado"
        "CANCELLED" -> "Reservación cancelada"
        else -> "Estado desconocido"
    }
}

private fun getServiceStateBadge(state: String): String {
    return when (state) {
        "PENDING" -> "PENDIENTE"
        "ACTIVE" -> "ACTIVA"
        "COMPLETED" -> "COMPLETADA"
        "CANCELLED" -> "CANCELADA"
        else -> "DESCONOCIDO"
    }
}

private fun getServiceStateColor(state: String): Color {
    return when (state) {
        "PENDING" -> Color(0xFFFF9800) // Orange
        "ACTIVE" -> Color(0xFF4CAF50) // Green
        "COMPLETED" -> Color(0xFF2196F3) // Blue
        "CANCELLED" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }
}

private fun formatServiceDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (_: Exception) {
        dateString
    }
}