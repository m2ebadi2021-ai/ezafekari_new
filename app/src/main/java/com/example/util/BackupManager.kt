package com.example.util

import com.example.data.model.FinancialLogEntity
import com.example.data.model.LoanEntity
import com.example.data.model.SalarySettingsEntity
import com.example.data.model.WorkLogEntity
import org.json.JSONArray
import org.json.JSONObject

data class BackupData(
    val exportDate: String,
    val appVersion: String,
    val settings: SalarySettingsEntity?,
    val workLogs: List<WorkLogEntity>,
    val financialLogs: List<FinancialLogEntity>,
    val loans: List<LoanEntity>
)

object BackupManager {

    fun createBackupJson(
        settings: SalarySettingsEntity,
        workLogs: List<WorkLogEntity>,
        financialLogs: List<FinancialLogEntity>,
        loans: List<LoanEntity>
    ): String {
        val root = JSONObject()
        root.put("version", "1405.1")
        root.put("export_date", PersianDateUtil.today().format())
        root.put("app_title", "سامانه حقوق و اضافه‌کاری ۱۴۰۵")

        // Settings JSON
        val settingsObj = JSONObject().apply {
            put("id", settings.id)
            put("userName", settings.userName)
            put("personnelCode", settings.personnelCode)
            put("jobTitle", settings.jobTitle)
            put("companyName", settings.companyName)
            put("baseSalary", settings.baseSalary)
            put("housingAllowance", settings.housingAllowance)
            put("workerCoupon", settings.workerCoupon)
            put("maritalAllowance", settings.maritalAllowance)
            put("childCount", settings.childCount)
            put("childAllowancePerChild", settings.childAllowancePerChild)
            put("seniorityAllowance", settings.seniorityAllowance)
            put("shiftPercentage", settings.shiftPercentage)
            put("technicalBonus", settings.technicalBonus)
            put("responsibilityBonus", settings.responsibilityBonus)
            put("supervisoryBonus", settings.supervisoryBonus)
            put("otherBonuses", settings.otherBonuses)
            put("otherDeductions", settings.otherDeductions)
            put("supplementaryInsurance", settings.supplementaryInsurance)
            put("workerInsurancePercent", settings.workerInsurancePercent)
            put("isAutoInsurance", settings.isAutoInsurance)
            put("isAutoTax", settings.isAutoTax)
            put("manualTaxAmount", settings.manualTaxAmount)
            put("dailyWorkMinutes", settings.dailyWorkMinutes)
            put("hourlyBaseDivisor", settings.hourlyBaseDivisor)
            put("regularOvertimeMultiplier", settings.regularOvertimeMultiplier)
            put("specialOvertimeMultiplier", settings.specialOvertimeMultiplier)
            put("themeMode", settings.themeMode)
            put("themeColor", settings.themeColor)
        }
        root.put("salary_settings", settingsObj)

        // Work Logs JSON Array
        val workLogsArr = JSONArray()
        for (log in workLogs) {
            val obj = JSONObject().apply {
                put("id", log.id)
                put("type", log.type)
                put("subType", log.subType)
                put("date", log.date)
                put("timeFrom", log.timeFrom)
                put("timeTo", log.timeTo)
                put("durationMinutes", log.durationMinutes)
                put("note", log.note)
            }
            workLogsArr.put(obj)
        }
        root.put("work_logs", workLogsArr)

        // Financial Logs JSON Array
        val finArr = JSONArray()
        for (f in financialLogs) {
            val obj = JSONObject().apply {
                put("id", f.id)
                put("type", f.type)
                put("title", f.title)
                put("amount", f.amount)
                put("date", f.date)
                put("note", f.note)
            }
            finArr.put(obj)
        }
        root.put("financial_logs", finArr)

        // Loans JSON Array
        val loansArr = JSONArray()
        for (l in loans) {
            val obj = JSONObject().apply {
                put("id", l.id)
                put("title", l.title)
                put("totalAmount", l.totalAmount)
                put("installmentCount", l.installmentCount)
                put("paidInstallments", l.paidInstallments)
                put("interestRate", l.interestRate)
                put("monthlyPayment", l.monthlyPayment)
                put("startDate", l.startDate)
                put("note", l.note)
            }
            loansArr.put(obj)
        }
        root.put("loans", loansArr)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): Result<BackupData> {
        return try {
            val root = JSONObject(jsonString.trim())
            val exportDate = root.optString("export_date", "")
            val version = root.optString("version", "1405")

            // Parse settings
            val settings = if (root.has("salary_settings")) {
                val s = root.getJSONObject("salary_settings")
                SalarySettingsEntity(
                    id = 1,
                    userName = s.optString("userName", "کاربر گرامی"),
                    personnelCode = s.optString("personnelCode", ""),
                    jobTitle = s.optString("jobTitle", "پرسنل شاغل"),
                    companyName = s.optString("companyName", "شرکت"),
                    baseSalary = s.optLong("baseSalary", 14_949_900L),
                    housingAllowance = s.optLong("housingAllowance", 1_500_000L),
                    workerCoupon = s.optLong("workerCoupon", 2_800_000L),
                    maritalAllowance = s.optLong("maritalAllowance", 750_000L),
                    childCount = s.optInt("childCount", 0),
                    childAllowancePerChild = s.optLong("childAllowancePerChild", 1_495_000L),
                    seniorityAllowance = s.optLong("seniorityAllowance", 360_000L),
                    shiftPercentage = s.optDouble("shiftPercentage", 0.0),
                    technicalBonus = s.optLong("technicalBonus", 0L),
                    responsibilityBonus = s.optLong("responsibilityBonus", 0L),
                    supervisoryBonus = s.optLong("supervisoryBonus", 0L),
                    otherBonuses = s.optLong("otherBonuses", 0L),
                    otherDeductions = s.optLong("otherDeductions", 0L),
                    supplementaryInsurance = s.optLong("supplementaryInsurance", 0L),
                    workerInsurancePercent = s.optDouble("workerInsurancePercent", 7.0),
                    isAutoInsurance = s.optBoolean("isAutoInsurance", true),
                    isAutoTax = s.optBoolean("isAutoTax", true),
                    manualTaxAmount = s.optLong("manualTaxAmount", 0L),
                    dailyWorkMinutes = s.optInt("dailyWorkMinutes", 440),
                    hourlyBaseDivisor = s.optDouble("hourlyBaseDivisor", 220.0),
                    regularOvertimeMultiplier = s.optDouble("regularOvertimeMultiplier", 1.4),
                    specialOvertimeMultiplier = s.optDouble("specialOvertimeMultiplier", 1.8),
                    themeMode = s.optString("themeMode", "SYSTEM"),
                    themeColor = s.optString("themeColor", "INDIGO")
                )
            } else null

            // Parse work logs
            val workLogs = mutableListOf<WorkLogEntity>()
            if (root.has("work_logs")) {
                val arr = root.getJSONArray("work_logs")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    workLogs.add(
                        WorkLogEntity(
                            id = 0, // auto-generate primary keys upon restore to prevent collisions
                            type = obj.optString("type", "OVERTIME"),
                            subType = obj.optString("subType", "REGULAR"),
                            date = obj.optString("date", ""),
                            timeFrom = obj.optString("timeFrom", "00:00"),
                            timeTo = obj.optString("timeTo", "00:00"),
                            durationMinutes = obj.optInt("durationMinutes", 0),
                            note = obj.optString("note", "")
                        )
                    )
                }
            }

            // Parse financial logs
            val financialLogs = mutableListOf<FinancialLogEntity>()
            if (root.has("financial_logs")) {
                val arr = root.getJSONArray("financial_logs")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    financialLogs.add(
                        FinancialLogEntity(
                            id = 0,
                            type = obj.optString("type", "MOSAEDE"),
                            title = obj.optString("title", "مساعده"),
                            amount = obj.optLong("amount", 0L),
                            date = obj.optString("date", ""),
                            note = obj.optString("note", "")
                        )
                    )
                }
            }

            // Parse loans
            val loans = mutableListOf<LoanEntity>()
            if (root.has("loans")) {
                val arr = root.getJSONArray("loans")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    loans.add(
                        LoanEntity(
                            id = 0,
                            title = obj.optString("title", "وام"),
                            totalAmount = obj.optLong("totalAmount", 0L),
                            installmentCount = obj.optInt("installmentCount", 1),
                            paidInstallments = obj.optInt("paidInstallments", 0),
                            interestRate = obj.optDouble("interestRate", 0.0),
                            monthlyPayment = obj.optLong("monthlyPayment", 0L),
                            startDate = obj.optString("startDate", ""),
                            note = obj.optString("note", "")
                        )
                    )
                }
            }

            Result.success(
                BackupData(
                    exportDate = exportDate,
                    appVersion = version,
                    settings = settings,
                    workLogs = workLogs,
                    financialLogs = financialLogs,
                    loans = loans
                )
            )
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("فرمت فایل یا کد بکاپ نامعتبر است: ${e.localizedMessage}"))
        }
    }
}
