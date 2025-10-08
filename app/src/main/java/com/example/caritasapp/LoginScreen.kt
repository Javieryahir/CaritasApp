package com.example.caritasapp

import MapScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.caritasapp.composables.HyperlinkText


@Composable
fun LoginScreen(navController: NavController) {



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Cáritas Monterrey",
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .padding(bottom = 24.dp)
        )


        // Título
        Text(
            text = "Ingresa con Google",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
        )

        // Subtítulo
        Text(
            text = "Ingresa con el siguiente botón",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Botón Google
        Button(
            onClick = {navController.navigate("search") },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continuar con Google")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto de términos
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