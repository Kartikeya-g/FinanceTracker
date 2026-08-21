package com.financetracker.app

import android.app.Application
import com.financetracker.app.data.db.AppDatabase
import com.financetracker.app.data.repository.FinanceRepository
import com.financetracker.app.notification.NotificationHelper

class FinanceApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: FinanceRepository by lazy { FinanceRepository.getInstance(database) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
