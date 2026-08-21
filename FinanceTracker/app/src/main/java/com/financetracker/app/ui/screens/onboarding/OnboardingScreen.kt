package com.financetracker.app.ui.screens.onboarding

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val context = LocalContext.current

    val permissions = buildList {
        add(Manifest.permission.RECEIVE_SMS)
        add(Manifest.permission.READ_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    val permissionState = rememberMultiplePermissionsState(permissions)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Let's set up automatic tracking", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "To detect payments the moment they happen, Finance Tracker needs permission " +
                "to read SMS (for bank alerts) and to show notifications. Your messages never " +
                "leave your device.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
        )

        if (permissionState.allPermissionsGranted) {
            Text(
                text = "\u2713 SMS & notification permissions granted",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Button(
                onClick = { permissionState.launchMultiplePermissionRequest() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Grant SMS & Notification Access") }
        }

        Text(
            text = "For UPI apps that alert only via notification (Google Pay, PhonePe), also " +
                "enable Notification Access below.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        OutlinedButton(
            onClick = {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Open Notification Access Settings") }

        Button(
            onClick = onFinished,
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) { Text(if (permissionState.allPermissionsGranted) "Continue" else "Skip for now") }
    }
}
