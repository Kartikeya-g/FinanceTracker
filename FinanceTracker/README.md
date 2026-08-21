# Finance Tracker (Android)

A personal finance tracker: add your debit/credit cards, auto-detect payments and
credits from bank SMS/UPI notifications, get an instant popup for every
transaction, and edit/categorize everything afterward.

## What's included

- **Profile** — name, email, currency, monthly budget, toggles for SMS auto-detect
  and notifications.
- **Cards** — add debit/credit cards (bank, nickname, network, last 4 digits only —
  full card numbers/CVV are never stored or requested).
- **Auto transaction detection**
  - `sms/SmsReceiver.kt` listens for incoming SMS.
  - `sms/SmsParser.kt` regex-parses the message for amount, debit/credit keywords,
    last-4 account digits, and balance.
  - `sms/UpiNotificationListener.kt` additionally reads notifications from Google Pay,
    PhonePe, Paytm, and BHIM for UPI payments that only ever show as an app
    notification (no SMS).
  - A detected transaction is matched to a saved card (by last-4 digits) and a
    popup notification fires immediately (`notification/NotificationHelper.kt`).
    Tapping it opens the transaction pre-filled so you can rename/categorize it.
- **Manual transactions** — add anything the SMS parser misses (cash, etc.).
- **Dashboard** — total expenses, total credits, your cards, recent activity.
- **Edit screen** — rename the auto-generated title ("HDFC Bank" → "Groceries –
  Big Bazaar"), change category, add notes, or delete.

## Opening the project

1. Install [Android Studio](https://developer.android.com/studio) (Koala or newer).
2. `File > Open` and select the `FinanceTracker` folder.
3. Android Studio will offer to generate the Gradle wrapper jar automatically on
   first sync (the wrapper jar binary isn't included in this bundle — only
   `gradle-wrapper.properties`). If it doesn't prompt automatically, run:
   ```
   gradle wrapper --gradle-version 8.7
   ```
   from the project root once, using any local Gradle install, then re-open in
   Android Studio.
4. Let Gradle sync (it will download AndroidX/Compose/Room dependencies —
   needs internet access).
5. Run on a device or emulator with **API 26+**. SMS parsing only works on a
   real device (or an emulator with SMS forwarding) since emulators can't
   receive real bank SMS.

## First run

The app opens an onboarding screen requesting:
- `RECEIVE_SMS` / `READ_SMS` — to catch bank transaction alerts.
- `POST_NOTIFICATIONS` (Android 13+) — to show the transaction popup.
- **Notification Access** (separate system setting) — only needed if you want
  UPI-app-only transactions (no SMS) to be captured too.

Add your cards first (Cards tab) so incoming SMS can be matched to the right
account by last-4 digits.

## Customizing the SMS parser for your bank

Bank SMS formats vary. `SmsParser.kt` uses keyword + regex matching rather than
one rigid template, but if your bank's format doesn't parse correctly:

1. Send yourself a test transaction and copy the exact SMS text.
2. Open `sms/SmsParser.kt`.
3. Adjust `amountRegex`, `lastFourRegex`, `balanceRegex`, or the
   `debitKeywords`/`creditKeywords` lists to match your bank's wording.
4. `rawSmsBody` is stored on every auto-detected transaction, so you can always
   look back at the original message if something parsed incorrectly.

## Architecture

- **Kotlin + Jetpack Compose (Material3)**
- **Room** for local storage (transactions, cards, profile) — nothing leaves
  the device; there is no backend/server.
- **MVVM** — one ViewModel per screen, backed by a single `FinanceRepository`.
- **Navigation Compose** with a bottom nav bar (Home / Transactions / Cards / Profile).

## Known limitations / next steps

- SMS parsing is regex-based and tuned for common Indian bank formats
  (debited/credited/spent/received + `INR`/`Rs`/`₹` amount markers). Extend the
  regexes for other formats as needed.
- No cloud sync/backup beyond Android's own Auto Backup (local DB only).
- No charts yet — total expense/credit are shown as numbers on the dashboard;
  a category breakdown chart would be a natural next addition.
