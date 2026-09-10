package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexusTeal

@Composable
fun AvatarView(
    name: String,
    emoji: String = "👤",
    backgroundColor: Long = 0xFF0A9396,
    size: Dp = 48.dp,
    hasStatusRing: Boolean = false,
    isOnline: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val ringModifier = if (hasStatusRing) {
            Modifier
                .size(size)
                .border(
                    width = 2.5.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            NexusTeal,
                            Color(0xFF4361EE),
                            Color(0xFF7209B7),
                            NexusTeal
                        )
                    ),
                    shape = CircleShape
                )
        } else Modifier

        Box(
            modifier = ringModifier
                .size(if (hasStatusRing) size - 6.dp else size)
                .clip(CircleShape)
                .background(Color(backgroundColor)),
            contentAlignment = Alignment.Center
        ) {
            if (emoji.isNotBlank()) {
                Text(
                    text = emoji,
                    fontSize = (size.value * 0.45f).sp
                )
            } else {
                Text(
                    text = name.take(2).uppercase(),
                    color = Color.White,
                    fontSize = (size.value * 0.4f).sp,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF06D6A0))
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}
