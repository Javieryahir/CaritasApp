package com.example.caritasapp

// 👇 importa la nueva pantalla
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.caritasapp.reservations.ReservationPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        composable("login")   { LoginScreen(navController) }
        // 👇 Antes: MapScreen(navController). Ahora: ReservationPage(navController)
        composable("search")  { ReservationPage(navController) }
        composable("shelter") { ShelterDetailsScreen(navController) }
    }
}
