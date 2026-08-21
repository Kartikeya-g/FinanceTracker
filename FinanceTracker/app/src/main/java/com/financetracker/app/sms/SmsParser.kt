package com.financetracker.app.sms

import com.financetracker.app.data.model.TransactionType

/**
 * Result of successfully parsing a bank/UPI SMS.
 */
data class ParsedTransaction(
    val amount: Double,
    val type: TransactionType,
    val merchant: String?,
    val lastFourDigits: String?,
    val balanceAfter: Double?
)

/**
 * Parses raw SMS bodies from Indian banks/UPI apps into structured
 * transaction data using regex. Bank SMS formats vary a lot, so this
 * matches on keywords ("debited"/"credited"/"spent"/"received") plus
 * common amount/account patterns rather than one rigid template.
 *
 * This is intentionally extensible: add new regexes as you encounter
 * SMS formats from banks not covered here.
 */
object SmsParser {

    private val amountRegex = Regex(
        """(?:INR|Rs\.?|₹)\s?([0-9]+(?:,[0-9]{2,3})*(?:\.[0-9]{1,2})?)""",
        RegexOption.IGNORE_CASE
    )

    private val lastFourRegex = Regex(
        """(?:a/c|acc(?:ount)?|card)\s?(?:no\.?)?\s?(?:x|X|\*)*([0-9]{4})\b""",
        RegexOption.IGNORE_CASE
    )

    private val balanceRegex = Regex(
        """(?:avl(?:\.|\s)?bal|available balance|bal)\s?:?\s?(?:INR|Rs\.?|₹)?\s?([0-9]+(?:,[0-9]{2,3})*(?:\.[0-9]{1,2})?)""",
        RegexOption.IGNORE_CASE
    )

    private val merchantRegex = Regex(
        """(?:at|to|towards|from)\s+([A-Za-z0-9 &._'\-]{2,30})""",
        RegexOption.IGNORE_CASE
    )

    private val debitKeywords = listOf(
        "debited", "spent", "paid", "purchase of", "withdrawn", "debit of"
    )
    private val creditKeywords = listOf(
        "credited", "received", "credit of", "deposited"
    )

    /**
     * Returns null if the message doesn't look like a transaction alert at all
     * (e.g. OTPs, promotions, balance-check replies with no amount).
     */
    fun parse(body: String): ParsedTransaction? {
        val lower = body.lowercase()

        // Skip obvious non-transaction messages
        if (lower.contains("otp") || lower.contains("one time password")) return null

        val amountMatch = amountRegex.find(body) ?: return null
        val amount = amountMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null

        val type = when {
            debitKeywords.any { lower.contains(it) } -> TransactionType.EXPENSE
            creditKeywords.any { lower.contains(it) } -> TransactionType.CREDIT
            else -> return null // no recognizable debit/credit keyword -> not a transaction alert
        }

        val lastFour = lastFourRegex.find(body)?.groupValues?.get(1)
        val balance = balanceRegex.find(body)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
        val merchant = merchantRegex.find(body)?.groupValues?.get(1)?.trim()

        return ParsedTransaction(
            amount = amount,
            type = type,
            merchant = merchant,
            lastFourDigits = lastFour,
            balanceAfter = balance
        )
    }
}
