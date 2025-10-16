package com.example.caritasapp.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.R
import com.example.caritasapp.composables.HyperlinkText

@Composable
fun LoginScreen(navController: NavController) {
    var phone by remember { mutableStateOf("") }

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

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            placeholder = {
                Text(
                    "Celular",
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        )


        Button(
            onClick = { navController.navigate("search") },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5D97A3),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Iniciar Sesión", fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
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
