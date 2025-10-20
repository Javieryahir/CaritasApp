package com.example.caritasapp.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.data.countryCodes
import com.example.caritasapp.data.NetworkModule
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext

private val Teal = Color(0xFF5D97A3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountPt1(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { NetworkModule.createAuthRepository(context) }
    
    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var phone     by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(countryCodes[0]) }
    var expanded by remember { mutableStateOf(false) }
    var isSigningUp by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf("") }
    
    val isLoading by authRepository.isLoading.collectAsState()
    val error by authRepository.error.collectAsState()

    // Phone validation
    val isValidPhone = phone.length == 10 && phone.all(Char::isDigit)
    val canContinue = firstName.isNotBlank() && isValidPhone && !isLoading

    // Handle signup API call
    LaunchedEffect(isSigningUp) {
        if (isSigningUp) {
            val fullPhoneNumber = "${selectedCountry.code}${phone}"
            val result = authRepository.signup(
                phoneNumber = fullPhoneNumber,
                firstName = firstName.trim(),
                lastName = lastName.trim()
            )
            
            result.fold(
                onSuccess = {
                    // Navigate to confirmation screen
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "fullName", "${firstName.trim()} ${lastName.trim()}".trim()
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set("phone", fullPhoneNumber)
                    navController.navigate("create2")
                },
                onFailure = {
                    // Error is already handled by the repository
                    isSigningUp = false
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(horizontal = 28.dp, vertical = 20.dp)
    ) {
        // ===== Formulario centrado con el título pegado =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),        // título + campos van al centro
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TÍTULO – ahora justo encima del primer campo
            Text(
                "¿CUÁL ES TU NOMBRE?",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))        // separación corta con el primer label

            // Nombre(s)
            Text("Nombre(s)", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = {
                    Text(
                        "Nombre(s)",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                singleLine = true,
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


            Spacer(Modifier.height(18.dp))

            // Apellidos
            Text("Apellidos", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = {
                    Text(
                        "Apellidos",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                singleLine = true,
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


            Spacer(Modifier.height(18.dp))

            // Celular
            Text("Celular", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            
            // Phone input with country code
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                                tint = Teal
                            )
                        },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
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
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    singleLine = true,
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
                    ),
                    isError = phoneError.isNotEmpty() || (phone.isNotEmpty() && !isValidPhone)
                )
            }
            
            // Show phone validation error
            if (phoneError.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    phoneError,
                    color = Color.Red,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            // Show phone length error
            if (phone.isNotEmpty() && !isValidPhone) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "El número debe tener exactamente 10 dígitos",
                    color = Color.Red,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Show API error message if any
            error?.let { errorMessage ->
                Spacer(Modifier.height(12.dp))
                Text(
                    "Error: $errorMessage",
                    color = Color.Red,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Botones GRANDES, centrados y "pegados" al campo de celular
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.wrapContentWidth(), // no a los extremos
                horizontalArrangement = Arrangement.spacedBy(22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = { navController.popBackStack() },
                    shape = CircleShape,
                    modifier = Modifier.size(84.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White)
                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Teal, modifier = Modifier.size(40.dp)) }

                FilledIconButton(
                    onClick = {
                        if (isValidPhone) {
                            isSigningUp = true
                        } else {
                            phoneError = "Por favor ingresa un número válido de 10 dígitos"
                        }
                    },
                    enabled = canContinue,
                    shape = CircleShape,
                    modifier = Modifier.size(84.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White,
                        disabledContainerColor = Color(0x33FFFFFF)
                    )
                ) { 
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Teal,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Siguiente", tint = Teal, modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}
