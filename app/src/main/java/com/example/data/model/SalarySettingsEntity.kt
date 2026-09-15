package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salary_settings")
data class SalarySettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "کاربر گرامی",
    val personnelCode: String = "",
    val jobTitle: String = "پرسنل شاغل",
    val companyName: String = "شرکت / کارگاه",

    // احکام رسمی قانون کار سال ۱۴۰۵ بر اساس مصوبه و جدول کارتابان (ارقام به تومان)
    val baseSalary: Long = 16_625_550L, // حداقل حقوق پایه ماهانه ۳۰ روزه ۱۴۰۵ (روزانه ۵۵۴,۱۸۵ تومان)
    val housingAllowance: Long = 3_000_000L, // حق مسکن ۱۴۰۵ (۳ میلیون تومان)
    val workerCoupon: Long = 2_200_000L, // بن کارگری (کمک هزینه اقلام مصرفی) ۱۴۰۵ (۲.۲ میلیون تومان)
    val maritalAllowance: Long = 500_000L, // حق تأهل ۱۴۰۵ (۵۰۰ هزار تومان)
    val childCount: Int = 0,
    val childAllowancePerChild: Long = 1_662_555L, // حق اولاد برای هر فرزند (۳ برابر حداقل مزد روزانه ۵۵۴,۱۸۵ تومان)
    val seniorityAllowance: Long = 500_000L, // پایه سنوات ماهانه برای یک سال سابقه در ۱۴۰۵ (۵۰۰ هزار تومان)
    val shiftPercentage: Double = 0.0, // درصد نوبت کاری (۱۰، ۱۵، ۲۲.۵، ۳۵)

    // مزایای تخصصی و کارگاهی
    val technicalBonus: Long = 0L, // حق فنی و تخصصی
    val responsibilityBonus: Long = 0L, // حق مسئولیت / سرپرستی
    val supervisoryBonus: Long = 0L, // حق سرپرستی
    val otherBonuses: Long = 0L, // سایر مزایا

    // کسورات
    val otherDeductions: Long = 0L, // سایر کسورات
    val supplementaryInsurance: Long = 0L, // بیمه تکمیلی
    val workerInsurancePercent: Double = 7.0, // ۷٪ سهم بیمه کارگر
    val isAutoInsurance: Boolean = true,
    val isAutoTax: Boolean = true,
    val manualTaxAmount: Long = 0L,

    // تعریف ساعت کاری استاندارد و محاسبه خودکار اضافه کار تردد
    val dailyWorkMinutes: Int = 440, // ۷ ساعت و ۲۰ دقیقه (۴۴ ساعت در هفته موظفی قانون کار)
    val autoOvertimeOnExcessWork: Boolean = true, // اگر تردد بیشتر از ساعت کاری موظف باشد، اضافه کار در نظر بگیرد
    val standardShiftStartTime: String = "08:00", // ساعت ورود استاندارد
    val standardShiftEndTime: String = "15:20", // ساعت خروج استاندارد

    // روش‌ها و مبانی محاسبات قانون کار
    val hourlyBaseDivisor: Double = 220.0, // مبنای تقسیم ساعت کاری ماهانه: 220 ساعت، 192 ساعت، 176 ساعت
    val regularOvertimeMultiplier: Double = 1.4, // ضریب اضافه کاری عادی (۴۰٪ مازاد - ماده ۵۹ قانون کار)
    val specialOvertimeMultiplier: Double = 1.8, // ضریب تعطیل کاری / جمعه کاری (۸۰٪ مازاد)

    // تنظیمات ظاهر و تم برنامه
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val themeColor: String = "INDIGO" // "INDIGO", "EMERALD", "AMBER", "TEAL"
) {
    companion object {
        // ارقام رسمی و مصوب قانون کار سال ۱۴۰۵ (جدول کارتابان)
        val LAW_1405_DEFAULTS = SalarySettingsEntity(
            baseSalary = 16_625_550L,
            housingAllowance = 3_000_000L,
            workerCoupon = 2_200_000L,
            maritalAllowance = 500_000L,
            childAllowancePerChild = 1_662_555L,
            seniorityAllowance = 500_000L,
            dailyWorkMinutes = 440,
            autoOvertimeOnExcessWork = true,
            standardShiftStartTime = "08:00",
            standardShiftEndTime = "15:20",
            hourlyBaseDivisor = 220.0,
            regularOvertimeMultiplier = 1.4,
            specialOvertimeMultiplier = 1.8,
            workerInsurancePercent = 7.0,
            isAutoInsurance = true,
            isAutoTax = true
        )
    }
}

