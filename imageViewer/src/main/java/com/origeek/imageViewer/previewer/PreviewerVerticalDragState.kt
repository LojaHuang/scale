package com.origeek.imageViewer.previewer

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2022-10-17 14:42
 **/

// 默认下拉关闭缩放阈值
const val DEFAULT_SCALE_TO_CLOSE_MIN_VALUE = 0.8F

open class PreviewerVerticalDragState : PreviewerTransformState() {

    // 从外部传入viewer容器
    lateinit var viewerContainerState: ViewerContainerState

    // 下拉关闭的缩放的阈值，当scale小于这个值，就关闭，否则还原
    private var scaleToCloseMinValue = DEFAULT_SCALE_TO_CLOSE_MIN_VALUE

    /**
     * 将viewer容器的位置大小复制给transformContent
     */
    private suspend fun copyViewerContainerStateToTransformState() {
        transformState.apply {
            val targetScale = viewerContainerState.scale.value * fitScale
            graphicScaleX.snapTo(targetScale)
            graphicScaleY.snapTo(targetScale)
            val centerOffsetY = (containerSize.height - realSize.height).div(2)
            val centerOffsetX = (containerSize.width - realSize.width).div(2)
            offsetY.snapTo(centerOffsetY + viewerContainerState.offsetY.value)
            offsetX.snapTo(centerOffsetX + viewerContainerState.offsetX.value)
        }
    }

    /**
     * viewer容器缩小关闭
     */
    private suspend fun viewerContainerShrinkDown() {
        stateCloseStart()
        listOf(
            scope.async {
                viewerContainerState.scale.animateTo(0F, animationSpec = defaultAnimationSpec)
            },
            scope.async {
                uiAlpha.animateTo(0F, animationSpec = defaultAnimationSpec)
            }
        ).awaitAll()
        ticket.awaitNextTicket()
        animateContainerVisableState = MutableTransitionState(false)
        ticket.awaitNextTicket()
        viewerContainerState.reset()
        transformState.setExitState()
        stateCloseEnd()
    }

    /**
     * 响应下拉关闭
     */
    private suspend fun dragDownClose(key: Any) {
        transformState.notifyEnterChanged()
        allowLoading = false
        ticket.awaitNextTicket()
        copyViewerContainerStateToTransformState()
        viewerContainerState.resetImmediately()
        transformSnapToViewer(false)
        ticket.awaitNextTicket()
        closeTransform(key, defaultAnimationSpec)
        allowLoading = true
    }

    /**
     * 根据页面获取当前页码所属的key
     */
    internal var getKey: ((Int) -> Any)? = null

    /**
     * 设置下拉手势的方法
     * @param pointerInputScope PointerInputScope
     */
    internal suspend fun verticalDrag(pointerInputScope: PointerInputScope) {
        pointerInputScope.apply {
            var vStartOffset by mutableStateOf<Offset?>(null)
            var vOrientationDown by mutableStateOf<Boolean?>(null)
            if (getKey != null) detectVerticalDragGestures(
                onDragStart = OnDragStart@{
                    // 如果imageViewerState不存在，无法进行下拉手势
                    if (imageViewerState == null) return@OnDragStart
                    var transformItemState: TransformItemState? = null
                    // 查询当前transformItem
                    getKey?.apply {
                        findTransformItem(invoke(currentPage))?.apply {
                            transformItemState = this
                        }
                    }
                    // 更新当前transformItem
                    transformState.itemState = transformItemState
                    // 只有viewer的缩放率为1时才允许下拉手势
                    if (imageViewerState?.scale?.value == 1F) {
                        vStartOffset = it
                        // 进入下拉手势时禁用viewer的手势
                        imageViewerState?.allowGestureInput = false
                    }
                },
                onDragEnd = OnDragEnd@{
                    if (vStartOffset == null) return@OnDragEnd
                    vStartOffset = null
                    vOrientationDown = null
                    imageViewerState?.allowGestureInput = true
                    if (viewerContainerState.scale.value < scaleToCloseMinValue) {
                        scope.launch {
                            if (getKey != null) {
                                val key = getKey!!.invoke(currentPage)
                                val transformItem = findTransformItem(key)
                                if (transformItem != null) {
                                    dragDownClose(key)
                                } else {
                                    viewerContainerShrinkDown()
                                }
                            } else {
                                viewerContainerShrinkDown()
                            }
                            // 结束动画后需要把关闭的UI打开
                            uiAlpha.snapTo(1F)
                        }
                    } else {
                        scope.launch {
                            uiAlpha.animateTo(1F, defaultAnimationSpec)
                        }
                        scope.launch {
                            viewerContainerState.reset()
                        }
                    }
                },
                onVerticalDrag = OnVerticalDrag@{ change, dragAmount ->
                    if (imageViewerState == null) return@OnVerticalDrag
                    if (vStartOffset == null) return@OnVerticalDrag
                    if (vOrientationDown == null) vOrientationDown = dragAmount > 0
                    if (vOrientationDown == true) {
                        val offsetY = change.position.y - vStartOffset!!.y
                        val offsetX = change.position.x - vStartOffset!!.x
                        val containerHeight = viewerContainerState.containerSize.height
                        val scale = (containerHeight - offsetY.absoluteValue).div(
                            containerHeight
                        )
                        scope.launch {
                            uiAlpha.snapTo(scale)
                            viewerContainerState.offsetX.snapTo(offsetX)
                            viewerContainerState.offsetY.snapTo(offsetY)
                            viewerContainerState.scale.snapTo(scale)
                        }
                    } else {
                        // 如果不是向上，就返还输入权，以免页面卡顿
                        imageViewerState?.allowGestureInput = true
                    }
                }
            )
        }
    }

    /**
     * 开启下拉手势
     * @param getKey Function1<Int, Any>?
     * @param minScale Float?
     */
    fun enableVerticalDrag(minScale: Float? = null, getKey: ((Int) -> Any)? = null) {
        minScale?.let { scaleToCloseMinValue = it }
        this.getKey = getKey
    }

    /**
     * 关闭下拉手势
     */
    fun disableVerticalDrag() {
        this.getKey = null
    }
}