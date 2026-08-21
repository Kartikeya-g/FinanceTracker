package com.financetracker.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.financetracker.app.FinanceApplication
import com.financetracker.app.data.model.Transaction
import com.financetracker.app.data.model.TransactionSource
import com.financetracker.app.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return

        val bundle = intent.extras ?: return
        @Suppress("UNCHECKED_CAST")
        val pdus = bundle.get("pdus") as? Array<Any> ?: return
        val format = bundle.getString("format")

        val fullBody = StringBuilder()
        var sender = ""
        for (pdu in pdus) {
            val sms = SmsMessage.createFromPdu(pdu as ByteArray, format)
            fullBody.append(sms.messageBody)
            sender = sms.originatingAddress ?: sender
        }
        val body = fullBody.toString()

        // Only bother parsing messages that look like they're from a bank/VK sender
        // (e.g. "VM-HDFCBK", "AX-ICICIB"), skipping obvious personal numbers.
        if (!looksLikeBankSender(sender) && SmsParser.parse(body) == null) return

        val parsed = SmsParser.parse(body) ?: return

        val app = context.applicationContext as FinanceApplication
        val repository = app.repository

        CoroutineScope(Dispatchers.IO).launch {
            val matchedCard = parsed.lastFourDigits?.let { repository.findCardByLastFour(it) }

            val transaction = Transaction(
                title = parsed.merchant ?: "Bank Transaction",
                merchant = parsed.merchant,
                amount = parsed.amount,
                type = parsed.type,
                cardId = matchedCard?.id,
                source = TransactionSource.SMS_AUTO,
                rawSmsBody = body,
                balanceAfter = parsed.balanceAfter
            )
            repository.addTransaction(transaction)

            val profile = repository.getProfile()
            if (profile?.notificationsEnabled != false) {
                NotificationHelper.showTransactionNotification(context, transaction, profile?.currencySymbol ?: "₹")
            }
        }
    }

    private fun looksLikeBankSender(sender: String): Boolean {
        // Indian bank/DLT sender IDs are typically 6 chars like "VM-HDFCBK" or "AD-SBIINB"
        return Regex("^[A-Z]{2}-[A-Z]+$", RegexOption.IGNORE_CASE).containsMatchIn(sender) ||
            sender.any { it.isLetter() } && sender.length in 4..12 && !sender.startsWith("+")
    }
}
