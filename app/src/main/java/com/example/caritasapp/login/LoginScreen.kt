package com.example.caritasapp.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.R
import com.example.caritasapp.composables.HyperlinkText
import com.example.caritasapp.data.NetworkModule
import com.example.caritasapp.data.countryCodes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { NetworkModule.createAuthRepository(context) }
    
    var phone by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(countryCodes[0]) }
    var expanded by remember { mutableStateOf(false) }
    var isLoggingIn by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf("") }
    
    val isLoading by authRepository.isLoading.collectAsState()
    val error by authRepository.error.collectAsState()
    
    // Phone validation
    val isValidPhone = phone.length == 10 && phone.all(Char::isDigit)

    // Handle login API call
    LaunchedEffect(isLoggingIn) {
        if (isLoggingIn) {
            val fullPhoneNumber = "${selectedCountry.code}${phone}"
            val result = authRepository.login(phoneNumber = fullPhoneNumber)
            
            result.fold(
                onSuccess = {
                    // Navigate to loading screen to check for active reservations
                    navController.navigate("loading")
                },
                onFailure = {
                    // Error is already handled by the repository
                    isLoggingIn = false
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Cáritas Monterrey",
            modifier = Modifier
                .height(200.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Ingresa con tu celular",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Phone input with country code
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Country code dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.width(120.dp)
            ) {
                OutlinedTextField(
                    value = "${selectedCountry.flag} ${selectedCountry.code}",
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = "Seleccionar país",
                            tint = Color(0xFF5D97A3)
                        )
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        cursorColor = Color(0xFF5D97A3)
                    )
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    countryCodes.forEach { country ->
                        DropdownMenuItem(
                            text = { 
                                Text("${country.flag} ${country.code} ${country.name}")
                            },
                            onClick = {
                                selectedCountry = country
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // Phone number input
            OutlinedTextField(
                value = phone,
                onValueChange = { 
                    if (it.all(Char::isDigit) && it.length <= 10) {
                        phone = it
                        phoneError = ""
                    }
                },
                placeholder = {
                    Text(
                        "Número (10 dígitos)",
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    cursorColor = Color(0xFF5D97A3)
                ),
                isError = phoneError.isNotEmpty() || (phone.isNotEmpty() && !isValidPhone)
            )
        }


        // Show phone validation error
        if (phoneError.isNotEmpty()) {
            Text(
                phoneError,
                color = Color.Red,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
        
        // Show phone length error
        if (phone.isNotEmpty() && !isValidPhone) {
            Text(
                "El número debe tener exactamente 10 dígitos",
                color = Color.Red,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Show API error message if any
        error?.let { errorMessage ->
            Text(
                "Error: $errorMessage",
                color = Color.Red,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { 
                if (isValidPhone) {
                    isLoggingIn = true
                } else {
                    phoneError = "Por favor ingresa un número válido de 10 dígitos"
                }
            },
            enabled = isValidPhone && !isLoading,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5D97A3),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Iniciar Sesión", fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("create1") },   // << AQUÍ
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD8E2DC),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp)
        ) {
            Text("Crear Cuenta", fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        HyperlinkText(
            fullText = "Al hacer clic en «Continuar», aceptas nuestros Términos de servicio y nuestra Política de privacidad.",
            hyperlinks = mapOf(
                "Términos de servicio" to "https://tecmx-my.sharepoint.com/:b:/g/personal/a01782862_tec_mx/EQHQJcc5Do5HkaOYdGlx5awB4R3---4CjhpAi2vJ_Zj6aw?e=gRy9zF",
                "Política de privacidad" to "https://tecmx-my.sharepoint.com/:b:/g/personal/a01782862_tec_mx/EReaHOm1lSpCgVQYi3YX_tMBlSo7GM6hFJvruEi8Rl-gUw?e=yaU7tm"
            ),
            fontSize = 18.sp,
            textColor = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
