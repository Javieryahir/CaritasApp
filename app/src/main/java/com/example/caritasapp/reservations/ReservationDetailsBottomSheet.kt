package com.example.caritasapp.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.caritasapp.data.DetailedReservationResponse
import com.example.caritasapp.data.DetailedHostelData
import com.example.caritasapp.data.PersonReservationData
import com.example.caritasapp.data.DetailedUserData
import com.example.caritasapp.data.NewServiceReservationResponse
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailsBottomSheet(
    reservation: DetailedReservationResponse,
    onDismiss: () -> Unit,
    onContactEmail: () -> Unit = {},
    onContactPhone: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    
    // Colors
    val Accent = Color(0xFF009CA6)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            
            // Hostel Images Carousel
            HostelImagesCarousel(
                images = reservation.hostel.imageUrls,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reservation Summary Card
            ReservationSummaryCard(
                hostel = reservation.hostel,
                startDate = reservation.startDate,
                endDate = reservation.endDate,
                state = reservation.state,
                accentColor = Accent,
                serviceReservations = reservation.serviceReservations
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Hostel Description
            if (reservation.hostel.description.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Accent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = reservation.hostel.description,
                            fontSize = 14.sp,
                            color = Color.Black,
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Guest Information
            if (reservation.personReservations.isNotEmpty()) {
                GuestInformationCard(
                    guests = reservation.personReservations,
                    accentColor = Accent
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Services Section
            if (reservation.serviceReservations.isNotEmpty()) {
                ServicesCard(
                    services = reservation.serviceReservations,
                    accentColor = Accent
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Location Section
            LocationCard(
                _locationUrl = reservation.hostel.locationUrl,
                accentColor = Accent
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Contact Section
            ContactCard(
                _user = reservation.user,
                onContactEmail = onContactEmail,
                onContactPhone = onContactPhone,
                accentColor = Accent
            )
            
            // Bottom padding for safe area
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HostelImagesCarousel(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    if (images.isNotEmpty()) {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(images) { imageUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Hostel Image",
                    modifier = Modifier
                        .width(280.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    } else {
        // Placeholder if no images
        Box(
            modifier = modifier
                .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Image,
                contentDescription = "No Image",
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun ReservationSummaryCard(
    hostel: DetailedHostelData,
    startDate: String,
    endDate: String,
    state: String,
    accentColor: Color,
    serviceReservations: List<NewServiceReservationResponse> = emptyList()
) {
    val totalServiceCost = serviceReservations.sumOf { service -> service.service.price * service.costCount }
    val totalCost = hostel.price + totalServiceCost
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Hostel Name
            Text(
                text = hostel.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Dates and Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Fechas",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatDateRange(startDate, endDate),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Precio",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$${String.format("%.2f", totalCost)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
            
            // Service cost breakdown
            if (totalServiceCost > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Alojamiento:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$${String.format("%.2f", hostel.price)}",
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Servicios:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$${String.format("%.2f", totalServiceCost)}",
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status Section
            StatusSection(
                state = state,
                _accentColor = accentColor
            )
        }
    }
}

@Composable
fun StatusSection(
    state: String,
    _accentColor: Color
) {
    val (statusColor, statusIcon, statusText) = when (state.uppercase()) {
        "PENDING" -> Triple(Color(0xFFFF9800), Icons.Filled.Schedule, "Pendiente")
        "CONFIRMED" -> Triple(Color(0xFF4CAF50), Icons.Filled.CheckCircle, "Confirmado")
        "CANCELLED" -> Triple(Color(0xFFF44336), Icons.Filled.Cancel, "Cancelado")
        "COMPLETED" -> Triple(Color(0xFF2196F3), Icons.Filled.Done, "Completado")
        else -> Triple(Color.Gray, Icons.AutoMirrored.Filled.Help, "Desconocido")
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = statusIcon,
            contentDescription = "Status",
            tint = statusColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Estado: $statusText",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = statusColor
        )
    }
}

@Composable
fun GuestInformationCard(
    guests: List<PersonReservationData>,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Información de Huéspedes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            guests.forEach { guestReservation ->
                GuestItem(
                    guest = guestReservation.person,
                    accentColor = accentColor
                )
                if (guestReservation != guests.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun GuestItem(
    guest: com.example.caritasapp.data.PersonData,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Guest",
            tint = accentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "${guest.firstName} ${guest.lastName}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = "Nacimiento: ${formatDate(guest.birthDate)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LocationCard(
    _locationUrl: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ubicación",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Map placeholder with location icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .clickable { /* Open location URL */ },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ver en Google Maps",
                        fontSize = 12.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    _user: DetailedUserData,
    onContactEmail: () -> Unit,
    onContactPhone: () -> Unit,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "¿Quejas o Sugerencias?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Contáctanos aquí",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Email Button
                Button(
                    onClick = onContactEmail,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Email", fontSize = 14.sp)
                }
                
                // Phone Button
                Button(
                    onClick = onContactPhone,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Phone",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Celular", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun ServicesCard(
    services: List<NewServiceReservationResponse>,
    accentColor: Color
) {
    val totalServiceCost = services.sumOf { service -> service.service.price * service.costCount }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Servicios",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(
                    text = "Total: $${String.format("%.2f", totalServiceCost)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            services.forEachIndexed { index, service ->
                ServiceItem(
                    service = service,
                    accentColor = accentColor
                )
                if (index < services.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ServiceItem(
    service: NewServiceReservationResponse,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Service",
            tint = accentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = getServiceName(service.service.type),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = "Cantidad: ${service.costCount}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "Fecha: ${formatDate(service.orderDate)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "Precio: $${service.service.price}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = accentColor
            )
        }
        // Status chip
        Surface(
            color = when (service.state.uppercase()) {
                "CONFIRMED" -> Color(0xFF4CAF50)
                "PENDING" -> Color(0xFFFF9800)
                "CANCELLED" -> Color(0xFFF44336)
                else -> Color.Gray
            },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = when (service.state.uppercase()) {
                    "CONFIRMED" -> "Confirmado"
                    "PENDING" -> "Pendiente"
                    "CANCELLED" -> "Cancelado"
                    else -> service.state
                },
                fontSize = 10.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// Helper functions
fun formatDateRange(startDate: String, endDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        
        val start = inputFormat.parse(startDate)
        val end = inputFormat.parse(endDate)
        
        "${outputFormat.format(start!!)} - ${outputFormat.format(end!!)}"
    } catch (e: Exception) {
        "$startDate - $endDate"
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}

fun getServiceName(serviceType: String): String {
    return when (serviceType.lowercase()) {
        "breakfasts" -> "Desayuno"
        "lunches" -> "Almuerzo"
        "dinners" -> "Cena"
        "laundry" -> "Lavandería"
        "shower" -> "Ducha"
        "medical" -> "Atención Médica"
        "dental" -> "Atención Dental"
        "psychological" -> "Atención Psicológica"
        else -> serviceType.replaceFirstChar { it.uppercase() }
    }
}
