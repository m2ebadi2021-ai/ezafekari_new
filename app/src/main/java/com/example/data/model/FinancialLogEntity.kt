package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_logs")
data class FinancialLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /**
     * Types:
     * "MOSAEDE" (مساعده)
     * "PADASH" (پاداش)
     * "FOOD" (غذا)
     * "AYAB_ZAHAB" (ایاب و ذهاب)
     * "SAYER_KOSORAT" (سایر کسورات)
     * "SAYER_MAZAYA" (سایر مزایا)
     */
    val type: String,
    val title: String,
    val amount: Long, // in Tomans
    val date: String, // e.g. "1403/06/15"
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
