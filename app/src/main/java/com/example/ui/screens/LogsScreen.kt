package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FinancialItemCard
import com.example.ui.components.LoanItemCard
import com.example.ui.components.MonthSelectorHeader
import com.example.ui.components.WorkLogItemCard
import com.example.ui.dialogs.AddFinancialDialog
import com.example.ui.dialogs.AddLoanDialog
import com.example.ui.dialogs.AddWorkLogBottomSheet
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianDateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val year by viewModel.selectedYear.collectAsState()
    val month by viewModel.selectedMonth.collectAsState()
    val workLogs by viewModel.currentMonthWorkLogs.collectAsState()
    val financialLogs by viewModel.currentMonthFinancialLogs.collectAsState()
    val loans by viewModel.allLoans.collectAsState()
    val settings by viewModel.salarySettings.collectAsState()

    var activeFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var showWorkLogSheet by remember { mutableStateOf(false) }
    var workLogSheetType by remember { mutableStateOf("EZAFEKARI") }
    var showFinancialDialog by remember { mutableStateOf(false) }
    var showLoanDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Filtered logs
    val filteredWorkLogs = remember(workLogs, activeFilter, searchQuery) {
        val base = when (activeFilter) {
            "ALL" -> workLogs
            "EZAFEKARI" -> workLogs.filter { it.type == "EZAFEKARI" }
            "TARADOD" -> workLogs.filter { it.type == "TARADOD" }
            "MORAKHASI" -> workLogs.filter { it.type == "MORAKHASI" }
            "MAMORIAT" -> workLogs.filter { it.type == "MAMORIAT" }
            else -> emptyList()
        }
        if (searchQuery.isBlank()) {
            base
        } else {
            val q = searchQuery.trim()
            base.filter {
                it.note.contains(q, ignoreCase = true) ||
                        it.date.contains(q) ||
                        it.subType.contains(q, ignoreCase = true)
            }
        }
    }

    val filteredTotalMinutes = remember(filteredWorkLogs) {
        filteredWorkLogs.sumOf { it.durationMinutes }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    when (activeFilter) {
                        "FINANCIAL" -> showFinancialDialog = true
                        "LOANS" -> showLoanDialog = true
                        "TARADOD" -> {
                            workLogSheetType = "TARADOD"
                            showWorkLogSheet = true
                        }
                        "MORAKHASI" -> {
                            workLogSheetType = "MORAKHASI"
                            showWorkLogSheet = true
                        }
                        "MAMORIAT" -> {
                            workLogSheetType = "MAMORIAT"
                            showWorkLogSheet = true
                        }
                        else -> {
                            workLogSheetType = "EZAFEKARI"
                            showWorkLogSheet = true
                        }
                    }
                },
                modifier = Modifier.testTag("fab_add_log"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = when (activeFilter) {
                        "FINANCIAL" -> "ثبت مورد مالی"
                        "LOANS" -> "ثبت وام جدید"
                        "TARADOD" -> "ثبت تردد"
                        "MORAKHASI" -> "ثبت مرخصی"
                        "MAMORIAT" -> "ثبت مأموریت"
                        else -> "ثبت جدید"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Month Selector
                MonthSelectorHeader(
                    year = year,
                    month = month,
                    onPrevMonth = { viewModel.prevMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onSelectMonth = { y, m -> viewModel.setYearAndMonth(y, m) }
                )
            }

            // Search Bar & Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("جستجو در یادداشت، تاریخ...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "پاک کردن")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            // Filter Chips Bar with counts
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val overtimeCount = workLogs.count { it.type == "EZAFEKARI" }
                    val attendanceCount = workLogs.count { it.type == "TARADOD" }
                    val leaveCount = workLogs.count { it.type == "MORAKHASI" }
                    val missionCount = workLogs.count { it.type == "MAMORIAT" }

                    val filters = listOf(
                        "ALL" to "همه موارد (${PersianDateUtil.toFaDigits(workLogs.size)})",
                        "EZAFEKARI" to "اضافه کاری (${PersianDateUtil.toFaDigits(overtimeCount)})",
                        "TARADOD" to "تردد (${PersianDateUtil.toFaDigits(attendanceCount)})",
                        "MORAKHASI" to "مرخصی (${PersianDateUtil.toFaDigits(leaveCount)})",
                        "MAMORIAT" to "مأموریت (${PersianDateUtil.toFaDigits(missionCount)})",
                        "FINANCIAL" to "مالی و مساعده (${PersianDateUtil.toFaDigits(financialLogs.size)})",
                        "LOANS" to "وام‌ها و اقساط (${PersianDateUtil.toFaDigits(loans.size)})"
                    )
                    items(filters) { (key, label) ->
                        val isSelected = activeFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeFilter = key },
                            label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Summary Pill for filtered work hours
            if (activeFilter != "FINANCIAL" && activeFilter != "LOANS" && filteredTotalMinutes > 0) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مجموع مدت زمان نمایش داده شده:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = PersianDateUtil.formatMinutesToHourMin(filteredTotalMinutes),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Financial list if selected
            if (activeFilter == "FINANCIAL") {
                if (financialLogs.isEmpty()) {
                    item {
                        EmptyStateView(message = "مورد مالی یا مساعده‌ای در این ماه ثبت نشده است.")
                    }
                } else {
                    items(financialLogs) { finItem ->
                        FinancialItemCard(
                            item = finItem,
                            onDelete = { viewModel.deleteFinancialLog(finItem) }
                        )
                    }
                }
            } else if (activeFilter == "LOANS") {
                if (loans.isEmpty()) {
                    item {
                        EmptyStateView(message = "وامی ثبت نشده است. با دکمه ثبت وام جدید می‌توانید وام‌های خود را مدیریت کنید.")
                    }
                } else {
                    items(loans) { loan ->
                        LoanItemCard(
                            loan = loan,
                            onPayInstallment = { viewModel.markLoanInstallmentPaid(loan) },
                            onDelete = { viewModel.deleteLoan(loan) }
                        )
                    }
                }
            } else {
                // Work logs list
                if (filteredWorkLogs.isEmpty()) {
                    item {
                        EmptyStateView(message = "هیچ رکوردی در این بخش برای ماه انتخابی ثبت نشده است.")
                    }
                } else {
                    items(filteredWorkLogs) { log ->
                        WorkLogItemCard(
                            log = log,
                            onDelete = { viewModel.deleteWorkLog(log) },
                            dailyWorkMinutes = settings.dailyWorkMinutes,
                            autoOvertime = settings.autoOvertimeOnExcessWork
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
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

    if (showLoanDialog) {
        AddLoanDialog(
            onDismiss = { showLoanDialog = false },
            onSave = { title, totalAmount, installmentCount, interestRate, monthlyPayment, startDate, note ->
                viewModel.addLoan(title, totalAmount, installmentCount, interestRate, monthlyPayment, startDate, note)
            }
        )
    }
}
