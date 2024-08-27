package com.example.jsonplaceholderapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun UserAvatarComponent(initials: String?, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = initials ?: "?",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
        )
    }
}