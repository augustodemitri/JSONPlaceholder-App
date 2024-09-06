package com.example.jsonplaceholderapp.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/*
This extension function extracts the initials from a string by taking
the first letter of each word and converting the result to uppercase.
If the string is empty, it returns an empty string.
Example: "John Doe" becomes "JD".
 */
fun String.getInitials(): String {
    return this.split(' ')
        .mapNotNull { it.firstOrNull()?.toString() }
        .reduceOrNull { acc, s -> acc + s }
        ?.uppercase() ?: ""
}

/*
This extension function formats an article's published date from
"dd/MM/yyyy HH:mm:ss" to a more readable format like "MMM dd, yyyy".
Example: "13/07/2023 13:25:21" becomes "Jul 13, 2023".
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