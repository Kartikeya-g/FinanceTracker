package com.financetracker.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.financetracker.app.MainActivity
import com.financetracker.app.R
import com.financetracker.app.data.model.Transaction
import com.financetracker.app.data.model.TransactionType

object NotificationHelper {

    const val CHANNEL_ID = "transaction_alerts"
    const val EXTRA_TRANSACTION_ID = "extra_transaction_id"
    const val EXTRA_NAVIGATE_TO_EDIT = "extra_navigate_to_edit"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Transaction Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies you every time a payment or credit is detected"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showTransactionNotification(context: Context, transaction: Transaction, currencySymbol: String) {
        val isExpense = transaction.type == TransactionType.EXPENSE
        val title = if (isExpense) "Payment made" else "Money received"
        val amountText = "$currencySymbol${"%.2f".format(transaction.amount)}"
        val content = "$amountText \u2022 ${transaction.title}. Tap to edit details."

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TRANSACTION_ID, transaction.id)
            putExtra(EXTRA_NAVIGATE_TO_EDIT, true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            transaction.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val icon = if (isExpense) R.drawable.ic_expense else R.drawable.ic_credit

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(transaction.id.hashCode(), notification)
    }
}
