package com.example.ui.dialogs

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.util.PersianDateUtil
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AddFinancialDialog(
    initialType: String = "MOSAEDE",
    onDismiss: () -> Unit,
    onSave: (type: String, title: String, amount: Long, date: String, note: String) -> Unit
) {
    val today = remember { PersianDateUtil.today() }
    val yesterday = remember { PersianDateUtil.yesterday() }

    var selectedType by remember { mutableStateOf(initialType) }
    var title by remember {
        mutableStateOf(
            when (initialType) {
                "MOSAEDE" -> "مساعده حقوق"
                "PADASH" -> "پاداش بهره‌وری"
                "FOOD" -> "هزینه ناهار/غذا"
                "AYAB_ZAHAB" -> "ایاب و ذهاب"
                else -> "سایر"
            }
        )
    }
    var amountText by remember { mutableStateOf("1000000") }
    var note by remember { mutableStateOf("") }

    var year by remember { mutableIntStateOf(today.year) }
    var month by remember { mutableIntStateOf(today.month) }
    var day by remember { mutableIntStateOf(today.day) }

    var showDatePickerDialog by remember { mutableStateOf(false) }

    val formattedDate = String.format(Locale.US, "%04d/%02d/%02d", year, month, day)
    val parsedAmount = amountText.toLongOrNull() ?: 0L

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("dialog_add_financial")
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
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ثبت امور مالی و مساعده",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "بستن")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Type selector
                Text(
                    text = "نوع تراکنش:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val types = listOf(
                        "MOSAEDE" to "مساعده حقوق (کسر)",
                        "PADASH" to "پاداش (افزایش)",
                        "AYAB_ZAHAB" to "ایاب و ذهاب (افزایش)",
                        "SAYER_MAZAYA" to "سایر مزایا (افزایش)",
                        "FOOD" to "هزینه ناهار/غذا (کسر)",
                        "SAYER_KOSORAT" to "کسورات متفرقه (کسر)"
                    )
                    types.forEach { (typeKey, label) ->
                        val isSelected = selectedType == typeKey
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedType = typeKey
                                title = when (typeKey) {
                                    "MOSAEDE" -> "مساعده حقوق"
                                    "PADASH" -> "پاداش تشویقی"
                                    "AYAB_ZAHAB" -> "کمک‌هزینه ایاب و ذهاب"
                                    "SAYER_MAZAYA" -> "سایر مزایای نقدی"
                                    "FOOD" -> "هزینه غذا"
                                    "SAYER_KOSORAT" -> "کسورات متفرقه"
                                    else -> "تراکنش مالی"
                                }
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان تراکنش") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = PersianDateUtil.toEnDigits(it).filter { c -> c.isDigit() }
                    },
                    label = { Text("مبلغ به تومان") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )

                if (parsedAmount > 0) {
                    Text(
                        text = PersianDateUtil.formatCurrency(parsedAmount),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Quick Amount Add Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val quickAmounts = listOf(
                        "۵۰۰ هزار" to 500_000L,
                        "۱ میلیون" to 1_000_000L,
                        "۲ میلیون" to 2_000_000L,
                        "۵ میلیون" to 5_000_000L,
                        "۱۰ میلیون" to 10_000_000L
                    )
                    quickAmounts.forEach { (lbl, amt) ->
                        FilterChip(
                            selected = parsedAmount == amt,
                            onClick = { amountText = amt.toString() },
                            label = { Text(lbl, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date Picker field
                Text(
                    text = "تاریخ تراکنش:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDatePickerDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${PersianDateUtil.toFaDigits(day)} ${PersianDateUtil.MONTH_NAMES[month - 1]} ${PersianDateUtil.toFaDigits(year)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "تغییر تاریخ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
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
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Today, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text("امروز", style = MaterialTheme.typography.labelSmall) }
                    )

                    FilterChip(
                        selected = year == yesterday.year && month == yesterday.month && day == yesterday.day,
                        onClick = {
                            year = yesterday.year
                            month = yesterday.month
                            day = yesterday.day
                        },
                        label = { Text("دیروز", style = MaterialTheme.typography.labelSmall) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("توضیحات (اختیاری)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
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
                            if (parsedAmount > 0 && title.isNotBlank()) {
                                onSave(selectedType, title, parsedAmount, formattedDate, note)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = parsedAmount > 0
                    ) {
                        Text("ذخیره مورد مالی", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

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
            }
        )
    }
}
