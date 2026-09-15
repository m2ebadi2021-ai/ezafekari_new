package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianDateUtil
import com.example.util.SalaryCalculator
import kotlin.math.roundToLong

@Composable
fun CalculatorsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.salarySettings.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        "نرخ ساعتی کار",
        "عیدی و پاداش",
        "سنوات پایان کار",
        "حق شیفت و نوبت‌کاری"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            when (selectedTab) {
                0 -> HourlyRateCalculatorTab(settings.baseSalary, settings.seniorityAllowance)
                1 -> EidiCalculatorTab(settings.baseSalary)
                2 -> SanavatCalculatorTab(settings.baseSalary)
                3 -> ShiftInfoTab()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HourlyRateCalculatorTab(defaultBase: Long, defaultSanavat: Long) {
    var baseText by remember { mutableStateOf((defaultBase + defaultSanavat).toString()) }

    val totalBase = baseText.toLongOrNull() ?: 0L
    // Hourly wage = base / 220
    val hourlyBase = if (totalBase > 0) (totalBase / 220.0).roundToLong() else 0L
    val overtimeHourly = (hourlyBase * 1.4).roundToLong()
    val fridayHourly = (hourlyBase * 1.4).roundToLong()
    val holidayHourly = (hourlyBase * 1.8).roundToLong()
    val nightHourly = (hourlyBase * 1.35).roundToLong()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calculator_hourly_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QueryBuilder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "محاسبه‌گر نرخ هر ساعت کار و اضافه کاری",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = baseText,
                onValueChange = { baseText = PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() } },
                label = { Text("مجموع پایه حقوق و سنوات ماهانه (تومان)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            RateRow("نرخ ۱ ساعت کار عادی (پایه / ۲۲۰)", hourlyBase, MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

            RateRow("نرخ ۱ ساعت اضافه‌کاری عادی (ضریب ۱.۴)", overtimeHourly, AmberAccent)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

            RateRow("نرخ ۱ ساعت جمعه‌کاری (ضریب ۱.۴)", fridayHourly, PrimaryIndigo)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

            RateRow("نرخ ۱ ساعت شب‌کاری (ضریب ۱.۳۵)", nightHourly, Color(0xFF7C3AED))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

            RateRow("نرخ ۱ ساعت تعطیل‌کاری / ویژه (ضریب ۱.۸)", holidayHourly, EmeraldSuccess)
        }
    }
}

@Composable
fun EidiCalculatorTab(defaultBase: Long) {
    var baseText by remember { mutableStateOf(defaultBase.toString()) }
    var monthsWorkedText by remember { mutableStateOf("12") }

    val baseSalary = baseText.toLongOrNull() ?: 0L
    val monthsWorked = monthsWorkedText.toIntOrNull() ?: 12

    val eidiAmount = SalaryCalculator.calculateEidi(baseSalary, monthsWorked)
    val minAnnualEidi = baseSalary * 2
    val maxAnnualEidi = 10_390_968L * 3

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calculator_eidi_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = AmberAccent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "محاسبه عیدی و پاداش سالانه قانون کار",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = baseText,
                onValueChange = { baseText = PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() } },
                label = { Text("حقوق پایه ماهانه (تومان)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = monthsWorkedText,
                onValueChange = { monthsWorkedText = PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() } },
                label = { Text("تعداد ماه‌های کارکرد در سال (۱ تا ۱۲)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AmberContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "مبلغ عیدی و پاداش قابل پرداخت:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = PersianDateUtil.formatCurrency(eidiAmount),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmberAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "📌 بر اساس قانون کار، حداقل عیدی ۶۰ روز (۲ برابر پایه) و حداکثر ۹۰ روز مزد (۳ برابر حداقل حقوق) می‌باشد.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SanavatCalculatorTab(defaultBase: Long) {
    var baseText by remember { mutableStateOf(defaultBase.toString()) }
    var yearsText by remember { mutableStateOf("1") }
    var monthsText by remember { mutableStateOf("0") }

    val baseSalary = baseText.toLongOrNull() ?: 0L
    val years = yearsText.toIntOrNull() ?: 0
    val months = monthsText.toIntOrNull() ?: 0

    val totalYears = years + (months / 12.0)
    val sanavatTotal = SalaryCalculator.calculateSanavatEnd(baseSalary, totalYears)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calculator_sanavat_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HistoryEdu,
                    contentDescription = null,
                    tint = EmeraldSuccess
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "محاسبه حق سنوات پایان خدمت / قطع همکاری",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = baseText,
                onValueChange = { baseText = PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() } },
                label = { Text("آخرین حقوق پایه ماهانه (تومان)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = yearsText,
                    onValueChange = { yearsText = PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() } },
                    label = { Text("تعداد سال‌های سابقه") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = monthsText,
                    onValueChange = { monthsText = PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() } },
                    label = { Text("تعداد ماه‌های مازاد") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EmeraldContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "مبلغ کل حق سنوات پایان کار:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EmeraldDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = PersianDateUtil.formatCurrency(sanavatTotal),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "📌 حق سنوات به ازای هر سال سابقه کار معادل یک ماه آخرین حقوق ثابت و پایه فرد محاسبه می‌گردد.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ShiftInfoTab() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calculator_shift_info_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WorkHistory,
                    contentDescription = null,
                    tint = PrimaryIndigo
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "جدول درصدهای نوبت‌کاری و شیفت قانون کار",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            ShiftItem("شیفت صبح و عصر", "۱۰ درصد", "کار در طول ماه بین صبح و عصر گردش دارد")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

            ShiftItem("شیفت صبح، عصر و شب", "۱۵ درصد", "کار در طول ماه بین سه شیفت صبح، عصر و شب گردش دارد")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

            ShiftItem("شیفت صبح و شب یا عصر و شب", "۲۲.۵ درصد", "کار در طول ماه بین صبح و شب یا عصر و شب گردش دارد")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

            ShiftItem("کار نوبتی شبانه (شب‌کاری ثابت)", "۳۵ درصد", "کارگر غیرنوبتی برای هر ساعت کار بین ۲۲ تا ۶ صبح")

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "📌 این درصدها علاوه بر مزد به کارگران شاغل در شیفت تعلق می‌گیرد.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RateRow(title: String, amount: Long, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = PersianDateUtil.formatCurrency(amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ShiftItem(title: String, percentage: String, description: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimaryContainer
            ) {
                Text(
                    text = percentage,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
