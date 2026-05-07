package com.fenixgames.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fenixgames.FenixApplication
import com.fenixgames.ui.screens.diagnostics.DiagnosticScreen
import com.fenixgames.ui.screens.diagnostics.DiagnosticViewModelFactory
import com.fenixgames.ui.screens.home.HomeScreen
import com.fenixgames.ui.screens.home.HomeViewModelFactory

object Routes {
    const val HOME = "home"
    const val DIAGNOSTICS = "diagnostics"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as FenixApplication
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel(
                    factory = HomeViewModelFactory(app.container)
                ),
                onOpenDiagnostics = { navController.navigate(Routes.DIAGNOSTICS) }
            )
        }
        composable(Routes.DIAGNOSTICS) {
            DiagnosticScreen(
                viewModel = viewModel(
                    factory = DiagnosticViewModelFactory(app.container)
                ),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
