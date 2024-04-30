package com.jvziyaoyao.viewer.sample

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import com.jvziyaoyao.image.viewer.ImageCanvas
import com.jvziyaoyao.image.viewer.getViewPort
import com.jvziyaoyao.viewer.sample.base.BaseActivity
import com.jvziyaoyao.viewer.sample.ui.component.rememberDecoderImagePainter
import com.jvziyaoyao.zoomable.zoomable.ZoomableGestureScope
import com.jvziyaoyao.zoomable.zoomable.ZoomableView
import com.jvziyaoyao.zoomable.zoomable.rememberZoomableState
import com.origeek.imageViewer.viewer.ImageViewer
import com.origeek.imageViewer.viewer.rememberViewerState
import kotlinx.coroutines.launch

class HugeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBasicContent {
//            HugeBody()
            HugeBody01()
        }
    }

}

@Composable
fun HugeBody() {
    val context = LocalContext.current
    val inputStream = remember { context.assets.open("a350.jpg") }
    val imageDecoder = rememberDecoderImagePainter(inputStream = inputStream)
    val scope = rememberCoroutineScope()
    val state = rememberViewerState()
    ImageViewer(
        model = imageDecoder,
        state = state,
        boundClip = false,
        detectGesture = {
            onDoubleTap = {
                scope.launch {
                    state.toggleScale(it)
                }
            }
        },
    )
}

@Composable
fun HugeBody01() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val inputStream = remember { context.assets.open("a350.jpg") }
    val imageDecoder = rememberDecoderImagePainter(inputStream = inputStream)
    val zoomableState = rememberZoomableState(
        contentSize = if (imageDecoder == null) Size.Zero else Size(
            width = imageDecoder.decoderWidth.toFloat(),
            height = imageDecoder.decoderHeight.toFloat()
        )
    )
    ZoomableView(
        modifier = Modifier
            .fillMaxSize(),
        state = zoomableState,
        boundClip = false,
        detectGesture = ZoomableGestureScope(
            onDoubleTap = {
                scope.launch {
                    zoomableState.toggleScale(it)
                }
            },
        ),
    ) {
        if (imageDecoder != null) {
            val viewPort = zoomableState.getViewPort()
            ImageCanvas(
                imageDecoder = imageDecoder,
                viewPort = viewPort,
            )
        }
    }
}