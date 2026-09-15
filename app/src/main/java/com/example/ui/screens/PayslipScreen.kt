package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.MonthSelectorHeader
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonDeduction
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianDateUtil
import com.example.util.SalaryBreakdown

@Composable
fun PayslipScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val year by viewModel.selectedYear.collectAsState()
    val month by viewModel.selectedMonth.collectAsState()
    val settings by viewModel.salarySettings.collectAsState()
    val salaryBreakdown by viewModel.currentSalaryBreakdown.collectAsState()

    val monthName = PersianDateUtil.MONTH_NAMES.getOrElse(month - 1) { "" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Month Selector
        MonthSelectorHeader(
            year = year,
            month = month,
            onPrevMonth = { viewModel.prevMonth() },
            onNextMonth = { viewModel.nextMonth() },
            onSelectMonth = { y, m -> viewModel.setYearAndMonth(y, m) }
        )

        // Main Payslip Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("payslip_full_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "فیش حقوق و دستمزد",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "$monthName ماه ${PersianDateUtil.toFaDigits(year)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "قانون کار",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Employee Details Table
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "نام کارمند:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = settings.userName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = "سمت / عنوان شغلی:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = settings.jobTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = "روزهای کارکرد:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${PersianDateUtil.toFaDigits(salaryBreakdown.workedDays)} روز",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Earnings Section (دریافتی‌ها و مزایا)
                SectionHeader(title = "حقوق، دستمزد و مزایا", color = EmeraldSuccess)
                Spacer(modifier = Modifier.height(6.dp))

                PayslipItemRow("حقوق پایه ماهانه", salaryBreakdown.basePayEarned)
                PayslipItemRow("بن کارگری و اقلام مصرفی", salaryBreakdown.workerCoupon)
                PayslipItemRow("حق مسکن", salaryBreakdown.housingAllowance)
                if (salaryBreakdown.seniorityEarned > 0) {
                    PayslipItemRow("پایه سنوات خدمت", salaryBreakdown.seniorityEarned)
                }
                if (salaryBreakdown.maritalAllowance > 0) {
                    PayslipItemRow("حق تأهل", salaryBreakdown.maritalAllowance)
                }
                if (salaryBreakdown.childAllowance > 0) {
                    PayslipItemRow(
                        "حق اولاد (${PersianDateUtil.toFaDigits(settings.childCount)} فرزند)",
                        salaryBreakdown.childAllowance
                    )
                }
                if (salaryBreakdown.regularOvertimePay > 0) {
                    PayslipItemRow(
                        "اضافه کاری عادی (${PersianDateUtil.formatMinutesToHourMin(salaryBreakdown.regularOvertimeMinutes)})",
                        salaryBreakdown.regularOvertimePay
                    )
                }
                if (salaryBreakdown.specialOvertimePay > 0) {
                    PayslipItemRow(
                        "اضافه کاری ویژه/تعطیل (${PersianDateUtil.formatMinutesToHourMin(salaryBreakdown.specialOvertimeMinutes)})",
                        salaryBreakdown.specialOvertimePay
                    )
                }
                if (salaryBreakdown.shiftAllowance > 0) {
                    PayslipItemRow("حق نوبت کاری / شیفت", salaryBreakdown.shiftAllowance)
                }
                if (salaryBreakdown.otherBonuses > 0) {
                    PayslipItemRow("مزایای احکام شغلی و کارگاهی", salaryBreakdown.otherBonuses)
                }
                if (salaryBreakdown.registeredBonuses > 0) {
                    PayslipItemRow("پاداش‌ها و تشویقی‌های ثبت‌شده", salaryBreakdown.registeredBonuses)
                }
                if (salaryBreakdown.registeredOtherAllowances > 0) {
                    PayslipItemRow("سایر مزایای نقدی (ایاب‌وذهاب و رفاهی)", salaryBreakdown.registeredOtherAllowances)
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(8.dp))

                TotalRow("جمع کل ناخالص دریافتی:", salaryBreakdown.grossSalary, EmeraldSuccess)

                Spacer(modifier = Modifier.height(20.dp))

                // Deductions Section (کسورات قانونی و اختیاری)
                SectionHeader(title = "کسورات ماهانه", color = CrimsonDeduction)
                Spacer(modifier = Modifier.height(6.dp))

                if (salaryBreakdown.insuranceDeduction > 0) {
                    PayslipItemRow("بیمه تأمین اجتماعی سهم کارگر (۷٪)", salaryBreakdown.insuranceDeduction, isDeduction = true)
                }
                if (salaryBreakdown.taxDeduction > 0) {
                    PayslipItemRow("مالیات حقوق ماهانه", salaryBreakdown.taxDeduction, isDeduction = true)
                } else {
                    PayslipItemRow("مالیات حقوق", 0, isDeduction = true, customNote = "معاف از مالیات")
                }
                if (salaryBreakdown.supplementaryInsurance > 0) {
                    PayslipItemRow("بیمه تکمیلی درمان", salaryBreakdown.supplementaryInsurance, isDeduction = true)
                }
                if (salaryBreakdown.advanceSalaryDeduction > 0) {
                    PayslipItemRow("مساعده حقوق دریافتی در این ماه", salaryBreakdown.advanceSalaryDeduction, isDeduction = true)
                }
                if (salaryBreakdown.loanInstallmentDeduction > 0) {
                    PayslipItemRow("اقساط وام‌های دریافتی", salaryBreakdown.loanInstallmentDeduction, isDeduction = true)
                }
                if (salaryBreakdown.registeredFinancialDeductions > 0) {
                    PayslipItemRow("کسورات ثبت‌شده (هزینه غذا، خسارت و...)", salaryBreakdown.registeredFinancialDeductions, isDeduction = true)
                }
                if (salaryBreakdown.otherDeductions > 0) {
                    PayslipItemRow("سایر کسورات", salaryBreakdown.otherDeductions, isDeduction = true)
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(8.dp))

                TotalRow("جمع کل کسورات:", salaryBreakdown.totalDeductions, CrimsonDeduction)

                Spacer(modifier = Modifier.height(22.dp))

                // Net Pay Highlight Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = EmeraldContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "خالص دریافتی نهایی کارمند:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmeraldDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = PersianDateUtil.formatCurrency(salaryBreakdown.netSalary),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }
                }
            }
        }

        // Share Payslip button
        Button(
            onClick = {
                sharePayslipText(context, settings.userName, monthName, year, salaryBreakdown)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_share_payslip"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "اشتراک‌گذاری متن فیش حقوقی",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SectionHeader(title: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PayslipItemRow(
    title: String,
    amount: Long,
    isDeduction: Boolean = false,
    customNote: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (customNote != null) {
            Text(
                text = customNote,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = (if (isDeduction && amount > 0) "- " else "") + PersianDateUtil.formatCurrency(amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isDeduction && amount > 0) CrimsonDeduction else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TotalRow(title: String, total: Long, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = PersianDateUtil.formatCurrency(total),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun sharePayslipText(
    context: Context,
    userName: String,
    monthName: String,
    year: Int,
    breakdown: SalaryBreakdown
) {
    val text = buildString {
        appendLine("📄 فیش حقوق و دستمزد")
        appendLine("👤 کارمند: $userName")
        appendLine("🗓️ دوره: $monthName ${PersianDateUtil.toFaDigits(year)}")
        appendLine("⏳ کارکرد: ${PersianDateUtil.toFaDigits(breakdown.workedDays)} روز")
        appendLine("-------------------------")
        appendLine("💰 حقوق و مزایا:")
        appendLine("• پایه حقوق: ${PersianDateUtil.formatCurrency(breakdown.basePayEarned)}")
        appendLine("• بن کارگری: ${PersianDateUtil.formatCurrency(breakdown.workerCoupon)}")
        appendLine("• حق مسکن: ${PersianDateUtil.formatCurrency(breakdown.housingAllowance)}")
        if (breakdown.seniorityEarned > 0) appendLine("• سنوات: ${PersianDateUtil.formatCurrency(breakdown.seniorityEarned)}")
        if (breakdown.maritalAllowance > 0) appendLine("• حق تأهل: ${PersianDateUtil.formatCurrency(breakdown.maritalAllowance)}")
        if (breakdown.childAllowance > 0) appendLine("• حق اولاد: ${PersianDateUtil.formatCurrency(breakdown.childAllowance)}")
        if (breakdown.regularOvertimePay > 0) appendLine("• اضافه‌کاری عادی: ${PersianDateUtil.formatCurrency(breakdown.regularOvertimePay)}")
        if (breakdown.specialOvertimePay > 0) appendLine("• اضافه‌کاری تعطیل: ${PersianDateUtil.formatCurrency(breakdown.specialOvertimePay)}")
        if (breakdown.registeredBonuses > 0) appendLine("• پاداش ثبت‌شده: ${PersianDateUtil.formatCurrency(breakdown.registeredBonuses)}")
        if (breakdown.registeredOtherAllowances > 0) appendLine("• سایر مزایای ثبت‌شده: ${PersianDateUtil.formatCurrency(breakdown.registeredOtherAllowances)}")
        appendLine("🔹 جمع کل ناخالص: ${PersianDateUtil.formatCurrency(breakdown.grossSalary)}")
        appendLine("-------------------------")
        appendLine("🔻 کسورات:")
        appendLine("• بیمه تأمین اجتماعی (۷٪): ${PersianDateUtil.formatCurrency(breakdown.insuranceDeduction)}")
        appendLine("• مالیات: ${PersianDateUtil.formatCurrency(breakdown.taxDeduction)}")
        if (breakdown.advanceSalaryDeduction > 0) appendLine("• مساعده دریافتی: ${PersianDateUtil.formatCurrency(breakdown.advanceSalaryDeduction)}")
        if (breakdown.registeredFinancialDeductions > 0) appendLine("• کسورات ثبت‌شده (غذا/جریمه): ${PersianDateUtil.formatCurrency(breakdown.registeredFinancialDeductions)}")
        if (breakdown.loanInstallmentDeduction > 0) appendLine("• اقساط وام: ${PersianDateUtil.formatCurrency(breakdown.loanInstallmentDeduction)}")
        appendLine("🔸 جمع کل کسورات: ${PersianDateUtil.formatCurrency(breakdown.totalDeductions)}")
        appendLine("=========================")
        appendLine("🟢 خالص پرداختی: ${PersianDateUtil.formatCurrency(breakdown.netSalary)}")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "اشتراک فیش حقوقی")
    context.startActivity(shareIntent)
}
