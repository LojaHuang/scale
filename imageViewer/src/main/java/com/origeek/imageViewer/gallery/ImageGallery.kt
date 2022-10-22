package com.origeek.imageViewer.gallery

import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.origeek.imageViewer.previewer.DEFAULT_ITEM_SPACE
import com.origeek.imageViewer.viewer.ImageViewer
import com.origeek.imageViewer.viewer.ImageViewerState
import com.origeek.imageViewer.viewer.rememberViewerState
import kotlinx.coroutines.launch

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2022-10-10 11:50
 **/

class GalleryGestureScope(
    var onTap: () -> Unit = {},
    var onDoubleTap: () -> Boolean = { false },
    var onLongPress: () -> Unit = {},
)

class GalleryLayerScope(
    var viewerContainer: @Composable (
        page: Int, viewerState: ImageViewerState, viewer: @Composable () -> Unit
    ) -> Unit = { _, _, viewer -> viewer() },
    var background: @Composable ((Int) -> Unit) = {},
    var foreground: @Composable ((Int) -> Unit) = {},
)

open class ImageGalleryState(
    @IntRange(from = 0) currentPage: Int = 0,
) {

    internal var pagerState: ImagePagerState = ImagePagerState(currentPage)

    var imageViewerState by mutableStateOf<ImageViewerState?>(null)
        internal set

    val currentPage: Int
        get() = pagerState.currentPage

    val targetPage: Int
        get() = pagerState.targetPage

    val pageCount: Int
        get() = pagerState.pageCount

    val currentPageOffset: Float
        get() = pagerState.currentPageOffset

    val interactionSource: InteractionSource
        get() = pagerState.interactionSource

    suspend fun scrollToPage(
        @IntRange(from = 0) page: Int,
        @FloatRange(from = 0.0, to = 1.0) pageOffset: Float = 0f,
    ) = pagerState.scrollToPage(page, pageOffset)

    suspend fun animateScrollToPage(
        @IntRange(from = 0) page: Int,
        @FloatRange(from = 0.0, to = 1.0) pageOffset: Float = 0f,
    ) = pagerState.animateScrollToPage(page, pageOffset)

    companion object {
        val Saver: Saver<ImageGalleryState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.currentPage,
                )
            },
            restore = {
                val imageGalleryState = ImageGalleryState()
                imageGalleryState.pagerState = ImagePagerState(it[0] as Int)
                imageGalleryState
            }
        )
    }

}

@Composable
fun rememberImageGalleryState(
    @IntRange(from = 0) currentPage: Int = 0,
): ImageGalleryState {
    return rememberSaveable(saver = ImageGalleryState.Saver) { ImageGalleryState(currentPage) }
}

@Composable
fun ImageGallery(
    modifier: Modifier = Modifier,
    count: Int,
    state: ImageGalleryState = rememberImageGalleryState(),
    imageLoader: @Composable (Int) -> Any?,
    itemSpacing: Dp = DEFAULT_ITEM_SPACE,
    detectGesture: GalleryGestureScope.() -> Unit = {},
    galleryLayer: GalleryLayerScope.() -> Unit = {},
) {
    require(count >= 0) { "imageCount must be >= 0" }
    val scope = rememberCoroutineScope()
    // 手势相关
    val galleryGestureScope = remember { GalleryGestureScope() }
    detectGesture.invoke(galleryGestureScope)
    // 图层相关
    val galleryLayerScope = remember { GalleryLayerScope() }
    galleryLayer.invoke(galleryLayerScope)
    // 确保不会越界
    val currentPage by remember(key1 = state.currentPage, key2 = state) {
        derivedStateOf {
            if (state.currentPage >= count) {
                if (count > 0) count - 1 else 0
            } else state.currentPage
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        galleryLayerScope.background(currentPage)
        ImageHorizonPager(
            count = count,
            state = state.pagerState,
            modifier = Modifier
                .fillMaxSize(),
            itemSpacing = itemSpacing,
        ) { page ->
            val imageState = rememberViewerState()
            LaunchedEffect(key1 = currentPage) {
                if (currentPage != page) imageState.reset()
                if (currentPage == page) {
                    state.imageViewerState = imageState
                }
            }
            galleryLayerScope.viewerContainer(page, imageState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    key(count, page) {
                        ImageViewer(
                            modifier = Modifier.fillMaxSize(),
                            model = imageLoader(page),
                            state = imageState,
                            boundClip = false,
                            detectGesture = {
                                this.onTap = {
                                    galleryGestureScope.onTap()
                                }
                                this.onDoubleTap = {
                                    val consumed = galleryGestureScope.onDoubleTap()
                                    if (!consumed) scope.launch {
                                        imageState.toggleScale(it)
                                    }
                                }
                                this.onLongPress = { galleryGestureScope.onLongPress() }
                            },
                        )
                    }
                }
            }
        }
        galleryLayerScope.foreground(currentPage)
    }
}