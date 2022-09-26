package com.origeek.viewerDemo.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.origeek.viewerDemo.ui.theme.ViewerDemoTheme
import kotlin.math.ceil

@Composable
fun BasePage(content: @Composable () -> Unit) {
    ViewerDemoTheme {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )
            }
            content()
        }
    }
}

/**
 * @program: ImageGallery
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2022-07-28 23:06
 **/

@Composable
fun GridLayout(
    modifier: Modifier = Modifier,
    columns: Int,
    size: Int,
    padding: Dp = 0.dp,
    block: @Composable (Int) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val line = ceil(size.toDouble() / columns).toInt()
        val halfPadding = padding.div(2)
        for (c in 0 until line) {
            if (c != 0) Spacer(modifier = Modifier.height(halfPadding))
            Row(modifier = Modifier.fillMaxWidth()) {
                for (r in 0 until columns) {
                    val index = c * columns + r
                    if (r != 0) Spacer(modifier = Modifier.width(halfPadding))
                    Box(
                        modifier = Modifier
                            .weight(1F)
                    ) {
                        if (index < size) {
                            block(index)
                        }
                    }
                    if (r != columns - 1) Spacer(modifier = Modifier.width(halfPadding))
                }
            }
            if (c != line - 1) Spacer(modifier = Modifier.height(halfPadding))
        }
    }
}

@Composable
fun LazyGridLayout(
    modifier: Modifier = Modifier,
    columns: Int,
    size: Int,
    padding: Dp = 0.dp,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    block: @Composable (Int) -> Unit,
) {
    val line = ceil(size.toDouble() / columns).toInt()
    val halfPadding = padding.div(2)
    LazyColumn(
        modifier = modifier,
        state = state,
        content = {
            items(count = line, key = { it }) { c ->
                if (c != 0) Spacer(modifier = Modifier.height(halfPadding))
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (r in 0 until columns) {
                        val index = c * columns + r
                        if (r != 0) Spacer(modifier = Modifier.width(halfPadding))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            if (index < size) {
                                block(index)
                            }
                        }
                        if (r != columns - 1) Spacer(modifier = Modifier.width(halfPadding))
                    }
                }
                if (c != line - 1) Spacer(modifier = Modifier.height(halfPadding))
            }
        }, contentPadding = contentPadding
    )
}