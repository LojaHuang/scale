package com.origeek.viewerDemo

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.origeek.imageViewer.viewer.ImageViewer
import com.origeek.imageViewer.viewer.rememberViewerState
import com.origeek.viewerDemo.base.BaseActivity
import com.origeek.viewerDemo.ui.component.rememberDecoderImagePainter
import com.origeek.viewerDemo.ui.theme.ViewerDemoTheme
import kotlinx.coroutines.launch

class HugeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBasicContent {
            ViewerDemoTheme {
                HugeBody()
            }
        }
    }

}

@Composable
fun HugeBody() {
    val context = LocalContext.current
    val imageDecoder = rememberDecoderImagePainter(inputStream = context.assets.open("a350.jpg"))
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
