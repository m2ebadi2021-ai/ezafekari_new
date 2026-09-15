package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MonthSelectorHeader
import com.example.ui.components.StatCard
import com.example.ui.components.WorkLogItemCard
import com.example.ui.dialogs.AddFinancialDialog
import com.example.ui.dialogs.AddWorkLogBottomSheet
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CrimsonDeduction
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianDateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToLogs: () -> Unit,
    onNavigateToPayslip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val year by viewModel.selectedYear.collectAsState()
    val month by viewModel.selectedMonth.collectAsState()
    val settings by viewModel.salarySettings.collectAsState()
    val workLogs by viewModel.currentMonthWorkLogs.collectAsState()
    val salaryBreakdown by viewModel.currentSalaryBreakdown.collectAsState()

    var showWorkLogSheet by remember { mutableStateOf(false) }
    var workLogSheetType by remember { mutableStateOf("EZAFEKARI") }
    var showFinancialDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val totalOvertimeMinutes = salaryBreakdown.regularOvertimeMinutes + salaryBreakdown.specialOvertimeMinutes
    val totalOvertimePay = salaryBreakdown.regularOvertimePay + salaryBreakdown.specialOvertimePay

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Month Selector Header
            MonthSelectorHeader(
                year = year,
                month = month,
                onPrevMonth = { viewModel.prevMonth() },
                onNextMonth = { viewModel.nextMonth() },
                onSelectMonth = { y, m -> viewModel.setYearAndMonth(y, m) }
            )
        }

        // Hero Monthly Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_dashboard_card"),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryDark, PrimaryIndigo)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = settings.userName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${settings.jobTitle} • ${settings.companyName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = AmberAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${PersianDateUtil.MONTH_NAMES[month - 1]} ${PersianDateUtil.toFaDigits(year)}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Overtime & Salary highlight
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "کل اضافه کاری ماه",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = PersianDateUtil.formatMinutesToHourMin(totalOvertimeMinutes),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberAccent
                                )
                                Text(
                                    text = "مبلغ: ${PersianDateUtil.formatCurrency(totalOvertimePay)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "تخمین خالص دریافتی",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = PersianDateUtil.formatCurrency(salaryBreakdown.netSalary),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "مشاهده فیش حقوقی ←",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable { onNavigateToPayslip() }
                                        .padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick One-Tap Attendance Check-In / Check-Out
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ثبت سریع حضور و غیاب امروز:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Quick Check-In
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.quickCheckIn() }
                                .testTag("btn_quick_check_in"),
                            color = EmeraldContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ثبت ورود با ۱ لمس",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }
                        }

                        // Quick Check-Out
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.quickCheckOut() }
                                .testTag("btn_quick_check_out"),
                            color = PrimaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ثبت خروج با ۱ لمس",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Action Tiles
        item {
            Text(
                text = "دسترسی و ثبت سریع:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionItem(
                    title = "اضافه کاری",
                    icon = Icons.Default.MoreTime,
                    iconBg = AmberAccent,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        workLogSheetType = "EZAFEKARI"
                        showWorkLogSheet = true
                    }
                )
                QuickActionItem(
                    title = "تردد و ساعت",
                    icon = Icons.Default.Schedule,
                    iconBg = PrimaryIndigo,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        workLogSheetType = "TARADOD"
                        showWorkLogSheet = true
                    }
                )
                QuickActionItem(
                    title = "مرخصی",
                    icon = Icons.Default.BeachAccess,
                    iconBg = EmeraldSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        workLogSheetType = "MORAKHASI"
                        showWorkLogSheet = true
                    }
                )
                QuickActionItem(
                    title = "مساعده/مالی",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconBg = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showFinancialDialog = true
                    }
                )
            }
        }

        // Live Monthly Statistics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "نرخ اضافه کاری عادی",
                    value = PersianDateUtil.formatCurrency(salaryBreakdown.regularOvertimeHourlyRate),
                    subtitle = "به ازای هر ساعت کار",
                    icon = Icons.Default.Paid,
                    iconColor = AmberAccent,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "کل کسورات ماه",
                    value = PersianDateUtil.formatCurrency(salaryBreakdown.totalDeductions),
                    subtitle = "بیمه، مالیات، مساعده",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconColor = CrimsonDeduction,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Work Logs header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آخرین ثبت‌های این ماه:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToLogs) {
                    Text("مشاهده همه (${PersianDateUtil.toFaDigits(workLogs.size)})")
                }
            }
        }

        // Recent items
        if (workLogs.isEmpty()) {
            item {
                EmptyStateView(
                    message = "هنوز کارکردی در این ماه ثبت نشده است.\nاز دکمه‌های بالا برای ثبت اضافه کاری یا تردد استفاده کنید."
                )
            }
        } else {
            items(workLogs.take(5)) { log ->
                WorkLogItemCard(
                    log = log,
                    onDelete = { viewModel.deleteWorkLog(log) },
                    dailyWorkMinutes = settings.dailyWorkMinutes,
                    autoOvertime = settings.autoOvertimeOnExcessWork
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showWorkLogSheet) {
        AddWorkLogBottomSheet(
            sheetState = sheetState,
            initialType = workLogSheetType,
            standardDailyWorkMinutes = settings.dailyWorkMinutes,
            autoOvertimeOnExcessWork = settings.autoOvertimeOnExcessWork,
            onDismiss = { showWorkLogSheet = false },
            onSave = { type, subType, date, timeFrom, timeTo, duration, note ->
                viewModel.addWorkLog(type, subType, date, timeFrom, timeTo, duration, note)
            },
            onSaveDailyLeaveRange = { subType, dates, dailyMinutes, note ->
                viewModel.addDailyLeaveRange(subType, dates, dailyMinutes, note)
            }
        )
    }

    if (showFinancialDialog) {
        AddFinancialDialog(
            initialType = "MOSAEDE",
            onDismiss = { showFinancialDialog = false },
            onSave = { type, title, amount, date, note ->
                viewModel.addFinancialLog(type, title, amount, date, note)
            }
        )
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("quick_action_${title}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBg,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
