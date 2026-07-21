package com.falcon.hydrohabit.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlackLight
import com.falcon.hydrohabit.ui.theme.waterColorMeter

enum class WeightUnit(val label: String) {
    KG("Kg"),
    LBS("Lbs"),
}

private const val LBS_TO_KG = 0.453592f

fun WeightUnit.toKg(value: Float): Float =
    if (this == WeightUnit.LBS) value * LBS_TO_KG else value

fun WeightUnit.fromKg(kg: Float): Float =
    if (this == WeightUnit.LBS) kg / LBS_TO_KG else kg

@Composable
fun WeightUnitSelector(
    selected: WeightUnit,
    onSelected: (WeightUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(end = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(0.5.dp, Color(0xFFD1D1D6), RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WeightUnit.entries.forEach { unit ->
            val isSelected = unit == selected
            Text(
                text = unit.label,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(if (isSelected) 500 else 400),
                    color = if (isSelected) Color.White else primaryBlackLight,
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) waterColorMeter else Color.Transparent)
                    .clickable { onSelected(unit) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
    }
}
