package com.example.util

import java.text.DecimalFormat
import java.util.Calendar
import java.util.Locale

object PersianDateUtil {
    val MONTH_NAMES = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val WEEK_DAYS = listOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    data class PersianDate(
        val year: Int,
        val month: Int,
        val day: Int
    ) {
        val monthName: String get() = if (month in 1..12) MONTH_NAMES[month - 1] else ""
        fun format(): String = String.format(Locale.US, "%04d/%02d/%02d", year, month, day)
        fun formatFa(): String = toFaDigits(format())
    }

    /**
     * Accurate Gregorian to Jalali (Persian) date conversion
     */
    fun gregorianToPersian(gYear: Int, gMonth: Int, gDay: Int): PersianDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy = gYear - 1600
        val gm = gMonth - 1
        val gd = gDay - 1

        var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
        for (i in 0 until gm) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..11) {
            if (jDayNo < jDaysInMonth[i]) {
                jm = i + 1
                break
            }
            jDayNo -= jDaysInMonth[i]
        }
        val jd = jDayNo + 1

        return PersianDate(jy, jm, jd)
    }

    fun today(): PersianDate {
        val cal = Calendar.getInstance()
        return gregorianToPersian(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun yesterday(): PersianDate {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }
        return gregorianToPersian(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Accurate Jalali to Gregorian conversion
     */
    fun persianToGregorian(jYear: Int, jMonth: Int, jDay: Int): Calendar {
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        val jy = jYear - 979
        val jm = jMonth - 1
        val jd = jDay - 1

        var jDayNo = 365 * jy + (jy / 33) * 8 + (jy % 33 + 3) / 4
        for (i in 0 until jm) {
            jDayNo += jDaysInMonth[i]
        }
        jDayNo += jd

        var gDayNo = jDayNo + 79
        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097

        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524

            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = false
            }
        }

        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }

        val gDaysInMonth = intArrayOf(
            31, if (gy % 4 == 0 && (gy % 100 != 0 || gy % 400 == 0)) 29 else 28,
            31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        )
        var gm = 0
        for (i in 0..11) {
            if (gDayNo < gDaysInMonth[i]) {
                gm = i
                break
            }
            gDayNo -= gDaysInMonth[i]
        }
        val gd = gDayNo + 1
        return Calendar.getInstance().apply {
            set(gy, gm, gd, 12, 0, 0)
        }
    }

    /**
     * Returns 0 for Saturday (شنبه) to 6 for Friday (جمعه)
     */
    fun getPersianDayOfWeekIndex(jYear: Int, jMonth: Int, jDay: Int): Int {
        val cal = persianToGregorian(jYear, jMonth, jDay)
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6
            else -> 0
        }
    }

    fun getDayOfWeekName(jYear: Int, jMonth: Int, jDay: Int): String {
        val idx = getPersianDayOfWeekIndex(jYear, jMonth, jDay)
        return WEEK_DAYS.getOrElse(idx) { "" }
    }

    fun currentTimeString(): String {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        return String.format(Locale.US, "%02d:%02d", h, m)
    }

    /**
     * Converts English digits 0-9 to Persian digits ۰-۹
     */
    fun toFaDigits(input: Any?): String {
        if (input == null) return ""
        val str = input.toString()
        val faDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(faDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Converts Persian/Arabic digits to English digits
     */
    fun toEnDigits(input: String): String {
        var res = input
        val fa = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val ar = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        for (i in 0..9) {
            res = res.replace(fa[i], ('0' + i)).replace(ar[i], ('0' + i))
        }
        return res
    }

    /**
     * Formats monetary amount with 3-digit comma separators and Persian digits + تومان
     */
    fun formatCurrency(amount: Long, unit: String = "تومان"): String {
        val formatter = DecimalFormat("#,###")
        val formatted = formatter.format(amount)
        return "${toFaDigits(formatted)} $unit"
    }

    /**
     * Formats minutes to Persian human readable string (e.g. ۳ ساعت و ۴۵ دقیقه)
     */
    fun formatMinutesToHourMin(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return when {
            hours > 0 && mins > 0 -> "${toFaDigits(hours)} ساعت و ${toFaDigits(mins)} دقیقه"
            hours > 0 -> "${toFaDigits(hours)} ساعت"
            else -> "${toFaDigits(mins)} دقیقه"
        }
    }

    /**
     * Calculate difference between two HH:mm times in minutes
     */
    fun calculateMinutesBetween(timeFrom: String, timeTo: String): Int {
        try {
            val p1 = timeFrom.trim().split(":")
            val p2 = timeTo.trim().split(":")
            val h1 = toEnDigits(p1[0]).toInt()
            val m1 = toEnDigits(p1[1]).toInt()
            val h2 = toEnDigits(p2[0]).toInt()
            val m2 = toEnDigits(p2[1]).toInt()

            var minutes = (h2 * 60 + m2) - (h1 * 60 + m1)
            if (minutes < 0) {
                // Crosses midnight
                minutes += 24 * 60
            }
            return minutes
        } catch (_: Exception) {
            return 0
        }
    }

    fun daysInPersianMonth(year: Int, month: Int): Int {
        return when (month) {
            in 1..6 -> 31
            in 7..11 -> 30
            12 -> if (isPersianLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    fun isPersianLeapYear(year: Int): Boolean {
        // Standard 33-year cycle algorithm
        val a = ((year - 474) % 2820 + 474 + 38) * 682
        val b = a % 2816
        return b < 682
    }

    /**
     * Calculates the inclusive number of days between two Persian dates (d1 to d2)
     */
    fun daysBetweenInclusive(y1: Int, m1: Int, d1: Int, y2: Int, m2: Int, d2: Int): Int {
        val cal1 = persianToGregorian(y1, m1, d1)
        val cal2 = persianToGregorian(y2, m2, d2)
        val diffMillis = cal2.timeInMillis - cal1.timeInMillis
        val days = (diffMillis / (1000L * 60 * 60 * 24)).toInt() + 1
        return if (days < 1) 1 else days
    }

    /**
     * Adds n days to a Persian date and returns the resulting Persian date
     */
    fun addDaysToPersianDate(y: Int, m: Int, d: Int, daysToAdd: Int): PersianDate {
        val cal = persianToGregorian(y, m, d)
        cal.add(Calendar.DAY_OF_MONTH, daysToAdd)
        return gregorianToPersian(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Returns list of all Persian dates in the range [startDate, endDate] inclusive
     */
    fun getDateRange(y1: Int, m1: Int, d1: Int, y2: Int, m2: Int, d2: Int): List<PersianDate> {
        val count = daysBetweenInclusive(y1, m1, d1, y2, m2, d2)
        val result = mutableListOf<PersianDate>()
        val cal = persianToGregorian(y1, m1, d1)
        for (i in 0 until count) {
            val pd = gregorianToPersian(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
            result.add(pd)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }
}

