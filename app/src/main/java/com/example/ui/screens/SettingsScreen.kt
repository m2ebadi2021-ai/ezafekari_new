package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.SalarySettingsEntity
import com.example.ui.theme.AmberDark
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.ThemeTeal
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianDateUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settingsFromDb by viewModel.salarySettings.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    // 1. Profile Info
    var userName by remember { mutableStateOf("") }
    var personnelCode by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }

    // 2. Base & Allowances
    var baseSalaryText by remember { mutableStateOf("") }
    var housingText by remember { mutableStateOf("") }
    var couponText by remember { mutableStateOf("") }
    var maritalText by remember { mutableStateOf("") }
    var childCountText by remember { mutableStateOf("") }
    var childAllowanceText by remember { mutableStateOf("") }
    var seniorityText by remember { mutableStateOf("") }

    // 3. Shift & Extra Bonuses
    var shiftPercentText by remember { mutableStateOf("") }
    var technicalBonusText by remember { mutableStateOf("") }
    var responsibilityBonusText by remember { mutableStateOf("") }
    var supervisoryBonusText by remember { mutableStateOf("") }
    var otherBonusesText by remember { mutableStateOf("") }

    // 4. Calculation Rules
    var hourlyBaseDivisorText by remember { mutableStateOf("220") }
    var regularOvertimeMultiplierText by remember { mutableStateOf("1.4") }
    var specialOvertimeMultiplierText by remember { mutableStateOf("1.8") }

    // 4.1 Working Hours & Auto Overtime Configuration
    var dailyWorkMinutesText by remember { mutableStateOf("440") }
    var autoOvertimeOnExcessWork by remember { mutableStateOf(true) }
    var standardShiftStartTime by remember { mutableStateOf("08:00") }
    var standardShiftEndTime by remember { mutableStateOf("16:20") }

    // 5. Insurance & Tax Rules
    var isAutoInsurance by remember { mutableStateOf(true) }
    var workerInsurancePercentText by remember { mutableStateOf("7.0") }
    var isAutoTax by remember { mutableStateOf(true) }
    var manualTaxText by remember { mutableStateOf("0") }
    var supplementaryInsuranceText by remember { mutableStateOf("0") }
    var otherDeductionsText by remember { mutableStateOf("0") }

    // 6. Theme & Palette
    var themeMode by remember { mutableStateOf("SYSTEM") }
    var themeColor by remember { mutableStateOf("INDIGO") }

    // Dialog States
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonInput by remember { mutableStateOf("") }
    var showClearDataConfirmDialog by remember { mutableStateOf(false) }
    var showBackupResultDialog by remember { mutableStateOf(false) }
    var backupResultJson by remember { mutableStateOf("") }

    // Synchronize form with database
    LaunchedEffect(settingsFromDb) {
        userName = settingsFromDb.userName
        personnelCode = settingsFromDb.personnelCode
        jobTitle = settingsFromDb.jobTitle
        companyName = settingsFromDb.companyName

        baseSalaryText = settingsFromDb.baseSalary.toString()
        housingText = settingsFromDb.housingAllowance.toString()
        couponText = settingsFromDb.workerCoupon.toString()
        maritalText = settingsFromDb.maritalAllowance.toString()
        childCountText = settingsFromDb.childCount.toString()
        childAllowanceText = settingsFromDb.childAllowancePerChild.toString()
        seniorityText = settingsFromDb.seniorityAllowance.toString()

        shiftPercentText = settingsFromDb.shiftPercentage.toString()
        technicalBonusText = settingsFromDb.technicalBonus.toString()
        responsibilityBonusText = settingsFromDb.responsibilityBonus.toString()
        supervisoryBonusText = settingsFromDb.supervisoryBonus.toString()
        otherBonusesText = settingsFromDb.otherBonuses.toString()

        hourlyBaseDivisorText = settingsFromDb.hourlyBaseDivisor.toString()
        regularOvertimeMultiplierText = settingsFromDb.regularOvertimeMultiplier.toString()
        specialOvertimeMultiplierText = settingsFromDb.specialOvertimeMultiplier.toString()

        dailyWorkMinutesText = settingsFromDb.dailyWorkMinutes.toString()
        autoOvertimeOnExcessWork = settingsFromDb.autoOvertimeOnExcessWork
        standardShiftStartTime = settingsFromDb.standardShiftStartTime
        standardShiftEndTime = settingsFromDb.standardShiftEndTime

        isAutoInsurance = settingsFromDb.isAutoInsurance
        workerInsurancePercentText = settingsFromDb.workerInsurancePercent.toString()
        isAutoTax = settingsFromDb.isAutoTax
        manualTaxText = settingsFromDb.manualTaxAmount.toString()
        supplementaryInsuranceText = settingsFromDb.supplementaryInsurance.toString()
        otherDeductionsText = settingsFromDb.otherDeductions.toString()

        themeMode = settingsFromDb.themeMode
        themeColor = settingsFromDb.themeColor
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تنظیمات و احکام حقوقی",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "قانون کار ۱۴۰۵",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // ==============================================================
        // 0. OFFICIAL 1405 LABOR LAW BANNER & RESET BUTTON
        // ==============================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "نرخ‌ها و احکام مصوب قانون کار سال ۱۴۰۵",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "حداقل مزد ماهانه ۳۰ روزه: ۱۴,۹۴۹,۹۰۰ تومان (روزانه ۴۹۸,۳۳۰ تومان)\n" +
                            "بن کارگری: ۲,۸۰۰,۰۰۰ تومان | حق مسکن: ۱,۵۰۰,۰۰۰ تومان\n" +
                            "حق تأهل: ۷۵۰,۰۰۰ تومان | حق اولاد هر فرزند: ۱,۴۹۵,۰۰۰ تومان\n" +
                            "پایه سنوات ماهانه: ۳۶۰,۰۰۰ تومان | سقف معافیت مالیاتی: ۲۴ میلیون تومان",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldDark.copy(alpha = 0.9f),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showResetConfirmDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldDark),
                    border = BorderStroke(1.dp, EmeraldDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تنظیم مجدد احکام بر اساس مقادیر رسمی ۱۴۰۵",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ==============================================================
        // 1. PROFILE & WORKPLACE INFO
        // ==============================================================
        SettingsCard(title = "مشخصات پرسنلی و شغلی", icon = Icons.Default.Badge) {
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("نام و نام خانوادگی") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = jobTitle,
                    onValueChange = { jobTitle = it },
                    label = { Text("سمت / عنوان شغلی") },
                    modifier = Modifier.weight(1.4f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = personnelCode,
                    onValueChange = { personnelCode = PersianDateUtil.toEnDigits(it) },
                    label = { Text("شماره پرسنلی") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("نام کارگاه / شرکت / سازمان") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // ==============================================================
        // 2. BASE SALARY & LEGAL ALLOWANCES (1405)
        // ==============================================================
        SettingsCard(title = "پایه حقوق و مزایای قانونی سال ۱۴۰۵ (تومان)", icon = Icons.Default.Payments) {
            MoneyInputField("حقوق پایه ماهانه ۳۰ روزه", baseSalaryText) { baseSalaryText = it }
            MoneyInputField("بن کارگری (کمک هزینه اقلام مصرفی)", couponText) { couponText = it }
            MoneyInputField("حق مسکن ماهانه", housingText) { housingText = it }
            MoneyInputField("حق تأهل ماهانه", maritalText) { maritalText = it }
            MoneyInputField("پایه سنوات ماهانه", seniorityText) { seniorityText = it }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = childCountText,
                    onValueChange = { childCountText = PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() } },
                    label = { Text("تعداد اولاد") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = childAllowanceText,
                    onValueChange = { childAllowanceText = PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() } },
                    label = { Text("حق هر فرزند (تومان)") },
                    modifier = Modifier.weight(2f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // ==============================================================
        // 3. CALCULATION METHODS & MULTIPLIERS (روش‌های محاسبات قانون کار)
        // ==============================================================
        SettingsCard(title = "روش‌ها و ضرایب محاسبات قانون کار", icon = Icons.Default.Calculate) {
            Text(
                text = "مخرج ساعت کار ماهانه (ساعت موظفی تقسیم حقوق پایه):",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val divisors = listOf(
                    220.0 to "۲۲۰ ساعت (استاندارد قانون کار)",
                    192.0 to "۱۹۲ ساعت (۴ هفته × ۴۸)",
                    176.0 to "۱۷۶ ساعت (۴۴ ساعت در ۴ هفته)"
                )
                divisors.forEach { (div, label) ->
                    val isSelected = (hourlyBaseDivisorText.toDoubleOrNull() ?: 220.0) == div
                    FilterChip(
                        selected = isSelected,
                        onClick = { hourlyBaseDivisorText = div.toString() },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "ضریب اضافه‌کاری عادی (ماده ۵۹ قانون کار):",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1.4 to "۱.۴ (۴۰٪ اضافه)", 1.5 to "۱.۵ (۵۰٪ اضافه)", 1.35 to "۱.۳۵").forEach { (mult, label) ->
                    val isSelected = (regularOvertimeMultiplierText.toDoubleOrNull() ?: 1.4) == mult
                    FilterChip(
                        selected = isSelected,
                        onClick = { regularOvertimeMultiplierText = mult.toString() },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "ضریب اضافه‌کاری تعطیل‌کاری و جمعه‌کاری:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1.8 to "۱.۸ (۸۰٪ اضافه - جمعه/تعطیل)", 2.0 to "۲.۰ (دو برابر)", 1.4 to "۱.۴").forEach { (mult, label) ->
                    val isSelected = (specialOvertimeMultiplierText.toDoubleOrNull() ?: 1.8) == mult
                    FilterChip(
                        selected = isSelected,
                        onClick = { specialOvertimeMultiplierText = mult.toString() },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "درصد حق شیفت و نوبت‌کاری (%):",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    0.0 to "۰٪ (عادی)",
                    10.0 to "۱۰٪ (صبح-عصر)",
                    15.0 to "۱۵٪ (صبح-عصر-شب)",
                    22.5 to "۲۲.۵٪ (صبح-شب یا عصر-شب)",
                    35.0 to "۳۵٪ (نوبت شب ثابت)"
                ).forEach { (p, label) ->
                    val isSelected = (shiftPercentText.toDoubleOrNull() ?: 0.0) == p
                    FilterChip(
                        selected = isSelected,
                        onClick = { shiftPercentText = p.toString() },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            MoneyInputField("حق فنی و تخصصی", technicalBonusText) { technicalBonusText = it }
            MoneyInputField("حق مسئولیت / سرپرستی", responsibilityBonusText) { responsibilityBonusText = it }
            MoneyInputField("سایر مزایای کارگاهی و توافقی", otherBonusesText) { otherBonusesText = it }
        }

        // ==============================================================
        // 3.1. WORKING HOURS & AUTO OVERTIME (ساعت کاری موظف و اضافه‌کاری تردد)
        // ==============================================================
        SettingsCard(title = "ساعت کاری موظف و اضافه‌کاری تردد", icon = Icons.Default.Schedule) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "محاسبه خودکار اضافه کار از تردد",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "اگر تردد ثبت‌شده بیش از ساعت موظف روزانه باشد، مازاد آن خودکار اضافه کار محاسبه می‌شود",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = autoOvertimeOnExcessWork,
                    onCheckedChange = { autoOvertimeOnExcessWork = it }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "ساعت کاری موظف روزانه:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val currentMins = dailyWorkMinutesText.toIntOrNull() ?: 440
                listOf(
                    440 to "۷:۲۰ (قانون کار ۴۴۰ د)",
                    480 to "۸:۰۰ (۴۸۰ دقیقه)",
                    510 to "۸:۳۰ (۵۱۰ دقیقه)",
                    540 to "۹:۰۰ (۵۴۰ دقیقه)"
                ).forEach { (mins, label) ->
                    val isSelected = currentMins == mins
                    FilterChip(
                        selected = isSelected,
                        onClick = { dailyWorkMinutesText = mins.toString() },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = dailyWorkMinutesText,
                    onValueChange = { dailyWorkMinutesText = PersianDateUtil.toEnDigits(it) },
                    label = { Text("دقایق موظفی روزانه") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
                val enteredMins = dailyWorkMinutesText.toIntOrNull() ?: 0
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "معادل: ${PersianDateUtil.formatMinutesToHourMin(enteredMins)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "ساعت پیش‌فرض شیفت کاری:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = standardShiftStartTime,
                    onValueChange = { standardShiftStartTime = it },
                    label = { Text("ساعت ورود (مثال 08:00)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = standardShiftEndTime,
                    onValueChange = { standardShiftEndTime = it },
                    label = { Text("ساعت خروج (مثال 16:20)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // ==============================================================
        // 4. INSURANCE & TAX RULES (قوانین بیمه و مالیات ۱۴۰۵)
        // ==============================================================
        SettingsCard(title = "قوانین بیمه و مالیات حقوق سال ۱۴۰۵", icon = Icons.Default.Security) {
            // Insurance Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "محاسبه خودکار بیمه ۷٪ تأمین اجتماعی",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "کسر ۷ درصد سهم کارگر از اقلام مشمول با اعمال سقف مجاز",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isAutoInsurance,
                    onCheckedChange = { isAutoInsurance = it }
                )
            }

            if (!isAutoInsurance) {
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = workerInsurancePercentText,
                    onValueChange = { workerInsurancePercentText = PersianDateUtil.toEnDigits(it) },
                    label = { Text("درصد دلخواه بیمه (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // Tax Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "محاسبه پلکانی مالیات حقوق سال ۱۴۰۵",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "معافیت تا ۲۴ میلیون تومان در ماه و پلکان‌های ۱۰٪، ۱۵٪، ۲۰٪، ۳۰٪",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isAutoTax,
                    onCheckedChange = { isAutoTax = it }
                )
            }

            if (!isAutoTax) {
                Spacer(modifier = Modifier.height(8.dp))
                MoneyInputField("مبلغ مالیات دستی ماهانه (تومان)", manualTaxText) { manualTaxText = it }
            }

            Spacer(modifier = Modifier.height(10.dp))
            MoneyInputField("حق بیمه تکمیلی ماهانه (تومان)", supplementaryInsuranceText) { supplementaryInsuranceText = it }
            MoneyInputField("سایر کسورات قانونی / توافقی (تومان)", otherDeductionsText) { otherDeductionsText = it }
        }

        // ==============================================================
        // 5. THEME & APPEARANCE (تنظیم تم و رنگ برنامه)
        // ==============================================================
        SettingsCard(title = "تنظیم تم و ظاهر برنامه", icon = Icons.Default.Palette) {
            Text(
                text = "حالت نمایش:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("SYSTEM", "پیرو سیستم", Icons.Default.SettingsBrightness),
                    Triple("LIGHT", "روشن", Icons.Default.LightMode),
                    Triple("DARK", "تاریک", Icons.Default.DarkMode)
                ).forEach { (mode, label, icon) ->
                    val isSelected = themeMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            themeMode = mode
                            val updated = settingsFromDb.copy(themeMode = mode, themeColor = themeColor)
                            viewModel.saveSalarySettings(updated)
                        },
                        leadingIcon = {
                            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "رنگ‌بندی اصلی برنامه:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val palettes = listOf(
                    Triple("INDIGO", "سرمه‌ای رسمی", Color(0xFF3F51B5)),
                    Triple("EMERALD", "سبز حسابداری", EmeraldDark),
                    Triple("TEAL", "فیروزه‌ای مدرن", ThemeTeal),
                    Triple("AMBER", "کهربایی طلایی", AmberDark)
                )

                palettes.forEach { (colorKey, colorTitle, colorVal) ->
                    val isSelected = themeColor == colorKey
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = colorVal.copy(alpha = if (isSelected) 0.2f else 0.08f),
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) colorVal else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                themeColor = colorKey
                                val updated = settingsFromDb.copy(themeMode = themeMode, themeColor = colorKey)
                                viewModel.saveSalarySettings(updated)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(colorVal)
                                    .border(1.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = colorTitle,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) colorVal else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // ==============================================================
        // 6. BACKUP & RESTORE & DATA MANAGEMENT (بکاپ‌گیری و مدیریت داده‌ها)
        // ==============================================================
        SettingsCard(title = "بکاپ‌گیری و مدیریت داده‌ها", icon = Icons.Default.Backup) {
            Text(
                text = "ایجاد فایل پشتیبان آفلاین از تمامی ترددها، مرخصی‌ها، اضافه‌کاری‌ها، مساعده‌ها، وام‌ها و احکام حقوقی شما.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Backup Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val json = viewModel.generateBackupJson()
                            clipboardManager.setText(AnnotatedString(json))
                            backupResultJson = json
                            showBackupResultDialog = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تهیه و کپی بکاپ", style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = {
                        scope.launch {
                            val json = viewModel.generateBackupJson()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, json)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "ارسال فایل پشتیبان حقوق و دستمزد")
                            context.startActivity(shareIntent)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("اشتراک‌گذاری بکاپ", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        restoreJsonInput = ""
                        showRestoreDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("بازیابی نسخه پشتیبان", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = { showClearDataConfirmDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("پاکسازی رکوردها", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // ==============================================================
        // 7. SAVE SETTINGS BUTTON
        // ==============================================================
        Button(
            onClick = {
                val updated = settingsFromDb.copy(
                    userName = userName.ifBlank { "کاربر گرامی" },
                    personnelCode = personnelCode,
                    jobTitle = jobTitle.ifBlank { "پرسنل شاغل" },
                    companyName = companyName.ifBlank { "کارگاه / شرکت" },
                    baseSalary = baseSalaryText.toLongOrNull() ?: settingsFromDb.baseSalary,
                    housingAllowance = housingText.toLongOrNull() ?: settingsFromDb.housingAllowance,
                    workerCoupon = couponText.toLongOrNull() ?: settingsFromDb.workerCoupon,
                    maritalAllowance = maritalText.toLongOrNull() ?: settingsFromDb.maritalAllowance,
                    childCount = childCountText.toIntOrNull() ?: 0,
                    childAllowancePerChild = childAllowanceText.toLongOrNull() ?: settingsFromDb.childAllowancePerChild,
                    seniorityAllowance = seniorityText.toLongOrNull() ?: settingsFromDb.seniorityAllowance,
                    shiftPercentage = shiftPercentText.toDoubleOrNull() ?: 0.0,
                    technicalBonus = technicalBonusText.toLongOrNull() ?: 0L,
                    responsibilityBonus = responsibilityBonusText.toLongOrNull() ?: 0L,
                    supervisoryBonus = supervisoryBonusText.toLongOrNull() ?: 0L,
                    otherBonuses = otherBonusesText.toLongOrNull() ?: 0L,
                    hourlyBaseDivisor = hourlyBaseDivisorText.toDoubleOrNull() ?: 220.0,
                    regularOvertimeMultiplier = regularOvertimeMultiplierText.toDoubleOrNull() ?: 1.4,
                    specialOvertimeMultiplier = specialOvertimeMultiplierText.toDoubleOrNull() ?: 1.8,
                    dailyWorkMinutes = dailyWorkMinutesText.toIntOrNull() ?: 440,
                    autoOvertimeOnExcessWork = autoOvertimeOnExcessWork,
                    standardShiftStartTime = standardShiftStartTime.ifBlank { "08:00" },
                    standardShiftEndTime = standardShiftEndTime.ifBlank { "16:20" },
                    isAutoInsurance = isAutoInsurance,
                    workerInsurancePercent = workerInsurancePercentText.toDoubleOrNull() ?: 7.0,
                    isAutoTax = isAutoTax,
                    manualTaxAmount = manualTaxText.toLongOrNull() ?: 0L,
                    supplementaryInsurance = supplementaryInsuranceText.toLongOrNull() ?: 0L,
                    otherDeductions = otherDeductionsText.toLongOrNull() ?: 0L,
                    themeMode = themeMode,
                    themeColor = themeColor
                )
                viewModel.saveSalarySettings(updated)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_save_settings"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ذخیره احکام و تنظیمات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    // ==============================================================
    // DIALOGS
    // ==============================================================

    // 1. Reset Confirm Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldDark) },
            title = { Text("تنظیم مجدد احکام سال ۱۴۰۵") },
            text = {
                Text(
                    "آیا مطمئن هستید که می‌خواهید حقوق پایه، بن، مسکن، سنوات، حق تأهل و اولاد را طبق مصوبه رسمی شورای عالی کار سال ۱۴۰۵ بروزرسانی کنید؟\n" +
                            "(اطلاعات نام و شرکت شما حفظ خواهد شد)"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetTo1405LaborLawDefaults()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("بله، اعمال ارقام ۱۴۰۵")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // 2. Backup Result Dialog
    if (showBackupResultDialog) {
        AlertDialog(
            onDismissRequest = { showBackupResultDialog = false },
            icon = { Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldDark) },
            title = { Text("نسخه پشتیبان آماده شد") },
            text = {
                Column {
                    Text("متن فایل پشتیبان با موفقیت در کلیپ‌بورد کپی شد. می‌توانید آن را ذخیره یا در برنامه‌های دیگر جای‌گذاری کنید:")
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        Text(
                            text = backupResultJson.take(500) + if (backupResultJson.length > 500) "..." else "",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showBackupResultDialog = false }) {
                    Text("بستن")
                }
            }
        )
    }

    // 3. Restore Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            icon = { Icon(imageVector = Icons.Default.Restore, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("بازیابی نسخه پشتیبان") },
            text = {
                Column {
                    Text("متن JSON پشتیبان تهیه شده را در کادر زیر جای‌گذاری (Paste) کنید:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = restoreJsonInput,
                        onValueChange = { restoreJsonInput = it },
                        placeholder = { Text("متن پشتیبان JSON...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreJsonInput.isNotBlank()) {
                            viewModel.restoreBackup(restoreJsonInput) { success, _ ->
                                if (success) {
                                    showRestoreDialog = false
                                }
                            }
                        }
                    },
                    enabled = restoreJsonInput.isNotBlank()
                ) {
                    Text("شروع بازیابی")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // 4. Clear Data Confirm Dialog
    if (showClearDataConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataConfirmDialog = false },
            icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("پاکسازی کامل رکوردها") },
            text = {
                Text("آیا از حذف تمام رکوردهای کارکرد، مرخصی‌ها، مساعده‌ها و وام‌ها اطمینان دارید؟ این عملیات غیرقابل بازگشت است.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllLogs()
                        showClearDataConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("بله، پاکسازی کن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataConfirmDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun MoneyInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    val num = value.toLongOrNull() ?: 0L
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() }) },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )
        if (num > 0) {
            Text(
                text = PersianDateUtil.formatCurrency(num),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}
