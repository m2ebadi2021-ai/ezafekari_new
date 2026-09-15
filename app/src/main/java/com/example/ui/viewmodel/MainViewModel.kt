package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.FinancialLogEntity
import com.example.data.model.LoanEntity
import com.example.data.model.SalarySettingsEntity
import com.example.data.model.WorkLogEntity
import com.example.data.repository.AppRepository
import com.example.util.PersianDateUtil
import com.example.util.SalaryBreakdown
import com.example.util.SalaryCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    val todayDate = PersianDateUtil.today()

    private val _selectedYear = MutableStateFlow(todayDate.year)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow(todayDate.month)
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private val _selectedMonthPrefix = MutableStateFlow(
        String.format(Locale.US, "%04d/%02d", todayDate.year, todayDate.month)
    )
    val selectedMonthPrefix: StateFlow<String> = _selectedMonthPrefix.asStateFlow()

    // Status message for Snackbars
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database)
    }

    val salarySettings: StateFlow<SalarySettingsEntity> = repository.salarySettings
        .combine(MutableStateFlow(SalarySettingsEntity())) { settings, default ->
            settings ?: default
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SalarySettingsEntity()
        )

    val allLoans: StateFlow<List<LoanEntity>> = repository.allLoans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMonthWorkLogs: StateFlow<List<WorkLogEntity>> = _selectedMonthPrefix
        .flatMapLatest { prefix -> repository.getWorkLogsByMonth(prefix) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMonthFinancialLogs: StateFlow<List<FinancialLogEntity>> = _selectedMonthPrefix
        .flatMapLatest { prefix -> repository.getFinancialLogsByMonth(prefix) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allWorkLogs: StateFlow<List<WorkLogEntity>> = repository.allWorkLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Month Salary Breakdown
    val currentSalaryBreakdown: StateFlow<SalaryBreakdown> = combine(
        salarySettings,
        currentMonthWorkLogs,
        currentMonthFinancialLogs,
        allLoans
    ) { settings, workLogs, finLogs, loans ->
        var regularOvertimeMins = 0
        var specialOvertimeMins = 0

        for (log in workLogs) {
            when (log.type) {
                "EZAFEKARI" -> {
                    if (log.subType == "SPECIAL") {
                        specialOvertimeMins += log.durationMinutes
                    } else {
                        regularOvertimeMins += log.durationMinutes
                    }
                }
                "TARADOD", "HOZOOR", "KAR" -> {
                    // اگر تردد بیشتر از ساعت کاری موظف باشد، مازاد آن اضافه کار در نظر گرفته می‌شود
                    if (settings.autoOvertimeOnExcessWork && settings.dailyWorkMinutes > 0) {
                        val excess = log.durationMinutes - settings.dailyWorkMinutes
                        if (excess > 0) {
                            regularOvertimeMins += excess
                        }
                    }
                }
            }
        }

        // تفکیک موارد مالی ثبت‌شده: پاداش‌ها، مزایا، مساعده و کسورات
        var advancesTotal = 0L
        var bonusesTotal = 0L
        var otherAllowancesTotal = 0L
        var otherFinancialDeductionsTotal = 0L

        for (fin in finLogs) {
            when (fin.type) {
                "MOSAEDE" -> advancesTotal += fin.amount
                "PADASH" -> bonusesTotal += fin.amount
                "AYAB_ZAHAB", "SAYER_MAZAYA" -> otherAllowancesTotal += fin.amount
                "FOOD", "SAYER_KOSORAT", "JARIME" -> otherFinancialDeductionsTotal += fin.amount
                else -> {
                    if (fin.amount > 0) {
                        otherFinancialDeductionsTotal += fin.amount
                    }
                }
            }
        }

        var totalLoanPayments = 0L
        for (loan in loans) {
            if (loan.paidInstallments < loan.installmentCount) {
                totalLoanPayments += loan.monthlyPayment
            }
        }

        // Days in month
        val daysCount = PersianDateUtil.daysInPersianMonth(_selectedYear.value, _selectedMonth.value)

        SalaryCalculator.calculateMonthlySalary(
            settings = settings,
            workedDays = daysCount,
            regularOvertimeMinutes = regularOvertimeMins,
            specialOvertimeMinutes = specialOvertimeMins,
            advanceSalary = advancesTotal,
            loanInstallments = totalLoanPayments,
            registeredBonuses = bonusesTotal,
            registeredOtherAllowances = otherAllowancesTotal,
            registeredFinancialDeductions = otherFinancialDeductionsTotal
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SalaryCalculator.calculateMonthlySalary(SalarySettingsEntity())
    )

    fun setYearAndMonth(year: Int, month: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
        _selectedMonthPrefix.value = String.format(Locale.US, "%04d/%02d", year, month)
    }

    fun nextMonth() {
        var y = _selectedYear.value
        var m = _selectedMonth.value + 1
        if (m > 12) {
            m = 1
            y += 1
        }
        setYearAndMonth(y, m)
    }

    fun prevMonth() {
        var y = _selectedYear.value
        var m = _selectedMonth.value - 1
        if (m < 1) {
            m = 12
            y -= 1
        }
        setYearAndMonth(y, m)
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    // Work Log actions
    fun addWorkLog(
        type: String,
        subType: String,
        date: String,
        timeFrom: String,
        timeTo: String,
        durationMinutes: Int,
        note: String
    ) {
        viewModelScope.launch {
            val entity = WorkLogEntity(
                type = type,
                subType = subType,
                date = date,
                timeFrom = timeFrom,
                timeTo = timeTo,
                durationMinutes = durationMinutes,
                note = note
            )
            repository.insertWorkLog(entity)
            _userMessage.value = "مورد با موفقیت ثبت شد"
        }
    }

    fun updateWorkLog(log: WorkLogEntity) {
        viewModelScope.launch {
            repository.updateWorkLog(log)
            _userMessage.value = "ویرایش با موفقیت ذخیره شد"
        }
    }

    fun deleteWorkLog(log: WorkLogEntity) {
        viewModelScope.launch {
            repository.deleteWorkLog(log)
            _userMessage.value = "آیتم با موفقیت حذف شد"
        }
    }

    // Quick One-Tap Check-In / Check-Out for attendance
    fun quickCheckIn() {
        viewModelScope.launch {
            val date = PersianDateUtil.today().format()
            val time = PersianDateUtil.currentTimeString()
            val entity = WorkLogEntity(
                type = "TARADOD",
                subType = "IN",
                date = date,
                timeFrom = time,
                timeTo = time,
                durationMinutes = 0,
                note = "ورود سریع با یک لمس"
            )
            repository.insertWorkLog(entity)
            _userMessage.value = "ساعت ورود ($time) با موفقیت ثبت شد"
        }
    }

    fun quickCheckOut() {
        viewModelScope.launch {
            val date = PersianDateUtil.today().format()
            val time = PersianDateUtil.currentTimeString()
            val entity = WorkLogEntity(
                type = "TARADOD",
                subType = "OUT",
                date = date,
                timeFrom = time,
                timeTo = time,
                durationMinutes = 0,
                note = "خروج سریع با یک لمس"
            )
            repository.insertWorkLog(entity)
            _userMessage.value = "ساعت خروج ($time) با موفقیت ثبت شد"
        }
    }

    // Financial Log actions
    fun addFinancialLog(
        type: String,
        title: String,
        amount: Long,
        date: String,
        note: String
    ) {
        viewModelScope.launch {
            val entity = FinancialLogEntity(
                type = type,
                title = title,
                amount = amount,
                date = date,
                note = note
            )
            repository.insertFinancialLog(entity)
            _userMessage.value = "مورد مالی با موفقیت ثبت شد"
        }
    }

    fun deleteFinancialLog(log: FinancialLogEntity) {
        viewModelScope.launch {
            repository.deleteFinancialLog(log)
            _userMessage.value = "مورد مالی حذف شد"
        }
    }

    // Loan actions
    fun addLoan(
        title: String,
        totalAmount: Long,
        installmentCount: Int,
        interestRate: Double,
        monthlyPayment: Long,
        startDate: String,
        note: String
    ) {
        viewModelScope.launch {
            val entity = LoanEntity(
                title = title,
                totalAmount = totalAmount,
                installmentCount = installmentCount,
                interestRate = interestRate,
                monthlyPayment = monthlyPayment,
                paidInstallments = 0,
                startDate = startDate,
                note = note
            )
            repository.insertLoan(entity)
            _userMessage.value = "وام با موفقیت ثبت شد"
        }
    }

    fun markLoanInstallmentPaid(loan: LoanEntity) {
        viewModelScope.launch {
            if (loan.paidInstallments < loan.installmentCount) {
                repository.updateLoan(loan.copy(paidInstallments = loan.paidInstallments + 1))
                _userMessage.value = "یک قسط ثبت پرداخت شد"
            }
        }
    }

    fun deleteLoan(loan: LoanEntity) {
        viewModelScope.launch {
            repository.deleteLoan(loan)
            _userMessage.value = "وام با موفقیت حذف شد"
        }
    }

    // Settings actions
    fun saveSalarySettings(settings: SalarySettingsEntity) {
        viewModelScope.launch {
            repository.saveSalarySettings(settings)
            _userMessage.value = "تنظیمات و احکام حقوقی ذخیره شدند"
        }
    }

    // Daily leave date range registration (از روز تا روز)
    fun addDailyLeaveRange(
        subType: String,
        dates: List<String>,
        dailyMinutes: Int = 440,
        note: String
    ) {
        viewModelScope.launch {
            val count = dates.size
            for ((index, d) in dates.withIndex()) {
                val dayNote = if (count > 1) {
                    if (note.isNotBlank()) "$note (روز ${PersianDateUtil.toFaDigits(index + 1)} از ${PersianDateUtil.toFaDigits(count)})"
                    else "مرخصی روزانه (روز ${PersianDateUtil.toFaDigits(index + 1)} از ${PersianDateUtil.toFaDigits(count)})"
                } else {
                    note.ifBlank { "مرخصی روزانه" }
                }

                val entity = WorkLogEntity(
                    type = "MORAKHASI",
                    subType = subType,
                    date = d,
                    timeFrom = "08:00",
                    timeTo = "15:20",
                    durationMinutes = dailyMinutes,
                    note = dayNote
                )
                repository.insertWorkLog(entity)
            }
            _userMessage.value = if (count > 1) {
                "مرخصی ${PersianDateUtil.toFaDigits(count)} روزه با موفقیت ثبت گردید"
            } else {
                "مرخصی روزانه با موفقیت ثبت گردید"
            }
        }
    }

    // Reset settings to official 1405 Labor Law provisions
    fun resetTo1405LaborLawDefaults() {
        viewModelScope.launch {
            val current = repository.getSettingsDirect()
            val updated = current.copy(
                baseSalary = SalarySettingsEntity.LAW_1405_DEFAULTS.baseSalary,
                housingAllowance = SalarySettingsEntity.LAW_1405_DEFAULTS.housingAllowance,
                workerCoupon = SalarySettingsEntity.LAW_1405_DEFAULTS.workerCoupon,
                maritalAllowance = SalarySettingsEntity.LAW_1405_DEFAULTS.maritalAllowance,
                childAllowancePerChild = SalarySettingsEntity.LAW_1405_DEFAULTS.childAllowancePerChild,
                seniorityAllowance = SalarySettingsEntity.LAW_1405_DEFAULTS.seniorityAllowance,
                hourlyBaseDivisor = 220.0,
                regularOvertimeMultiplier = 1.4,
                specialOvertimeMultiplier = 1.8,
                workerInsurancePercent = 7.0,
                isAutoInsurance = true,
                isAutoTax = true,
                dailyWorkMinutes = 440,
                autoOvertimeOnExcessWork = true,
                standardShiftStartTime = "08:00",
                standardShiftEndTime = "16:20"
            )
            repository.saveSalarySettings(updated)
            _userMessage.value = "احکام قانون کار سال ۱۴۰۵ با موفقیت بارگذاری شدند"
        }
    }

    // Backup & Restore
    suspend fun generateBackupJson(): String {
        val settings = repository.getSettingsDirect()
        val workLogs = repository.getAllWorkLogsList()
        val financial = repository.getAllFinancialLogsList()
        val loans = repository.getAllLoansList()
        return com.example.util.BackupManager.createBackupJson(settings, workLogs, financial, loans)
    }

    fun restoreBackup(
        backupJson: String,
        onComplete: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch {
            val parseResult = com.example.util.BackupManager.parseBackupJson(backupJson)
            if (parseResult.isSuccess) {
                val data = parseResult.getOrThrow()
                repository.restoreDatabase(
                    settings = data.settings,
                    workLogs = data.workLogs,
                    financialLogs = data.financialLogs,
                    loans = data.loans,
                    clearExisting = true
                )
                val msg = "بازیابی با موفقیت انجام شد (${PersianDateUtil.toFaDigits(data.workLogs.size)} کارکرد، ${PersianDateUtil.toFaDigits(data.financialLogs.size)} تراکنش مالی)"
                _userMessage.value = msg
                onComplete(true, msg)
            } else {
                val err = parseResult.exceptionOrNull()?.message ?: "خطا در پردازش اطلاعات بکاپ"
                _userMessage.value = err
                onComplete(false, err)
            }
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.restoreDatabase(
                settings = null,
                workLogs = emptyList(),
                financialLogs = emptyList(),
                loans = emptyList(),
                clearExisting = true
            )
            _userMessage.value = "تمام اطلاعات کارکرد و تراکنش‌ها پاکسازی شدند"
        }
    }
}

