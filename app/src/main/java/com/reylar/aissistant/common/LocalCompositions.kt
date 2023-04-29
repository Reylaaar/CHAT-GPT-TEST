package com.reylar.aissistant.common

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.reylar.aissistant.OpenMessageViewModel


val localNavController =
    compositionLocalOf<NavHostController> { error("No localNavController found!") }

val localMessageViewModel =
    compositionLocalOf <OpenMessageViewModel>{ error("No OpenMessageViewModel found!")  }