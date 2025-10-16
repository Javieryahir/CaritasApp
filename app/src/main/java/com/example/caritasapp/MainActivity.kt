package com.example.caritasapp

// 👇 importa la nueva pantalla
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.caritasapp.reservations.ShelterDetailsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 👇 Asegura que Android muestre la barra de estado y respete los márgenes del sistema
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            MyApp()
            //MaterialTheme {
            //    com.example.caritasapp.debug.HostelsDebugScreen()
            //}
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login")   { com.example.caritasapp.login.LoginScreen(navController) }

        // Flujo de creación de cuenta
        composable("create1") { com.example.caritasapp.login.CreateAccountPt1(navController) }
        composable("create2") { com.example.caritasapp.login.CreateAccountPt2(navController) }
        composable("create3") { com.example.caritasapp.login.CreateAccountPt3(navController) }

        // Ya la tienes:
        composable("search")  { com.example.caritasapp.reservations.ReservationPage(navController) }
        composable("shelter") { ShelterDetailsScreen(navController) }
    }
}

