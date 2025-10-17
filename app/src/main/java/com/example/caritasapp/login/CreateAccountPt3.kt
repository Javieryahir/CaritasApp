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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.data.NetworkModule

private val Teal = Color(0xFF5D97A3)

@Composable
fun CreateAccountPt3(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { NetworkModule.createAuthRepository(context) }
    
    val prev = navController.previousBackStackEntry?.savedStateHandle
    val phone = prev?.get<String>("phone").orEmpty()
    val fullName = prev?.get<String>("fullName").orEmpty()

    var code by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    
    val isLoading by authRepository.isLoading.collectAsState()
    val error by authRepository.error.collectAsState()
    
    val canConfirm = code.length >= 4

    // Handle signup confirmation API call
    LaunchedEffect(isConfirming) {
        if (isConfirming) {
            val result = authRepository.confirmSignup(
                phoneNumber = phone,
                code = code
            )
            
            result.fold(
                onSuccess = {
                    // Navigate to main app
                    navController.navigate("search") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onFailure = {
                    // Error is already handled by the repository
                    isConfirming = false
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
        // Contenido centrado (incluye los botones para que queden pegados)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "VERIFICACIÓN",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))

            Text(
                "Código",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { if (it.all(Char::isDigit)) code = it },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center
                ),
                placeholder = {
                    Text(
                        "Código",
                        fontSize = 22.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

            Spacer(Modifier.height(12.dp))
            Text(
                "Enviado a: $phone",
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            
            // Show error message if any
            error?.let { errorMessage ->
                Spacer(Modifier.height(12.dp))
                Text(
                    errorMessage,
                    color = Color.Red,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Botones grandes, centrados y pegados al campo
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(22.dp),
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
                        tint = Teal,
                        modifier = Modifier.size(44.dp)
                    )
                }

                FilledIconButton(
                    onClick = {
                        isConfirming = true
                    },
                    enabled = canConfirm && !isLoading,
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
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Confirmar",
                            tint = Teal,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }
        }
    }
}
