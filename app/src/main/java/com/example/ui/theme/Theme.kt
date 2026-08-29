package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class LedgerExtendedColors(
  val ledgerPaper: Color,
  val ledgerPaperSurface: Color,
  val ledgerPaperShaded: Color,
  val headerSurface: Color,
  val headerContent: Color,
  val headerContentMuted: Color,
  val inkNavy: Color,
  val inkNavyLight: Color,
  val ruledLine: Color,
  val ruledLineStrong: Color,
  val ledgerGreen: Color,
  val ledgerGreenLight: Color,
  val ledgerGreenBorder: Color,
  val stampAmber: Color,
  val stampAmberLight: Color,
  val charcoal: Color,
  val mutedCharcoal: Color,
  val placeholderColor: Color,
  val softRed: Color,
  val softRedLight: Color,
  val softRedBg: Color,
  val heroCardContainer: Color,
  val heroCardContent: Color,
  val tableHeaderBg: Color
)

val LocalLedgerColors = staticCompositionLocalOf {
  LedgerExtendedColors(
    ledgerPaper = LedgerPaper,
    ledgerPaperSurface = LedgerPaperSurface,
    ledgerPaperShaded = LedgerPaperShaded,
    headerSurface = HeaderSurfaceLight,
    headerContent = HeaderContentLight,
    headerContentMuted = HeaderContentMutedLight,
    inkNavy = InkNavy,
    inkNavyLight = InkNavyLight,
    ruledLine = RuledLine,
    ruledLineStrong = RuledLineStrong,
    ledgerGreen = LedgerGreen,
    ledgerGreenLight = LedgerGreenLight,
    ledgerGreenBorder = LedgerGreenBorder,
    stampAmber = StampAmber,
    stampAmberLight = StampAmberLight,
    charcoal = Charcoal,
    mutedCharcoal = MutedCharcoal,
    placeholderColor = PlaceholderLight,
    softRed = SoftRed,
    softRedLight = SoftRedLight,
    softRedBg = SoftRedBg,
    heroCardContainer = HeroCardContainerLight,
    heroCardContent = Color.White,
    tableHeaderBg = TableHeaderContainerLight
  )
}

private val DarkColorScheme = darkColorScheme(
  primary = StampAmberDarkTheme,
  onPrimary = Color.Black,
  primaryContainer = StampAmberLightDark,
  onPrimaryContainer = StampAmberDarkTheme,
  secondary = LedgerGreenDark,
  onSecondary = Color.Black,
  secondaryContainer = LedgerGreenLightDark,
  onSecondaryContainer = LedgerGreenDark,
  tertiary = InkNavyDark,
  onTertiary = Color.Black,
  background = LedgerPaperDark,
  onBackground = CharcoalDark,
  surface = LedgerPaperSurfaceDark,
  onSurface = CharcoalDark,
  surfaceVariant = LedgerPaperShadedDark,
  onSurfaceVariant = MutedCharcoalDark,
  outline = RuledLineDark,
  outlineVariant = RuledLineStrongDark,
  error = SoftRedDark,
  onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
  primary = StampAmber,
  onPrimary = Color.White,
  primaryContainer = StampAmberLight,
  onPrimaryContainer = StampAmberDark,
  secondary = LedgerGreen,
  onSecondary = Color.White,
  secondaryContainer = LedgerGreenLight,
  onSecondaryContainer = LedgerGreen,
  tertiary = InkNavy,
  onTertiary = Color.White,
  background = LedgerPaper,
  onBackground = Charcoal,
  surface = LedgerPaperSurface,
  onSurface = Charcoal,
  surfaceVariant = LedgerPaperShaded,
  onSurfaceVariant = MutedCharcoal,
  outline = RuledLine,
  outlineVariant = RuledLineStrong,
  error = SoftRed,
  onError = Color.White
)

val DarkExtendedColors = LedgerExtendedColors(
  ledgerPaper = LedgerPaperDark,
  ledgerPaperSurface = LedgerPaperSurfaceDark,
  ledgerPaperShaded = LedgerPaperShadedDark,
  headerSurface = HeaderSurfaceDark,
  headerContent = HeaderContentDark,
  headerContentMuted = HeaderContentMutedDark,
  inkNavy = InkNavyDark,
  inkNavyLight = InkNavyLightDark,
  ruledLine = RuledLineDark,
  ruledLineStrong = RuledLineStrongDark,
  ledgerGreen = LedgerGreenDark,
  ledgerGreenLight = LedgerGreenLightDark,
  ledgerGreenBorder = LedgerGreenBorderDark,
  stampAmber = StampAmberDarkTheme,
  stampAmberLight = StampAmberLightDark,
  charcoal = CharcoalDark,
  mutedCharcoal = MutedCharcoalDark,
  placeholderColor = PlaceholderDark,
  softRed = SoftRedDark,
  softRedLight = SoftRedLightDark,
  softRedBg = SoftRedBgDark,
  heroCardContainer = HeroCardContainerDark,
  heroCardContent = Color.White,
  tableHeaderBg = TableHeaderContainerDark
)

val LightExtendedColors = LedgerExtendedColors(
  ledgerPaper = LedgerPaper,
  ledgerPaperSurface = LedgerPaperSurface,
  ledgerPaperShaded = LedgerPaperShaded,
  headerSurface = HeaderSurfaceLight,
  headerContent = HeaderContentLight,
  headerContentMuted = HeaderContentMutedLight,
  inkNavy = InkNavy,
  inkNavyLight = InkNavyLight,
  ruledLine = RuledLine,
  ruledLineStrong = RuledLineStrong,
  ledgerGreen = LedgerGreen,
  ledgerGreenLight = LedgerGreenLight,
  ledgerGreenBorder = LedgerGreenBorder,
  stampAmber = StampAmber,
  stampAmberLight = StampAmberLight,
  charcoal = Charcoal,
  mutedCharcoal = MutedCharcoal,
  placeholderColor = PlaceholderLight,
  softRed = SoftRed,
  softRedLight = SoftRedLight,
  softRedBg = SoftRedBg,
  heroCardContainer = HeroCardContainerLight,
  heroCardContent = Color.White,
  tableHeaderBg = TableHeaderContainerLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

  CompositionLocalProvider(LocalLedgerColors provides extendedColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}

