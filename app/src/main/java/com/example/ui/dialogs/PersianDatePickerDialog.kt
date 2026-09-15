package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonDeduction
import com.example.ui.theme.PrimaryIndigo
import com.example.util.PersianDateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersianDatePickerDialog(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int,
    onDismiss: () -> Unit,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit
) {
    val today = remember { PersianDateUtil.today() }
    val yesterday = remember { PersianDateUtil.yesterday() }

    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedMonth by remember { mutableIntStateOf(initialMonth) }
    var selectedDay by remember { mutableIntStateOf(initialDay) }

    // Displayed month in the picker view
    var viewYear by remember { mutableIntStateOf(initialYear) }
    var viewMonth by remember { mutableIntStateOf(initialMonth) }

    val daysInMonth = remember(viewYear, viewMonth) {
        PersianDateUtil.daysInPersianMonth(viewYear, viewMonth)
    }

    val startDayOfWeekIndex = remember(viewYear, viewMonth) {
        PersianDateUtil.getPersianDayOfWeekIndex(viewYear, viewMonth, 1)
    }

    val selectedDayOfWeek = remember(selectedYear, selectedMonth, selectedDay) {
        PersianDateUtil.getDayOfWeekName(selectedYear, selectedMonth, selectedDay)
    }

    val monthName = PersianDateUtil.MONTH_NAMES.getOrElse(viewMonth - 1) { "" }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("dialog_persian_date_picker")
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Selected Date Display
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "انتخاب تاریخ شمسی",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$selectedDayOfWeek، ${PersianDateUtil.toFaDigits(selectedDay)} ${PersianDateUtil.MONTH_NAMES.getOrElse(selectedMonth - 1) { "" }} ${PersianDateUtil.toFaDigits(selectedYear)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Presets: امروز، دیروز
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    val isTodaySelected = selectedYear == today.year && selectedMonth == today.month && selectedDay == today.day
                    val isYesterdaySelected = selectedYear == yesterday.year && selectedMonth == yesterday.month && selectedDay == yesterday.day

                    FilterChip(
                        selected = isTodaySelected,
                        onClick = {
                            selectedYear = today.year
                            selectedMonth = today.month
                            selectedDay = today.day
                            viewYear = today.year
                            viewMonth = today.month
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text("امروز (${PersianDateUtil.toFaDigits(today.day)} ${today.monthName})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    FilterChip(
                        selected = isYesterdaySelected,
                        onClick = {
                            selectedYear = yesterday.year
                            selectedMonth = yesterday.month
                            selectedDay = yesterday.day
                            viewYear = yesterday.year
                            viewMonth = yesterday.month
                        },
                        label = { Text("دیروز") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Month Navigation Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (viewMonth == 12) {
                                viewMonth = 1
                                viewYear += 1
                            } else {
                                viewMonth += 1
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ماه بعد",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "$monthName ${PersianDateUtil.toFaDigits(viewYear)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = {
                            if (viewMonth == 1) {
                                viewMonth = 12
                                viewYear -= 1
                            } else {
                                viewMonth -= 1
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "ماه قبل",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Days of week row
                val weekShortLabels = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    weekShortLabels.forEachIndexed { index, label ->
                        val isFriday = index == 6
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isFriday) CrimsonDeduction else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Calendar Days Grid (Total cells = startDayOfWeekIndex + daysInMonth)
                val totalCells = startDayOfWeekIndex + daysInMonth
                val gridRows = (totalCells + 6) / 7

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (row in 0 until gridRows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0..6) {
                                val cellIndex = row * 7 + col
                                val dayNum = cellIndex - startDayOfWeekIndex + 1

                                if (dayNum in 1..daysInMonth) {
                                    val isSelected = selectedYear == viewYear &&
                                            selectedMonth == viewMonth &&
                                            selectedDay == dayNum
                                    val isToday = today.year == viewYear &&
                                            today.month == viewMonth &&
                                            today.day == dayNum
                                    val isFriday = col == 6

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isSelected -> MaterialTheme.colorScheme.primary
                                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable {
                                                selectedYear = viewYear
                                                selectedMonth = viewMonth
                                                selectedDay = dayNum
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = PersianDateUtil.toFaDigits(dayNum),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                                isFriday -> CrimsonDeduction
                                                isToday -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }
                                } else {
                                    // Empty slot
                                    Spacer(modifier = Modifier.size(38.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons (Confirm / Cancel)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("انصراف")
                    }

                    Button(
                        onClick = {
                            onDateSelected(selectedYear, selectedMonth, selectedDay)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "انتخاب تاریخ",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
