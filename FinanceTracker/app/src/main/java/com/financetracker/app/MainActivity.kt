package com.financetracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.financetracker.app.notification.NotificationHelper
import com.financetracker.app.ui.navigation.FinanceNavHost
import com.financetracker.app.ui.navigation.Screen
import com.financetracker.app.ui.theme.FinanceTrackerTheme
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as FinanceApplication
        val repository = app.repository

        val deepLinkId = intent.getStringExtra(NotificationHelper.EXTRA_TRANSACTION_ID)

        // Decide the start destination: first run -> onboarding, otherwise dashboard.
        val hasProfile = runBlocking { repository.getProfile() != null }
        val startDestination = if (hasProfile) Screen.Dashboard.route else Screen.Onboarding.route

        setContent {
            FinanceTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FinanceNavHost(
                        repository = repository,
                        startDestination = startDestination,
                        deepLinkTransactionId = deepLinkId
                    )
                }
            }
        }
    }
}
