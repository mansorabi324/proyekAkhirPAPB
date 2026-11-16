package com.example.satuhati.core.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val localeId = Locale("id", "ID")

    fun formatDateTime(timeMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy - HH.mm", localeId)
        return sdf.format(Date(timeMillis))
    }

    fun formatDateOnly(timeMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", localeId)
        return sdf.format(Date(timeMillis))
    }

    fun formatShortDate(timeMillis: Long): String {
        val sdf = SimpleDateFormat("d MMMM", localeId)
        return sdf.format(Date(timeMillis))
    }

    fun formatMonthYear(timeMillis: Long): String {
        val sdf = SimpleDateFormat("MMMM yyyy", localeId)
        return sdf.format(Date(timeMillis))
    }
}
