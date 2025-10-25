package com.example.caritasapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith

// ---------- Pantallas ----------
import com.example.caritasapp.account.AccountScreen
import com.example.caritasapp.loading.LoadingScreen
import com.example.caritasapp.login.CreateAccountPt1
import com.example.caritasapp.login.CreateAccountPt2
import com.example.caritasapp.login.CreateAccountPt3
import com.example.caritasapp.login.LoginScreen
import com.example.caritasapp.reservations.RescheduleReservationScreen
import com.example.caritasapp.reservations.ServiceConfirmationScreen
import com.example.caritasapp.reservations.ServiceDetailsScreen
import com.example.caritasapp.reservations.ServiceReservationScreen
import com.example.caritasapp.reserve.ConfirmReservation
import com.example.caritasapp.reserve.ShelterDetailsScreen
import com.example.caritasapp.reserve.HealthFormsScreen
import com.example.caritasapp.reserve.WaitingPage
import com.example.caritasapp.transport.TransportScreen
import com.example.caritasapp.transport.WaitingPage as WaitingTransportPage
import com.example.caritasapp.reserve.ReservationPage as ReservePage      // pantalla del mapa / filtros
import com.example.caritasapp.reservations.ReservationPage as ReservationsPage // historial / lista

@RunWith(AndroidJUnit4::class)
class NavGraphRoutesTest {

    // 👇 OJO: usamos ComponentActivity, NO MainActivity
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun navGraph_registers_important_routes_and_rejects_unknown() {
        lateinit var navController: NavHostController

        composeRule.setContent {
            navController = rememberNavController().also {
                // aseguramos el navigator de Compose para navegación por rutas
                it.navigatorProvider.addNavigator(ComposeNavigator())
            }

            NavHost(navController = navController, startDestination = "login") {
                composable("login")   { LoginScreen(navController) }
                composable("create1") { CreateAccountPt1(navController) }
                composable("create2") { CreateAccountPt2(navController) }
                composable("create3") { CreateAccountPt3(navController) }

                composable("loading") { LoadingScreen(navController) }
                composable("search") { ReservePage(navController) }
                composable("reservations") { ReservationsPage(navController) }
                composable("shelter") { ShelterDetailsScreen(navController) }

                composable("health/{count}") { backStackEntry ->
                    val count = backStackEntry.arguments?.getString("count")?.toIntOrNull() ?: 1
                    HealthFormsScreen(navController, count)
                }

                composable("confirm") { ConfirmReservation(navController) }
                composable("waiting") { WaitingPage(navController) }
                composable("transport") { TransportScreen(navController) }

                composable("waiting_transport?pickup={pickup}&dropoff={dropoff}&date={date}&time={time}") {
                    val pickup  = it.arguments?.getString("pickup")  ?: ""
                    val dropoff = it.arguments?.getString("dropoff") ?: ""
                    val date    = it.arguments?.getString("date")    ?: ""
                    val time    = it.arguments?.getString("time")    ?: ""
                    WaitingTransportPage(
                        navController = navController,
                        pickup = pickup,
                        dropoff = dropoff,
                        date = date,
                        time = time
                    )
                }

                composable("account") { AccountScreen(navController) }

                composable("service_reservations") {
                    ServiceReservationScreen(navController)
                }
                composable("service_details/{serviceId}") { backStackEntry ->
                    val serviceId = backStackEntry.arguments?.getString("serviceId") ?: "1"
                    ServiceDetailsScreen(serviceId, navController)
                }
                composable("service_confirmation") {
                    ServiceConfirmationScreen(navController)
                }
                composable("reschedule/{reservationId}") { backStackEntry ->
                    val reservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
                    RescheduleReservationScreen(reservationId, navController)
                }
            }
        }

        // ---- casos positivos ----
        composeRule.runOnUiThread { navController.navigate("service_details/1") }
        assertEquals("service_details/{serviceId}", navController.currentDestination?.route)

        composeRule.runOnUiThread { navController.navigate("service_confirmation") }
        assertEquals("service_confirmation", navController.currentDestination?.route)

        composeRule.runOnUiThread { navController.navigate("reschedule/ABC123") }
        assertEquals("reschedule/{reservationId}", navController.currentDestination?.route)

        // ---- caso negativo ----
        val result = runCatching {
            composeRule.runOnUiThread { navController.navigate("unknown/route") }
        }
        assertTrue(
            "Esperábamos que navegar a una ruta inexistente falle",
            result.isFailure
        )
    }
}
