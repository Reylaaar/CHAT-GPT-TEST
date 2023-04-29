package com.reylar.aissistant.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.reylar.aissistant.ui.theme.Gray100

@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    maxLines: Int = 1,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    verticalPadding: Dp = 16.dp,
    boxModifier: Modifier? = null,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {

    val dividerState = remember {
        mutableStateOf(true)
    }

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                dividerState.value = !it.isFocused
            },
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        maxLines = maxLines,
        textStyle = style,
        value = value,
        onValueChange = onValueChange,
        decorationBox = { innerTextField ->
            Box(
                modifier = if (boxModifier != null)
                    boxModifier
                else Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (dividerState.value) Gray100
                        else if (isError) Color.Red
                        else MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier.padding(vertical = verticalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingIcon != null) {
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp)
                        ) {
                            leadingIcon()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                    ) {
                        if (value.isEmpty()) {
                            if (placeholder != null) {
                                placeholder()
                            }
                        }
                        innerTextField()
                    }
                    if (trailingIcon != null) {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                        ) {
                            trailingIcon()
                        }
                    }
                }
            }
        },
        visualTransformation = visualTransformation,
    )
}