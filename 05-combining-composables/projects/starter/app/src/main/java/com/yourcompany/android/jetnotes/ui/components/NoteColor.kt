package com.yourcompany.android.jetnotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NoteColor(
    modifier: Modifier = Modifier,
    color: Color = Color.Red,
    size: Dp = 40.dp,
    border: Dp = 2.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(
                border = BorderStroke(border, SolidColor(Color.Black)),
                shape = CircleShape
            )
    )
}

@Preview(showBackground = true)
@Composable
fun NoteColorPreview() {
    NoteColor(
        modifier = Modifier.padding(1.dp)
    )
}
