package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val totalAmount: Long, // in Tomans
    val installmentCount: Int,
    val interestRate: Double = 0.0,
    val monthlyPayment: Long = 0L,
    val paidInstallments: Int = 0,
    val startDate: String = "",
    val note: String = ""
)
