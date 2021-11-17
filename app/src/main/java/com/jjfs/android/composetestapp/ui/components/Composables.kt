package com.jjfs.android.composetestapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingText(
    text: String =  "LOADING",
    color: Color = MaterialTheme.colors.surface,
    size: TextUnit = 24.sp,
) {
    Text(
        text = text,
        color = color,
        fontSize = size,
        modifier = Modifier.padding(8.dp)
    )
}