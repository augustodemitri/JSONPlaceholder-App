package com.example.jsonplaceholderapp.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun String.getInitials(): String {
    return this.split(' ')
        .mapNotNull { it.firstOrNull()?.toString() }
        .reduceOrNull { acc, s -> acc + s }
        ?.uppercase() ?: ""
}

/*
This extension functions lets us parse the published date of an article and
display it in a more readable way. Eg: "13/07/2023 13:25:21" --> "Jul 13, 2023"
 */
fun String.toFormattedDate(): String {
    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    return try {
        val date = LocalDateTime.parse(this, inputFormatter)
        date.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        "Invalid Date"
    }
}