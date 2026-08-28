package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalLedgerColors

@Composable
fun SavedStampBadge(
  modifier: Modifier = Modifier,
  text: String = "PAGE SAVED",
  isAnimated: Boolean = false,
  rotationDegrees: Float = -15f
) {
  val ledgerColors = LocalLedgerColors.current
  val scale = remember { Animatable(if (isAnimated) 1.8f else 1f) }

  LaunchedEffect(isAnimated) {
    if (isAnimated) {
      scale.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
      )
    }
  }

  Box(
    modifier = modifier
      .rotate(rotationDegrees)
      .scale(scale.value)
      .testTag("saved_stamp_badge")
  ) {
    Surface(
      shape = RoundedCornerShape(4.dp),
      color = ledgerColors.ledgerGreenLight.copy(alpha = 0.8f),
      modifier = Modifier.border(
        width = 2.dp,
        color = ledgerColors.ledgerGreen,
        shape = RoundedCornerShape(4.dp)
      )
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Saved Stamp",
          tint = ledgerColors.ledgerGreen,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = text.uppercase(),
          color = ledgerColors.ledgerGreen,
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          letterSpacing = (-0.5).sp
        )
      }
    }
  }
}
