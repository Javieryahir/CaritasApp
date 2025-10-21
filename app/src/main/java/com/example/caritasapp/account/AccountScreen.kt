package com.example.caritasapp.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.caritasapp.data.NetworkModule
import com.example.caritasapp.navegationbar.AppBottomBar
import com.example.caritasapp.viewmodel.AccountViewModel

private val Teal = Color(0xFF5D97A3)
private val Accent = Color(0xFF009CA6)

@Composable
fun AccountScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { NetworkModule.createAuthRepository(context) }
    val userRepository = remember { NetworkModule.createUserRepository(context) }
    val viewModel: AccountViewModel = viewModel {
        AccountViewModel(authRepository, userRepository)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle logout navigation
    LaunchedEffect(uiState.user, uiState.isLoading) {
        println("=== LOGOUT DEBUG ===")
        println("User: ${uiState.user}")
        println("IsLoading: ${uiState.isLoading}")
        println("Should navigate: ${uiState.user == null && !uiState.isLoading}")
        println("===================")
        
        if (uiState.user == null && !uiState.isLoading) {
            println("Navigating to login screen...")
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ========== Header teal bajado hasta el centro ==========
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val headerHeight = this.maxHeight / 2 - 160.dp // borde inferior en el centro

            Surface(
                color = Teal,
                shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(headerHeight)
            ) {}
        }

        // ========== Contenido centrado (avatar en el centro) ==========
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(bottom = 110.dp), // espacio para la bottom bar
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(60.dp))
            // Loading state
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Teal,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Cargando datos...",
                    color = Teal,
                    fontSize = 18.sp
                )
            } else {
                // Avatar centrado; quedará mitad sobre teal / mitad sobre blanco
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(8.dp, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Avatar",
                            tint = Teal,
                            modifier = Modifier.size(110.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Capture user data in local variables to avoid smart cast issues
                val user = uiState.user
                val fullName = if (user != null) {
                    val firstName = user.firstName ?: ""
                    val lastName = user.lastName ?: ""
                    val name = "${firstName} ${lastName}".trim()
                    if (name.isBlank()) "Usuario" else name
                } else {
                    "Usuario"
                }
                
                // Debug logging
                println("=== ACCOUNT SCREEN DEBUG ===")
                println("User: $user")
                println("User firstName: ${user?.firstName}")
                println("User lastName: ${user?.lastName}")
                println("Full name: $fullName")
                println("UI State: $uiState")
                println("============================")
                
                Text(
                    text = fullName.ifBlank { "Usuario" },
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(8.dp))
                
                // Add a subtitle
                Text(
                    text = "Mi Cuenta",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Tarjetas con datos del usuario
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tarjeta de teléfono
                    UserInfoCard(
                        icon = Icons.Filled.Phone,
                        label = "Teléfono",
                        value = user?.phoneNumber ?: "No disponible"
                    )
                    
                    // Tarjeta de email si está disponible
                    user?.email?.let { email ->
                        UserInfoCard(
                            icon = Icons.Filled.Email,
                            label = "Email",
                            value = email
                        )
                    }
                    
                    // Add additional info cards to make it less empty
                    UserInfoCard(
                        icon = Icons.Filled.Person,
                        label = "Tipo de Usuario",
                        value = "Usuario Registrado"
                    )
                    
                    UserInfoCard(
                        icon = Icons.Filled.Security,
                        label = "Estado de Cuenta",
                        value = "Activa"
                    )
                }

                Spacer(Modifier.height(32.dp))

                // Error message
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Botón cerrar sesión
                Button(
                    onClick = {
                        println("=== LOGOUT BUTTON CLICKED ===")
                        viewModel.logout()
                        println("Logout method called")
                        // Navigate directly after logout
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                        println("Navigation called")
                        println("=============================")
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Cerrar Sesión", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Bottom bar
        AppBottomBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 20.dp)
        )
    }
}

@Composable
private fun UserInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Teal.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Teal,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = Teal.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF202020),
                    fontSize = 16.sp
                )
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAccountScreen() {
    AccountScreen(navController = rememberNavController())
}
