package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SalarySettingsEntity
import com.example.util.PersianDateUtil
import com.example.util.SalaryCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("اضافه کاری", appName)
    }

    @Test
    fun `test persian date util conversions`() {
        val today = PersianDateUtil.today()
        assertTrue(today.year >= 1403)
        assertTrue(today.month in 1..12)
        assertTrue(today.day in 1..31)

        val faNum = PersianDateUtil.toFaDigits("12345")
        assertEquals("۱۲۳۴۵", faNum)

        val enNum = PersianDateUtil.toEnDigits("۱۲۳۴۵")
        assertEquals("12345", enNum)

        val yesterday = PersianDateUtil.yesterday()
        assertTrue(yesterday.year >= 1403)

        // 1404/01/01 is Friday (جمعه) in Jalali calendar (index 6)
        val dayOfWeekIndex = PersianDateUtil.getPersianDayOfWeekIndex(1404, 1, 1)
        assertTrue(dayOfWeekIndex in 0..6)
        val dayOfWeekName = PersianDateUtil.getDayOfWeekName(1404, 1, 1)
        assertTrue(dayOfWeekName.isNotEmpty())
    }

    @Test
    fun `test salary calculation logic`() {
        val settings = SalarySettingsEntity(
            baseSalary = 10_000_000L,
            seniorityAllowance = 0L,
            housingAllowance = 900_000L,
            workerCoupon = 2_000_000L
        )

        val breakdown = SalaryCalculator.calculateMonthlySalary(
            settings = settings,
            workedDays = 30,
            regularOvertimeMinutes = 120 // 2 hours
        )

        assertNotNull(breakdown)
        assertTrue(breakdown.grossSalary > 10_000_000L)
        assertTrue(breakdown.netSalary > 0L)
    }
}
