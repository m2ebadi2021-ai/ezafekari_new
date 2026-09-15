package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryIndigo
import com.example.util.PersianDateUtil
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddWorkLogBottomSheet(
    sheetState: SheetState,
    initialType: String = "EZAFEKARI",
    standardDailyWorkMinutes: Int = 440,
    autoOvertimeOnExcessWork: Boolean = true,
    onDismiss: () -> Unit,
    onSave: (
        type: String,
        subType: String,
        date: String,
        timeFrom: String,
        timeTo: String,
        durationMinutes: Int,
        note: String
    ) -> Unit,
    onSaveDailyLeaveRange: ((subType: String, dates: List<String>, dailyMinutes: Int, note: String) -> Unit)? = null
) {
    val today = remember { PersianDateUtil.today() }
    val yesterday = remember { PersianDateUtil.yesterday() }

    var selectedType by remember { mutableStateOf(initialType) }
    var selectedSubType by remember {
        mutableStateOf(
            if (initialType == "EZAFEKARI") "ORDINARY"
            else if (initialType == "MORAKHASI") "DAILY"
            else "HOURLY"
        )
    }

    var year by remember { mutableIntStateOf(today.year) }
    var month by remember { mutableIntStateOf(today.month) }
    var day by remember { mutableIntStateOf(today.day) }

    var toYear by remember { mutableIntStateOf(today.year) }
    var toMonth by remember { mutableIntStateOf(today.month) }
    var toDay by remember { mutableIntStateOf(today.day) }

    var fromHour by remember { mutableIntStateOf(16) }
    var fromMin by remember { mutableIntStateOf(0) }
    var toHour by remember { mutableIntStateOf(19) }
    var toMin by remember { mutableIntStateOf(30) }

    var note by remember { mutableStateOf("") }

    // Dialog pickers visibility states
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showToDatePickerDialog by remember { mutableStateOf(false) }
    var showFromTimePicker by remember { mutableStateOf(false) }
    var showToTimePicker by remember { mutableStateOf(false) }

    val isDailyLeave = selectedType == "MORAKHASI" && selectedSubType != "HOURLY"

    val formattedDate = String.format(Locale.US, "%04d/%02d/%02d", year, month, day)
    val formattedToDate = String.format(Locale.US, "%04d/%02d/%02d", toYear, toMonth, toDay)
    val timeFromStr = String.format(Locale.US, "%02d:%02d", fromHour, fromMin)
    val timeToStr = String.format(Locale.US, "%02d:%02d", toHour, toMin)

    val durationMinutes by remember(fromHour, fromMin, toHour, toMin) {
        derivedStateOf {
            PersianDateUtil.calculateMinutesBetween(timeFromStr, timeToStr)
        }
    }

    val dailyLeaveDaysCount by remember(year, month, day, toYear, toMonth, toDay) {
        derivedStateOf {
            PersianDateUtil.daysBetweenInclusive(year, month, day, toYear, toMonth, toDay)
        }
    }

    val dayOfWeekName = remember(year, month, day) {
        PersianDateUtil.getDayOfWeekName(year, month, day)
    }

    val toDayOfWeekName = remember(toYear, toMonth, toDay) {
        PersianDateUtil.getDayOfWeekName(toYear, toMonth, toDay)
    }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 5.dp),
                shape = RoundedCornerShape(2.5.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ثبت کارکرد و تردد جدید",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "مدیریت دقیق ساعات اضافه کاری، مرخصی و شیفت",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "بستن")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Category Selector Chips
            Text(
                text = "دسته‌بندی ثبت:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val types = listOf(
                    "EZAFEKARI" to "اضافه کاری",
                    "TARADOD" to "تردد / ورود و خروج",
                    "MORAKHASI" to "مرخصی",
                    "MAMORIAT" to "مأموریت"
                )
                types.forEach { (typeKey, label) ->
                    val isSelected = selectedType == typeKey
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedType = typeKey
                            selectedSubType = if (typeKey == "EZAFEKARI") "ORDINARY" else "HOURLY"
                        },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Sub-category (Ordinary vs Special)
            AnimatedVisibility(visible = selectedType == "EZAFEKARI") {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "نوع اضافه کاری:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedSubType == "ORDINARY",
                            onClick = { selectedSubType = "ORDINARY" },
                            label = { Text("عادی (ضریب ۱.۴)") }
                        )
                        FilterChip(
                            selected = selectedSubType == "SPECIAL",
                            onClick = { selectedSubType = "SPECIAL" },
                            label = { Text("تعطیل / جمعه / ویژه (ضریب ۱.۸)") }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = selectedType == "MORAKHASI") {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "نوع مرخصی:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedSubType == "DAILY",
                            onClick = { selectedSubType = "DAILY" },
                            label = { Text("روزانه (استحقاقی)") }
                        )
                        FilterChip(
                            selected = selectedSubType == "HOURLY",
                            onClick = { selectedSubType = "HOURLY" },
                            label = { Text("ساعتی") }
                        )
                        FilterChip(
                            selected = selectedSubType == "ESTELAJI",
                            onClick = { selectedSubType = "ESTELAJI" },
                            label = { Text("استعلاجی / پزشکی") }
                        )
                        FilterChip(
                            selected = selectedSubType == "BI_HOQOQ",
                            onClick = { selectedSubType = "BI_HOQOQ" },
                            label = { Text("بدون حقوق") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isDailyLeave) {
                // ================================================================
                // DATE RANGE SELECTION FOR DAILY LEAVE (از روز تا روز)
                // ================================================================
                Text(
                    text = "انتخاب بازه تاریخی مرخصی (از روز تا روز):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Card 1: از روز (تاریخ شروع مرخصی)
                Text(
                    text = "از روز (تاریخ شروع مرخصی):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showDatePickerDialog = true }
                        .testTag("card_leave_from_date")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "$dayOfWeekName، ${PersianDateUtil.toFaDigits(day)} ${PersianDateUtil.MONTH_NAMES[month - 1]} ${PersianDateUtil.toFaDigits(year)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = "انتخاب تاریخ شروع",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = year == today.year && month == today.month && day == today.day,
                        onClick = {
                            year = today.year
                            month = today.month
                            day = today.day
                            if (toYear < year || (toYear == year && toMonth < month) || (toYear == year && toMonth == month && toDay < day)) {
                                toYear = year
                                toMonth = month
                                toDay = day
                            }
                        },
                        label = { Text("از امروز", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = false,
                        onClick = {
                            val tomorrow = PersianDateUtil.addDaysToPersianDate(today.year, today.month, today.day, 1)
                            year = tomorrow.year
                            month = tomorrow.month
                            day = tomorrow.day
                            if (toYear < year || (toYear == year && toMonth < month) || (toYear == year && toMonth == month && toDay < day)) {
                                toYear = year
                                toMonth = month
                                toDay = day
                            }
                        },
                        label = { Text("از فردا", style = MaterialTheme.typography.labelSmall) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 2: تا روز (تاریخ پایان مرخصی)
                Text(
                    text = "تا روز (تاریخ پایان مرخصی):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showToDatePickerDialog = true }
                        .testTag("card_leave_to_date")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.EventAvailable,
                                        contentDescription = null,
                                        tint = EmeraldDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "$toDayOfWeekName، ${PersianDateUtil.toFaDigits(toDay)} ${PersianDateUtil.MONTH_NAMES[toMonth - 1]} ${PersianDateUtil.toFaDigits(toYear)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formattedToDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = "انتخاب تاریخ پایان",
                            tint = EmeraldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Quick range preset chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = toYear == year && toMonth == month && toDay == day,
                        onClick = {
                            toYear = year
                            toMonth = month
                            toDay = day
                        },
                        label = { Text("همان روز (۱ روز)", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = dailyLeaveDaysCount == 2,
                        onClick = {
                            val next = PersianDateUtil.addDaysToPersianDate(year, month, day, 1)
                            toYear = next.year
                            toMonth = next.month
                            toDay = next.day
                        },
                        label = { Text("+۱ روز (۲ روزه)", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = dailyLeaveDaysCount == 3,
                        onClick = {
                            val next = PersianDateUtil.addDaysToPersianDate(year, month, day, 2)
                            toYear = next.year
                            toMonth = next.month
                            toDay = next.day
                        },
                        label = { Text("+۲ روز (۳ روزه)", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = dailyLeaveDaysCount == 5,
                        onClick = {
                            val next = PersianDateUtil.addDaysToPersianDate(year, month, day, 4)
                            toYear = next.year
                            toMonth = next.month
                            toDay = next.day
                        },
                        label = { Text("+۴ روز (۵ روزه)", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = dailyLeaveDaysCount == 7,
                        onClick = {
                            val next = PersianDateUtil.addDaysToPersianDate(year, month, day, 6)
                            toYear = next.year
                            toMonth = next.month
                            toDay = next.day
                        },
                        label = { Text("یک هفته (۷ روز)", style = MaterialTheme.typography.labelSmall) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Summary Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = EmeraldContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "مجموع مدت مرخصی: ${PersianDateUtil.toFaDigits(dailyLeaveDaysCount)} روز کامل",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "معادل ${PersianDateUtil.formatMinutesToHourMin(dailyLeaveDaysCount * 440)} (بر مبنای ۷:۲۰ در روز)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldDark.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }

            } else {
                // ==========================================
                // 1. MODERN DATE SELECTION (تقویم شمسی لمسی)
                // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تاریخ روز:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "برای انتخاب روز لمس کنید",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            // Date Card with Day of Week
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showDatePickerDialog = true }
                    .testTag("card_select_date")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "$dayOfWeekName، ${PersianDateUtil.toFaDigits(day)} ${PersianDateUtil.MONTH_NAMES[month - 1]} ${PersianDateUtil.toFaDigits(year)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = "تغییر تاریخ",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Date Action Chips: امروز، دیروز، پریروز
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isToday = year == today.year && month == today.month && day == today.day
                val isYesterday = year == yesterday.year && month == yesterday.month && day == yesterday.day

                FilterChip(
                    selected = isToday,
                    onClick = {
                        year = today.year
                        month = today.month
                        day = today.day
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Today, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    label = { Text("امروز", style = MaterialTheme.typography.labelSmall) }
                )

                FilterChip(
                    selected = isYesterday,
                    onClick = {
                        year = yesterday.year
                        month = yesterday.month
                        day = yesterday.day
                    },
                    label = { Text("دیروز", style = MaterialTheme.typography.labelSmall) }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // 2. MODERN TIME SELECTION (ساعت شروع و پایان)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بازه زمانی (ساعت شروع و پایان):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "لمس برای تغییر ساعت",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // From Time Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showFromTimePicker = true }
                        .testTag("card_from_time"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "از ساعت",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = PersianDateUtil.toFaDigits(timeFromStr),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "تغییر ساعت",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // To Time Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showToTimePicker = true }
                        .testTag("card_to_time"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "تا ساعت",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = PersianDateUtil.toFaDigits(timeToStr),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "تغییر ساعت",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Duration Preset Chips (انتخاب فوق‌العاده سریع مدت اضافه کاری)
            Text(
                text = "افزودن سریع مدت اضافه‌کاری از ساعت شروع:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val durationPresets = listOf(
                    "+۳۰ دقیقه" to 30,
                    "+۱ ساعت" to 60,
                    "+۱:۳۰ ساعت" to 90,
                    "+۲ ساعت" to 120,
                    "+۲:۳۰ ساعت" to 150,
                    "+۳ ساعت" to 180,
                    "+۴ ساعت" to 240,
                    "+۵ ساعت" to 300
                )

                durationPresets.forEach { (label, addedMinutes) ->
                    val totalMins = (fromHour * 60 + fromMin + addedMinutes) % (24 * 60)
                    val targetH = totalMins / 60
                    val targetM = totalMins % 60
                    val isCurrent = toHour == targetH && toMin == targetM

                    FilterChip(
                        selected = isCurrent,
                        onClick = {
                            toHour = targetH
                            toMin = targetM
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Calculated Duration Indicator Pill
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (durationMinutes > 0) EmeraldContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (durationMinutes > 0) EmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مجموع مدت زمان:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (durationMinutes > 0) EmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = PersianDateUtil.formatMinutesToHourMin(durationMinutes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (durationMinutes > 0) EmeraldDark else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (selectedType == "TARADOD" && autoOvertimeOnExcessWork && durationMinutes > standardDailyWorkMinutes) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AmberContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "محاسبه خودکار اضافه کاری تردد",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "حضور بیش از موظفی روزانه (${PersianDateUtil.formatMinutesToHourMin(standardDailyWorkMinutes)}) است: ${PersianDateUtil.formatMinutesToHourMin(durationMinutes - standardDailyWorkMinutes)} به عنوان اضافه‌کاری منظور می‌شود.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

            Spacer(modifier = Modifier.height(16.dp))

            // Note field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(if (isDailyLeave) "علت یا توضیحات مرخصی (اختیاری)" else "توضیحات یا بابت (اختیاری)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    if (isDailyLeave) {
                        val dateRange = PersianDateUtil.getDateRange(year, month, day, toYear, toMonth, toDay)
                        if (onSaveDailyLeaveRange != null) {
                            onSaveDailyLeaveRange(
                                selectedSubType,
                                dateRange.map { it.format() },
                                440,
                                note
                            )
                        } else {
                            val total = dateRange.size
                            for ((idx, pd) in dateRange.withIndex()) {
                                val dayNote = if (total > 1) {
                                    if (note.isNotBlank()) "$note (روز ${PersianDateUtil.toFaDigits(idx + 1)} از ${PersianDateUtil.toFaDigits(total)})"
                                    else "مرخصی روزانه (روز ${PersianDateUtil.toFaDigits(idx + 1)} از ${PersianDateUtil.toFaDigits(total)})"
                                } else {
                                    note.ifBlank { "مرخصی روزانه" }
                                }
                                onSave(
                                    "MORAKHASI",
                                    selectedSubType,
                                    pd.format(),
                                    "08:00",
                                    "15:20",
                                    440,
                                    dayNote
                                )
                            }
                        }
                    } else {
                        onSave(
                            selectedType,
                            selectedSubType,
                            formattedDate,
                            timeFromStr,
                            timeToStr,
                            durationMinutes,
                            note
                        )
                    }
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_submit_work_log"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isDailyLeave) {
                        "ثبت مرخصی ${PersianDateUtil.toFaDigits(dailyLeaveDaysCount)} روزه"
                    } else {
                        "ثبت نهایی در سامانه"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // ==========================================
    // POPUP PICKERS
    // ==========================================
    if (showDatePickerDialog) {
        PersianDatePickerDialog(
            initialYear = year,
            initialMonth = month,
            initialDay = day,
            onDismiss = { showDatePickerDialog = false },
            onDateSelected = { y, m, d ->
                year = y
                month = m
                day = d
                if (toYear < year || (toYear == year && toMonth < month) || (toYear == year && toMonth == month && toDay < day)) {
                    toYear = y
                    toMonth = m
                    toDay = d
                }
            }
        )
    }

    if (showToDatePickerDialog) {
        PersianDatePickerDialog(
            initialYear = toYear,
            initialMonth = toMonth,
            initialDay = toDay,
            onDismiss = { showToDatePickerDialog = false },
            onDateSelected = { y, m, d ->
                toYear = y
                toMonth = m
                toDay = d
            }
        )
    }

    if (showFromTimePicker) {
        ModernTimePickerDialog(
            title = "انتخاب ساعت شروع",
            initialHour = fromHour,
            initialMinute = fromMin,
            onDismiss = { showFromTimePicker = false },
            onTimeSelected = { h, m ->
                fromHour = h
                fromMin = m
            }
        )
    }

    if (showToTimePicker) {
        ModernTimePickerDialog(
            title = "انتخاب ساعت پایان",
            initialHour = toHour,
            initialMinute = toMin,
            onDismiss = { showToTimePicker = false },
            onTimeSelected = { h, m ->
                toHour = h
                toMin = m
            }
        )
    }
}
