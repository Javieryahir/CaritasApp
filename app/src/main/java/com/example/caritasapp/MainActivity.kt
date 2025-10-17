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
import com.example.caritasapp.reservations.ConfirmReservation
import com.example.caritasapp.reservations.ShelterDetailsScreen
import com.example.caritasapp.transport.TransportScreen
import com.example.caritasapp.transport.WaitingPage as WaitingTransportPage

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
        composable("create1") { com.example.caritasapp.login.CreateAccountPt1(navController) }
        composable("create2") { com.example.caritasapp.login.CreateAccountPt2(navController) }
        composable("create3") { com.example.caritasapp.login.CreateAccountPt3(navController) }
        composable("search")  { com.example.caritasapp.reservations.ReservationPage(navController) }
        composable("shelter") { ShelterDetailsScreen(navController) }

        // 👇 nueva firma con argumento
        composable("health/{count}") { backStackEntry ->
            val count = backStackEntry.arguments?.getString("count")?.toIntOrNull() ?: 1
            com.example.caritasapp.reservations.HealthFormsScreen(navController, count)
        }
        composable("confirm") { ConfirmReservation(navController) }
        composable("waiting") { com.example.caritasapp.reservations.WaitingPage(navController) }
        composable("transport")  { TransportScreen(navController) }
        composable(
            route = "waiting_transport?pickup={pickup}&dropoff={dropoff}&date={date}&time={time}"
        ) { backStackEntry ->
            val pickup  = backStackEntry.arguments?.getString("pickup")  ?: ""
            val dropoff = backStackEntry.arguments?.getString("dropoff") ?: ""
            val date    = backStackEntry.arguments?.getString("date")    ?: ""
            val time    = backStackEntry.arguments?.getString("time")    ?: ""

            WaitingTransportPage(
                navController = navController,
                pickup = pickup,
                dropoff = dropoff,
                date = date,
                time = time
            )
        }

    }
}

