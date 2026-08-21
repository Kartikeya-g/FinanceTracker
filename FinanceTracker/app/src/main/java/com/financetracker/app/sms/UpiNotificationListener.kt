package com.financetracker.app.sms

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.financetracker.app.FinanceApplication
import com.financetracker.app.data.model.Transaction
import com.financetracker.app.data.model.TransactionSource
import com.financetracker.app.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Reads notifications posted by UPI/payment apps (Google Pay, PhonePe, Paytm, etc.)
 * so transactions made purely through UPI - which sometimes arrive as an app
 * notification before/instead of an SMS - still get captured automatically.
 *
 * Requires the user to grant "Notification access" in system settings
 * (Settings > Apps > Special app access > Notification access).
 */
class UpiNotificationListener : NotificationListenerService() {

    private val trackedPackages = setOf(
        "com.google.android.apps.nbu.paisa.user", // Google Pay
        "com.phonepe.app",
        "net.one97.paytm",
        "in.org.npci.upiapp" // BHIM
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName !in trackedPackages) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val body = "$title $text"

        val parsed = SmsParser.parse(body) ?: return

        val app = applicationContext as FinanceApplication
        val repository = app.repository

        CoroutineScope(Dispatchers.IO).launch {
            val matchedCard = parsed.lastFourDigits?.let { repository.findCardByLastFour(it) }

            val transaction = Transaction(
                title = parsed.merchant ?: title,
                merchant = parsed.merchant,
                amount = parsed.amount,
                type = parsed.type,
                cardId = matchedCard?.id,
                source = TransactionSource.NOTIFICATION_AUTO,
                rawSmsBody = body,
                balanceAfter = parsed.balanceAfter
            )
            repository.addTransaction(transaction)

            val profile = repository.getProfile()
            if (profile?.notificationsEnabled != false) {
                NotificationHelper.showTransactionNotification(applicationContext, transaction, profile?.currencySymbol ?: "₹")
            }
        }
    }
}
