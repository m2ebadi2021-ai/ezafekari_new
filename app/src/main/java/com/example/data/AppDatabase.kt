package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.FinancialLogDao
import com.example.data.dao.LoanDao
import com.example.data.dao.SalarySettingsDao
import com.example.data.dao.WorkLogDao
import com.example.data.model.FinancialLogEntity
import com.example.data.model.LoanEntity
import com.example.data.model.SalarySettingsEntity
import com.example.data.model.WorkLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WorkLogEntity::class,
        FinancialLogEntity::class,
        LoanEntity::class,
        SalarySettingsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workLogDao(): WorkLogDao
    abstract fun financialLogDao(): FinancialLogDao
    abstract fun loanDao(): LoanDao
    abstract fun salarySettingsDao(): SalarySettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ezafekari_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default salary settings for year 1405
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.salarySettingsDao()?.saveSettings(SalarySettingsEntity())
                            }
                        }
                    }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
