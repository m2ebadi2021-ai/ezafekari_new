package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.model.FinancialLogEntity
import com.example.data.model.LoanEntity
import com.example.data.model.SalarySettingsEntity
import com.example.data.model.WorkLogEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val database: AppDatabase) {
    private val workLogDao = database.workLogDao()
    private val financialLogDao = database.financialLogDao()
    private val loanDao = database.loanDao()
    private val salarySettingsDao = database.salarySettingsDao()

    // Work Logs
    val allWorkLogs: Flow<List<WorkLogEntity>> = workLogDao.getAllLogs()

    fun getWorkLogsByMonth(monthPrefix: String): Flow<List<WorkLogEntity>> {
        return workLogDao.getLogsByMonth(monthPrefix)
    }

    suspend fun insertWorkLog(log: WorkLogEntity): Long {
        return workLogDao.insertLog(log)
    }

    suspend fun updateWorkLog(log: WorkLogEntity) {
        workLogDao.updateLog(log)
    }

    suspend fun deleteWorkLog(log: WorkLogEntity) {
        workLogDao.deleteLog(log)
    }

    suspend fun deleteWorkLogById(id: Long) {
        workLogDao.deleteById(id)
    }

    // Financial Logs
    val allFinancialLogs: Flow<List<FinancialLogEntity>> = financialLogDao.getAllFinancialLogs()

    fun getFinancialLogsByMonth(monthPrefix: String): Flow<List<FinancialLogEntity>> {
        return financialLogDao.getFinancialLogsByMonth(monthPrefix)
    }

    suspend fun insertFinancialLog(log: FinancialLogEntity): Long {
        return financialLogDao.insertFinancialLog(log)
    }

    suspend fun deleteFinancialLog(log: FinancialLogEntity) {
        financialLogDao.deleteFinancialLog(log)
    }

    // Loans
    val allLoans: Flow<List<LoanEntity>> = loanDao.getAllLoans()

    suspend fun insertLoan(loan: LoanEntity): Long {
        return loanDao.insertLoan(loan)
    }

    suspend fun updateLoan(loan: LoanEntity) {
        loanDao.updateLoan(loan)
    }

    suspend fun deleteLoan(loan: LoanEntity) {
        loanDao.deleteLoan(loan)
    }

    // Salary Settings
    val salarySettings: Flow<SalarySettingsEntity?> = salarySettingsDao.getSettings()

    suspend fun getSettingsDirect(): SalarySettingsEntity {
        return salarySettingsDao.getSettingsDirect() ?: SalarySettingsEntity()
    }

    suspend fun saveSalarySettings(settings: SalarySettingsEntity) {
        salarySettingsDao.saveSettings(settings)
    }

    // Backup & Restore helpers
    suspend fun getAllWorkLogsList(): List<WorkLogEntity> = workLogDao.getAllLogsList()
    suspend fun getAllFinancialLogsList(): List<FinancialLogEntity> = financialLogDao.getAllFinancialLogsList()
    suspend fun getAllLoansList(): List<LoanEntity> = loanDao.getAllLoansList()

    suspend fun restoreDatabase(
        settings: SalarySettingsEntity?,
        workLogs: List<WorkLogEntity>,
        financialLogs: List<FinancialLogEntity>,
        loans: List<LoanEntity>,
        clearExisting: Boolean = true
    ) {
        if (clearExisting) {
            workLogDao.deleteAllLogs()
            financialLogDao.deleteAllFinancialLogs()
            loanDao.deleteAllLoans()
        }
        if (workLogs.isNotEmpty()) {
            workLogDao.insertAllLogs(workLogs)
        }
        if (financialLogs.isNotEmpty()) {
            financialLogDao.insertAllFinancialLogs(financialLogs)
        }
        if (loans.isNotEmpty()) {
            loanDao.insertAllLoans(loans)
        }
        if (settings != null) {
            salarySettingsDao.saveSettings(settings)
        }
    }
}

