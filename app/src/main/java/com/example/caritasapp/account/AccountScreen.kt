package com.example.caritasapp.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
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
                .padding(horizontal = 24.dp)
                .padding(bottom = 110.dp), // espacio para la bottom bar
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                    fontSize = 16.sp
                )
            } else {
                // Avatar centrado; quedará mitad sobre teal / mitad sobre blanco
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .size(124.dp)
                        .shadow(8.dp, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Avatar",
                            tint = Teal,
                            modifier = Modifier.size(115.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Capture user data in local variables to avoid smart cast issues
                val user = uiState.user
                val fullName = if (user != null) {
                    "${user.firstName} ${user.lastName}".trim()
                } else {
                    "Usuario"
                }
                
                Text(
                    text = fullName,
                    color = Teal,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(18.dp))

                // Tarjetas con datos del usuario
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                }

                Spacer(Modifier.height(24.dp))

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
                    Text("Cerrar Sesión", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Teal,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = Teal.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF202020)
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
