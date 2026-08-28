package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 38.sp,
    letterSpacing = (-0.5).sp
  ),
  headlineMedium = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.sp
  ),
  titleLarge = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.15.sp
  ),
  titleMedium = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.15.sp
  ),
  bodyLarge = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp
  ),
  labelLarge = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp
  ),
  labelSmall = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
  )
)

// Monospace typography for ledger numbers to ensure tabular column alignment
val LedgerNumeralLarge = TextStyle(
  fontFamily = FontFamily.Monospace,
  fontWeight = FontWeight.Bold,
  fontSize = 22.sp,
  lineHeight = 28.sp,
  letterSpacing = 0.5.sp
)

val LedgerNumeralMedium = TextStyle(
  fontFamily = FontFamily.Monospace,
  fontWeight = FontWeight.SemiBold,
  fontSize = 16.sp,
  lineHeight = 22.sp,
  letterSpacing = 0.25.sp
)

val LedgerNumeralRegular = TextStyle(
  fontFamily = FontFamily.Monospace,
  fontWeight = FontWeight.Normal,
  fontSize = 15.sp,
  lineHeight = 20.sp,
  letterSpacing = 0.2.sp
)

