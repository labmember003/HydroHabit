package com.falcon.hydrohabit.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.features.onboarding.usecase.Gender
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor

@Composable
fun GenderSelector(
    selected: Gender?,
    onSelected: (Gender) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GenderOption("Male", selected == Gender.MALE) { onSelected(Gender.MALE) }
        GenderOption("Female", selected == Gender.FEMALE) { onSelected(Gender.FEMALE) }
        GenderOption("Prefer not to say", selected == Gender.UNSPECIFIED) { onSelected(Gender.UNSPECIFIED) }
    }
}

@Composable
private fun RowScope.GenderOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .border(
                width = 0.5.dp,
                color = if (isSelected) waterColor else Color(0xFFD1D1D6),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                if (isSelected) waterColor else Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = fontFamilyLight,
                fontWeight = FontWeight(400),
                color = if (isSelected) Color.White else primaryBlack,
                textAlign = TextAlign.Center,
            ),
            maxLines = 2,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}
