package com.financetracker.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.financetracker.app.data.repository.FinanceRepository
import com.financetracker.app.ui.screens.cards.AddCardScreen
import com.financetracker.app.ui.screens.cards.CardViewModel
import com.financetracker.app.ui.screens.cards.CardsScreen
import com.financetracker.app.ui.screens.dashboard.DashboardScreen
import com.financetracker.app.ui.screens.dashboard.DashboardViewModel
import com.financetracker.app.ui.screens.onboarding.OnboardingScreen
import com.financetracker.app.ui.screens.profile.ProfileScreen
import com.financetracker.app.ui.screens.profile.ProfileViewModel
import com.financetracker.app.ui.screens.transactions.AddTransactionScreen
import com.financetracker.app.ui.screens.transactions.EditTransactionScreen
import com.financetracker.app.ui.screens.transactions.TransactionListScreen
import com.financetracker.app.ui.screens.transactions.TransactionViewModel

sealed class Screen(val route: String, val label: String) {
    object Onboarding : Screen("onboarding", "Onboarding")
    object Dashboard : Screen("dashboard", "Home")
    object Transactions : Screen("transactions", "Transactions")
    object Cards : Screen("cards", "Cards")
    object Profile : Screen("profile", "Profile")
    object AddTransaction : Screen("add_transaction", "Add Transaction")
    object EditTransaction : Screen("edit_transaction/{id}", "Edit Transaction") {
        fun buildRoute(id: String) = "edit_transaction/$id"
    }
    object AddCard : Screen("add_card", "Add Card")
}

private val bottomNavItems = listOf(Screen.Dashboard, Screen.Transactions, Screen.Cards, Screen.Profile)

@Composable
fun FinanceNavHost(
    repository: FinanceRepository,
    startDestination: String,
    deepLinkTransactionId: String? = null
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination
            val showBottomBar = bottomNavItems.any { currentRoute?.hierarchy?.any { dest -> dest.route == it.route } == true }
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                val icon = when (screen) {
                                    Screen.Dashboard -> Icons.Filled.Home
                                    Screen.Transactions -> Icons.Filled.List
                                    Screen.Cards -> Icons.Filled.CreditCard
                                    Screen.Profile -> Icons.Filled.Person
                                    else -> Icons.Filled.Home
                                }
                                Icon(icon, contentDescription = screen.label)
                            },
                            label = { Text(screen.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Onboarding.route) {
                val profileVm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(repository))
                OnboardingScreen(onFinished = {
                    // Ensure a profile row exists so the app doesn't re-show onboarding next launch.
                    profileVm.saveProfile(
                        name = "",
                        email = "",
                        currencySymbol = "\u20B9",
                        monthlyBudget = null,
                        smsAutoDetectEnabled = true,
                        notificationsEnabled = true
                    )
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
            }

            composable(Screen.Dashboard.route) {
                val vm: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory(repository))
                DashboardScreen(
                    viewModel = vm,
                    onTransactionClick = { id -> navController.navigate(Screen.EditTransaction.buildRoute(id)) },
                    onSeeAllTransactions = { navController.navigate(Screen.Transactions.route) },
                    onSeeAllCards = { navController.navigate(Screen.Cards.route) }
                )
            }

            composable(Screen.Transactions.route) {
                val vm: TransactionViewModel = viewModel(factory = TransactionViewModel.Factory(repository))
                val profileVm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(repository))
                val profile by profileVm.profile.collectAsState()
                TransactionListScreen(
                    viewModel = vm,
                    currencySymbol = profile?.currencySymbol ?: "\u20B9",
                    onTransactionClick = { id -> navController.navigate(Screen.EditTransaction.buildRoute(id)) },
                    onAddClick = { navController.navigate(Screen.AddTransaction.route) }
                )
            }

            composable(Screen.AddTransaction.route) {
                val vm: TransactionViewModel = viewModel(factory = TransactionViewModel.Factory(repository))
                AddTransactionScreen(viewModel = vm, onSaved = { navController.popBackStack() })
            }

            composable(
                Screen.EditTransaction.route,
                arguments = listOf(navArgument("id") { })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                val vm: TransactionViewModel = viewModel(factory = TransactionViewModel.Factory(repository))
                val profileVm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(repository))
                val profile by profileVm.profile.collectAsState()
                EditTransactionScreen(
                    viewModel = vm,
                    transactionId = id,
                    currencySymbol = profile?.currencySymbol ?: "\u20B9",
                    onSaved = { navController.popBackStack() },
                    onDeleted = { navController.popBackStack() }
                )
            }

            composable(Screen.Cards.route) {
                val vm: CardViewModel = viewModel(factory = CardViewModel.Factory(repository))
                CardsScreen(viewModel = vm, onAddCardClick = { navController.navigate(Screen.AddCard.route) })
            }

            composable(Screen.AddCard.route) {
                val vm: CardViewModel = viewModel(factory = CardViewModel.Factory(repository))
                AddCardScreen(viewModel = vm, onSaved = { navController.popBackStack() })
            }

            composable(Screen.Profile.route) {
                val vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(repository))
                ProfileScreen(viewModel = vm)
            }
        }
    }

    // Handle a cold-start deep link straight from a transaction notification
    androidx.compose.runtime.LaunchedEffect(deepLinkTransactionId) {
        deepLinkTransactionId?.let {
            navController.navigate(Screen.EditTransaction.buildRoute(it))
        }
    }
}
