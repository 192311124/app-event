package com.example.rent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.rent.ui.components.BottomNavBar
import com.example.rent.ui.screens.*
import com.example.rent.ui.theme.BgPrimary
import com.example.rent.ui.viewmodel.AuthViewModel
import com.example.rent.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val currentRoute by mainViewModel.currentRoute.collectAsState()
            val routeParams by mainViewModel.routeParams.collectAsState()
            val currentUser by authViewModel.currentUser.collectAsState()

            val toastMessage by mainViewModel.toastMessage.collectAsState()
            val context = androidx.compose.ui.platform.LocalContext.current

            // Refresh user data when auth state changes
            androidx.compose.runtime.LaunchedEffect(currentUser) {
                currentUser?.let { user ->
                    mainViewModel.refreshUserData(user.uid)
                }
            }

            // Global toast observer for mainViewModel
            androidx.compose.runtime.LaunchedEffect(toastMessage) {
                toastMessage?.let { msg ->
                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                    mainViewModel.clearToast()
                }
            }

            // System back button handler
            androidx.activity.compose.BackHandler(enabled = currentRoute != "home" && currentRoute != "onboarding") {
                mainViewModel.goBack()
            }

            val cartItems by mainViewModel.cartItems.collectAsState()
            val cartCount = cartItems.sumOf { it.quantity }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = BgPrimary,
                bottomBar = {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        cartCount = cartCount,
                        onNavigate = { route -> mainViewModel.navigateTo(route) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgPrimary)
                        .padding(innerPadding)
                ) {
                    when (currentRoute) {
                        "onboarding" -> OnboardingScreen(
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "auth" -> AuthScreen(
                            initialMode = (routeParams["initialMode"] as? String) ?: "signin",
                            viewModel = authViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "location" -> LocationScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "home" -> HomeScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "categories", "category-details" -> CategoriesScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "items" -> ItemsCatalogScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "item-details" -> ItemDetailsScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "wishlist" -> WishlistScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "cart" -> CartScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "checkout" -> CheckoutScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "bookings" -> BookingsScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "profile" -> ProfileScreen(
                            authViewModel = authViewModel,
                            mainViewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "recommend" -> AiRecommendScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        "help" -> HelpCenterScreen(
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                        else -> HomeScreen(
                            viewModel = mainViewModel,
                            onNavigate = { r, p -> mainViewModel.navigateTo(r, p) }
                        )
                    }
                }
            }
        }
    }
}