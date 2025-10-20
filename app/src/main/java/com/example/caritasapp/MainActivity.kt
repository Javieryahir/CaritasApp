package com.example.caritasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ---------- Pantallas ----------
import com.example.caritasapp.account.AccountScreen
import com.example.caritasapp.reserve.ConfirmReservation
import com.example.caritasapp.reserve.ShelterDetailsScreen
import com.example.caritasapp.reserve.HealthFormsScreen
import com.example.caritasapp.transport.TransportScreen
import com.example.caritasapp.transport.WaitingPage as WaitingTransportPage
import com.example.caritasapp.data.ReservationData

// 👇 Alias para diferenciar “mapa” vs “lista”
import com.example.caritasapp.reserve.ReservationPage as ReservePage          // MAPA/FILTROS (antes “ReservationPage”)
import com.example.caritasapp.reservations.ReservationPage as ReservationsPage // LISTA/HISTORIAL nuevo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent { MyApp() }
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
        
        // Loading screen that checks for active reservations
        composable("loading") { com.example.caritasapp.loading.LoadingScreen(navController) }

        // “search” = flujo de RESERVA (mapa/filtros, antes llamado ReservationPage)
        composable("search")        { ReservePage(navController) }

        // NUEVA pantalla de LISTA/HISTORIAL para el tab “Reservaciones”
        composable("reservations")  { ReservationsPage(navController) }

        composable("shelter")       { ShelterDetailsScreen(navController) }

        composable("health/{count}") { backStackEntry ->
            val count = backStackEntry.arguments?.getString("count")?.toIntOrNull() ?: 1
            HealthFormsScreen(navController, count)
        }

        composable("confirm")  { ConfirmReservation(navController) }
        composable("waiting")  { com.example.caritasapp.reserve.WaitingPage(navController) }

        composable("transport") { TransportScreen(navController) }

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
        composable("account") { AccountScreen(navController) }
        
        // Service reservation routes
        composable("service_reservations") { com.example.caritasapp.reservations.ServiceReservationScreen(navController) }
        composable("service_details/{serviceId}") { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: "1"
            com.example.caritasapp.reservations.ServiceDetailsScreen(serviceId, navController)
        }
        composable("service_confirmation") { com.example.caritasapp.reservations.ServiceConfirmationScreen(navController) }
        
        // Reschedule reservation route
        composable("reschedule/{reservationId}") { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
            com.example.caritasapp.reservations.RescheduleReservationScreen(reservationId, navController)
        }
    }
}

