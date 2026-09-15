package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "work_logs")
data class WorkLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /**
     * Types: "EZAFEKARI" (اضافه کاری), "TARADOD" (تردد), "MORAKHASI" (مرخصی), "MAMORIAT" (ماموریت)
     */
    val type: String,
    /**
     * SubTypes:
     * For EZAFEKARI: "ORDINARY" (عادی - ضریب ۱.۴), "SPECIAL" (تعطیل/ویژه - ضریب ۱.۸)
     * For MORAKHASI: "HOURLY" (ساعتی), "DAILY" (روزانه), "ESTEHLAGHI" (استحقاقی), "ESTELAJI" (استعلاجی)
     * For MAMORIAT: "HOURLY", "DAILY"
     */
    val subType: String = "ORDINARY",
    val date: String, // e.g. "1403/06/15"
    val timeFrom: String = "00:00",
    val timeTo: String = "00:00",
    val durationMinutes: Int = 0,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
