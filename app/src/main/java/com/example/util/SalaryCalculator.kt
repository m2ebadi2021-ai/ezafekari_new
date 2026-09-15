package com.example.util

import com.example.data.model.SalarySettingsEntity
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

data class SalaryBreakdown(
    val workedDays: Int,
    val regularOvertimeMinutes: Int,
    val specialOvertimeMinutes: Int,
    val basePayEarned: Long,
    val seniorityEarned: Long,
    val housingAllowance: Long,
    val workerCoupon: Long,
    val maritalAllowance: Long,
    val childAllowance: Long,
    val shiftAllowance: Long,
    val otherBonuses: Long,
    val regularOvertimePay: Long,
    val specialOvertimePay: Long,
    val hourlyRate: Long,
    val regularOvertimeHourlyRate: Long,
    val specialOvertimeHourlyRate: Long,
    val grossSalary: Long,
    val insuranceDeduction: Long,
    val taxDeduction: Long,
    val supplementaryInsurance: Long,
    val advanceSalaryDeduction: Long,
    val loanInstallmentDeduction: Long,
    val otherDeductions: Long,
    val totalDeductions: Long,
    val netSalary: Long,
    // اقلام و رکوردهای مالی ثبت‌شده در ماه (پاداش‌ها، ایاب و ذهاب، کسورات و غذا)
    val registeredBonuses: Long = 0L,
    val registeredOtherAllowances: Long = 0L,
    val registeredFinancialDeductions: Long = 0L
)

object SalaryCalculator {

    fun calculateHourlyRate(settings: SalarySettingsEntity): Long {
        // محاسبه نرخ هر ساعت کار عادی بر اساس مبنای تنظیمی (۲۲۰ ساعت پیش‌فرض قانون کار، یا ۱۹۲ یا ۱۷۶ ساعت)
        val totalBase = settings.baseSalary + settings.seniorityAllowance
        val divisor = if (settings.hourlyBaseDivisor > 0) settings.hourlyBaseDivisor else 220.0
        return if (totalBase > 0) (totalBase / divisor).roundToLong() else 0L
    }

    fun calculateOvertimeHourlyRate(settings: SalarySettingsEntity, hourlyRate: Long, isSpecial: Boolean): Long {
        val multiplier = if (isSpecial) settings.specialOvertimeMultiplier else settings.regularOvertimeMultiplier
        return (hourlyRate * multiplier).roundToLong()
    }

    fun calculateMonthlySalary(
        settings: SalarySettingsEntity,
        workedDays: Int = 30,
        regularOvertimeMinutes: Int = 0,
        specialOvertimeMinutes: Int = 0,
        advanceSalary: Long = 0L,
        loanInstallments: Long = 0L,
        registeredBonuses: Long = 0L,
        registeredOtherAllowances: Long = 0L,
        registeredFinancialDeductions: Long = 0L
    ): SalaryBreakdown {
        val daysRatio = workedDays.toDouble() / 30.0

        // Base earnings proportional to worked days
        val basePayEarned = (settings.baseSalary * daysRatio).roundToLong()
        val seniorityEarned = (settings.seniorityAllowance * daysRatio).roundToLong()

        // Fixed monthly benefits (if worked full month or proportional)
        val housing = if (workedDays >= 30) settings.housingAllowance else (settings.housingAllowance * daysRatio).roundToLong()
        val coupon = if (workedDays >= 30) settings.workerCoupon else (settings.workerCoupon * daysRatio).roundToLong()
        val marital = if (workedDays >= 30) settings.maritalAllowance else (settings.maritalAllowance * daysRatio).roundToLong()
        val child = settings.childCount * settings.childAllowancePerChild

        // Shift allowance = % * (base + seniority)
        val shift = (((basePayEarned + seniorityEarned) * settings.shiftPercentage) / 100.0).roundToLong()

        // Additional permanent bonuses
        val otherBonusesTotal = settings.technicalBonus + settings.responsibilityBonus +
                settings.supervisoryBonus + settings.otherBonuses

        // Overtime calculations
        val hourlyRate = calculateHourlyRate(settings)
        val regularOvertimeRate = calculateOvertimeHourlyRate(settings, hourlyRate, false)
        val specialOvertimeRate = calculateOvertimeHourlyRate(settings, hourlyRate, true)

        val regularOvertimeHours = regularOvertimeMinutes / 60.0
        val specialOvertimeHours = specialOvertimeMinutes / 60.0

        val regularOvertimePay = (regularOvertimeHours * regularOvertimeRate).roundToLong()
        val specialOvertimePay = (specialOvertimeHours * specialOvertimeRate).roundToLong()

        // Gross salary (شامل حقوق پایه، مزایای قانونی، اضافه کاری‌ها و پاداش‌ها/مزایای ثبت‌شده)
        val grossSalary = basePayEarned + seniorityEarned + housing + coupon + marital +
                child + shift + otherBonusesTotal + regularOvertimePay + specialOvertimePay +
                registeredBonuses + registeredOtherAllowances

        // Deductions:
        // 1. Social Security Insurance: 7% of insured earnings
        // اقلام مشمول بیمه: حقوق پایه، سنوات، بن کارگری، حق مسکن، نوبت کاری، اضافه کاری، حق فنی و مسئولیت
        // اقلام معاف از بیمه طبق ماده ۲۸ قانون تامین اجتماعی: حق اولاد، حق ماموریت
        val insuredEarnings = basePayEarned + seniorityEarned + housing + coupon +
                shift + regularOvertimePay + specialOvertimePay + otherBonusesTotal

        // سقف بیمه تامین اجتماعی: حداکثر ۷ برابر حداقل حقوق پایه
        val insuranceCeiling = settings.baseSalary * 7
        val cappedInsuredEarnings = min(insuredEarnings, insuranceCeiling)

        val insurance = if (settings.isAutoInsurance) {
            ((cappedInsuredEarnings * settings.workerInsurancePercent) / 100.0).roundToLong()
        } else {
            0L
        }

        // 2. Income Tax (Labor tax bracket 1405)
        // درآمد مشمول مالیات = حقوق ناخالص منهای حق بیمه سهم کارگر
        val taxableIncome = max(0L, grossSalary - insurance)
        val tax = if (settings.isAutoTax) {
            calculateLaborTax1405(taxableIncome)
        } else {
            settings.manualTaxAmount
        }

        // 3. Supplementary Insurance & other deductions
        val supplementary = settings.supplementaryInsurance
        val otherDeductions = settings.otherDeductions

        val totalDeductions = insurance + tax + supplementary + otherDeductions +
                advanceSalary + loanInstallments + registeredFinancialDeductions

        val netSalary = max(0L, grossSalary - totalDeductions)

        return SalaryBreakdown(
            workedDays = workedDays,
            regularOvertimeMinutes = regularOvertimeMinutes,
            specialOvertimeMinutes = specialOvertimeMinutes,
            basePayEarned = basePayEarned,
            seniorityEarned = seniorityEarned,
            housingAllowance = housing,
            workerCoupon = coupon,
            maritalAllowance = marital,
            childAllowance = child,
            shiftAllowance = shift,
            otherBonuses = otherBonusesTotal,
            regularOvertimePay = regularOvertimePay,
            specialOvertimePay = specialOvertimePay,
            hourlyRate = hourlyRate,
            regularOvertimeHourlyRate = regularOvertimeRate,
            specialOvertimeHourlyRate = specialOvertimeRate,
            grossSalary = grossSalary,
            insuranceDeduction = insurance,
            taxDeduction = tax,
            supplementaryInsurance = supplementary,
            advanceSalaryDeduction = advanceSalary,
            loanInstallmentDeduction = loanInstallments,
            otherDeductions = otherDeductions,
            totalDeductions = totalDeductions,
            netSalary = netSalary,
            registeredBonuses = registeredBonuses,
            registeredOtherAllowances = registeredOtherAllowances,
            registeredFinancialDeductions = registeredFinancialDeductions
        )
    }

    /**
     * جدول پلکانی مالیات بر درآمد حقوق سال ۱۴۰۵ (بر اساس قانون بودجه سال ۱۴۰۵ کل کشور - ارقام به تومان)
     * - تا ۲۴,۰۰۰,۰۰۰ تومان در ماه: معاف از مالیات (۰٪)
     * - ۲۴,۰۰۰,۰۰۱ تا ۳۰,۰۰۰,۰۰۰ تومان (۶ میلیون مازاد): ۱۰٪
     * - ۳۰,۰۰۰,۰۰۱ تا ۳۸,۰۰۰,۰۰۰ تومان (۸ میلیون مازاد): ۱۵٪
     * - ۳۸,۰۰۰,۰۰۱ تا ۵۰,۰۰۰,۰۰۰ تومان (۱۲ میلیون مازاد): ۲۰٪
     * - ۵۰,۰۰۰,۰۰۱ تا ۶۶,۰۰۰,۰۰۰ تومان (۱۶ میلیون مازاد): ۲۵٪
     * - مازاد بر ۶۶,۰۰۰,۰۰۰ تومان: ۳۰٪
     */
    fun calculateLaborTax1405(taxableIncome: Long): Long {
        val exemptionThreshold = 24_000_000L // معافیت ماهانه ۲۴ میلیون تومان در سال ۱۴۰۵
        if (taxableIncome <= exemptionThreshold) return 0L

        var tax = 0.0
        var remaining = (taxableIncome - exemptionThreshold).toDouble()

        // پله اول مازاد: ۲۴ تا ۳۰ میلیون تومان (سقف ۶ میلیون) با نرخ ۱۰٪
        val slab1 = min(remaining, 6_000_000.0)
        tax += slab1 * 0.10
        remaining -= slab1

        // پله دوم مازاد: ۳۰ تا ۳۸ میلیون تومان (سقف ۸ میلیون) با نرخ ۱۵٪
        if (remaining > 0) {
            val slab2 = min(remaining, 8_000_000.0)
            tax += slab2 * 0.15
            remaining -= slab2
        }

        // پله سوم مازاد: ۳۸ تا ۵۰ میلیون تومان (سقف ۱۲ میلیون) با نرخ ۲۰٪
        if (remaining > 0) {
            val slab3 = min(remaining, 12_000_000.0)
            tax += slab3 * 0.20
            remaining -= slab3
        }

        // پله چهارم مازاد: ۵۰ تا ۶۶ میلیون تومان (سقف ۱۶ میلیون) با نرخ ۲۵٪
        if (remaining > 0) {
            val slab4 = min(remaining, 16_000_000.0)
            tax += slab4 * 0.25
            remaining -= slab4
        }

        // پله پنجم مازاد: بالاتر از ۶۶ میلیون تومان با نرخ ۳۰٪
        if (remaining > 0) {
            tax += remaining * 0.30
        }

        return tax.roundToLong()
    }

    /**
     * Backward-compatible alias for calculateLaborTax
     */
    fun calculateLaborTax(taxableIncome: Long): Long = calculateLaborTax1405(taxableIncome)

    /**
     * Eidi (End of Year Bonus) Calculation 1405
     * Formula: 2x monthly base salary, capped at 3x minimum wage
     */
    fun calculateEidi(baseMonthlySalary: Long, workedMonths: Int, minimumMonthlyWage: Long = 16_625_550L): Long {
        val minEidi = baseMonthlySalary * 2
        val maxEidi = minimumMonthlyWage * 3
        val annualBonus = min(minEidi, maxEidi)
        val months = min(12, max(0, workedMonths))
        return (annualBonus * (months / 12.0)).roundToLong()
    }

    /**
     * End of service severance (سنوات پایان خدمت)
     * 1 month of latest salary per full year worked
     */
    fun calculateSanavatEnd(latestMonthlySalary: Long, years: Double): Long {
        return (latestMonthlySalary * years).roundToLong()
    }
}
