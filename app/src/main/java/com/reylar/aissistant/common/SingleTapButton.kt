package com.reylar.aissistant.common

import android.os.SystemClock
import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.reylar.aissistant.ui.theme.Gray300
import com.reylar.aissistant.ui.theme.Gray50
import com.reylar.aissistant.ui.theme.Gray800


@Composable
fun SingleTapButton(
    modifier: Modifier = Modifier,
    clickDisablePeriod: Long = 500L,
    updateBgColor: Color? = null,
    updateTxtColor: Color? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    var lastClickTime by remember {
        mutableStateOf(0L)
    }
    Button(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp)),
        onClick = {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickDisablePeriod) {
            } else {
                lastClickTime = SystemClock.elapsedRealtime()
                onClick()
            }
        },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Gray50,
            disabledContentColor = Gray300,
            containerColor = updateBgColor ?: Gray800,
            contentColor = updateTxtColor ?: Color.White,
        ),
        content = content,
    )
}

