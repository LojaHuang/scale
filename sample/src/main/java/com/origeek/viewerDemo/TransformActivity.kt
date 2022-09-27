package com.origeek.viewerDemo

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.origeek.imageViewer.*
import com.origeek.viewerDemo.base.BaseActivity
import com.origeek.viewerDemo.ui.component.LazyGridLayout
import com.origeek.viewerDemo.ui.component.rememberCoilImagePainter
import com.origeek.viewerDemo.ui.theme.ViewerDemoTheme
import java.util.*
import java.util.stream.Collectors

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2022-09-21 18:20
 **/
class TransformActivity : BaseActivity() {

    private fun getItemList(time: Int = 1): List<DrawableItem> {
        val srcList = listOf(
            R.drawable.img_01,
            R.drawable.img_02,
            R.drawable.img_03,
            R.drawable.img_04,
            R.drawable.img_05,
            R.drawable.img_06,
        )
        val resList = mutableListOf<Int>()
        for (i in 0 until time) {
            resList.addAll(srcList)
        }
        return resList.stream().map {
            DrawableItem(
                id = UUID.randomUUID().toString(),
                res = it
            )
        }.collect(Collectors.toList())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val images = getItemList()
        setBasicContent {
            ViewerDemoTheme {
                TransformBody(images)
            }
        }
    }

}

data class DrawableItem(
    val id: String,
    val res: Int,
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TransformBody(images: List<DrawableItem>) {
    val transformContentState = rememberTransformContentState()
    val previewerState = rememberPreviewerState(transformState = transformContentState)
    val lineCount = 3
    if (previewerState.canClose) BackHandler {
        val index = previewerState.currentPage
        val id = images[index].id
        previewerState.closeTransform(id)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3F)
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "🎈 Transform")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .weight(7F)
        ) {
            LazyGridLayout(
                modifier = Modifier.fillMaxSize(),
                columns = lineCount,
                size = images.size,
                padding = 2.dp,
            ) { index ->
                val item = images[index]
                val painter = painterResource(id = item.res)
                val itemState = rememberTransformItemState()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1F)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                previewerState.openTransform(
                                    index = index,
                                    itemState = itemState,
                                )
                            }
                        }
                ) {
                    TransformImageView(
                        painter = painter,
                        itemState = itemState,
                        key = item.id,
                        contentState = transformContentState,
                    )
                }
            }
        }
    }
    ImagePreviewer(
        modifier = Modifier.fillMaxSize(),
        count = images.size,
        state = previewerState,
        imageLoader = { index ->
            val image = images[index].res
            rememberCoilImagePainter(image = image)
        },
        currentViewerState = {},
    )
}