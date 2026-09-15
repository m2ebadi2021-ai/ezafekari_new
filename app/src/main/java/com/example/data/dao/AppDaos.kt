package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FinancialLogEntity
import com.example.data.model.LoanEntity
import com.example.data.model.SalarySettingsEntity
import com.example.data.model.WorkLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkLogDao {
    @Query("SELECT * FROM work_logs ORDER BY date DESC, timeFrom DESC")
    fun getAllLogs(): Flow<List<WorkLogEntity>>

    @Query("SELECT * FROM work_logs WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC, timeFrom DESC")
    fun getLogsByMonth(monthPrefix: String): Flow<List<WorkLogEntity>>

    @Query("SELECT * FROM work_logs WHERE type = :type ORDER BY date DESC, timeFrom DESC")
    fun getLogsByType(type: String): Flow<List<WorkLogEntity>>

    @Query("SELECT * FROM work_logs WHERE type = :type AND date LIKE :monthPrefix || '%' ORDER BY date DESC, timeFrom DESC")
    fun getLogsByTypeAndMonth(type: String, monthPrefix: String): Flow<List<WorkLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WorkLogEntity): Long

    @Update
    suspend fun updateLog(log: WorkLogEntity)

    @Delete
    suspend fun deleteLog(log: WorkLogEntity)

    @Query("DELETE FROM work_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM work_logs")
    suspend fun getAllLogsList(): List<WorkLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogs(logs: List<WorkLogEntity>)

    @Query("DELETE FROM work_logs")
    suspend fun deleteAllLogs()
}

@Dao
interface FinancialLogDao {
    @Query("SELECT * FROM financial_logs ORDER BY date DESC, id DESC")
    fun getAllFinancialLogs(): Flow<List<FinancialLogEntity>>

    @Query("SELECT * FROM financial_logs WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC, id DESC")
    fun getFinancialLogsByMonth(monthPrefix: String): Flow<List<FinancialLogEntity>>

    @Query("SELECT * FROM financial_logs WHERE type = :type AND date LIKE :monthPrefix || '%'")
    fun getFinancialLogsByTypeAndMonth(type: String, monthPrefix: String): Flow<List<FinancialLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialLog(log: FinancialLogEntity): Long

    @Delete
    suspend fun deleteFinancialLog(log: FinancialLogEntity)

    @Query("DELETE FROM financial_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM financial_logs")
    suspend fun getAllFinancialLogsList(): List<FinancialLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFinancialLogs(logs: List<FinancialLogEntity>)

    @Query("DELETE FROM financial_logs")
    suspend fun deleteAllFinancialLogs()
}

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY id DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)

    @Query("SELECT * FROM loans")
    suspend fun getAllLoansList(): List<LoanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLoans(loans: List<LoanEntity>)

    @Query("DELETE FROM loans")
    suspend fun deleteAllLoans()
}

@Dao
interface SalarySettingsDao {
    @Query("SELECT * FROM salary_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<SalarySettingsEntity?>

    @Query("SELECT * FROM salary_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): SalarySettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SalarySettingsEntity)
}
