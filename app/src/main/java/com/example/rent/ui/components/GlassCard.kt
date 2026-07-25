package com.example.rent.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rent.ui.theme.GlassBorder
import com.example.rent.ui.theme.GlassSurface

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderColor: Color = GlassBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable { onClick() }
    } else {
        modifier
    }

    Surface(
        modifier = cardModifier,
        shape = shape,
        color = GlassSurface,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}
