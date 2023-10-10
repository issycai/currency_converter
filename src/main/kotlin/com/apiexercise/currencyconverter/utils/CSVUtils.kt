package com.apiexercise.currencyconverter.utils

import java.io.File

object CSVUtils {
    fun parseCSV(filePath: String): List<Map<String, String>> {
        val rows = mutableListOf<Map<String, String>>()

        val bufferedReader = File(filePath).bufferedReader()
        val header = bufferedReader.readLine()?.split(",") ?: throw IllegalArgumentException("No header found")

        bufferedReader.useLines { lines ->
            lines.forEach { line ->
                val cols = line.split(",")
                val row = header.zip(cols).toMap()
                rows.add(row)
            }
        }

        return rows
    }
}