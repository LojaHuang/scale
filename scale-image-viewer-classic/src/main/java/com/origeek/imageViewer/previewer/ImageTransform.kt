package com.origeek.imageViewer.previewer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.jvziyaoyao.scale.zoomable.previewer.DEFAULT_SOFT_ANIMATION_SPEC
import com.jvziyaoyao.scale.zoomable.previewer.ItemStateMap
import com.jvziyaoyao.scale.zoomable.previewer.LocalTransformItemStateMap
import com.jvziyaoyao.scale.zoomable.previewer.TransformItemState
import com.jvziyaoyao.scale.zoomable.previewer.TransformItemView
import com.jvziyaoyao.scale.zoomable.previewer.rememberTransformItemState
import com.jvziyaoyao.scale.zoomable.zoomable.ensureScale
import com.jvziyaoyao.scale.zoomable.zoomable.zeroOne
import com.origeek.imageViewer.viewer.commonDeprecatedText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2022-09-22 10:13
 **/

@Deprecated(
    message = commonDeprecatedText,
)
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    painter: Painter,
    key: Any,
    itemState: TransformItemState = rememberTransformItemState(),
    previewerState: ImagePreviewerState,
) {
    TransformImageView(
        modifier = modifier,
        key = key,
        itemState = itemState,
        contentState = previewerState.transformState,
    ) { itemKey ->
        key(itemKey) {
            LaunchedEffect(key1 = painter.intrinsicSize) {
                if (painter.intrinsicSize.isSpecified) {
                    itemState.intrinsicSize = painter.intrinsicSize
                }
            }
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Deprecated(
    message = commonDeprecatedText,
)
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    key: Any,
    itemState: TransformItemState = rememberTransformItemState(),
    previewerState: ImagePreviewerState,
) {
    TransformImageView(
        modifier = modifier,
        key = key,
        itemState = itemState,
        previewerState = previewerState,
    ) {
        itemState.intrinsicSize = Size(
            bitmap.width.toFloat(),
            bitmap.height.toFloat()
        )
        Image(
            modifier = Modifier.fillMaxSize(),
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}

@Deprecated(
    message = commonDeprecatedText,
)
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    key: Any,
    itemState: TransformItemState = rememberTransformItemState(),
    previewerState: ImagePreviewerState,
) {
    TransformImageView(
        modifier = modifier,
        key = key,
        itemState = itemState,
        previewerState = previewerState,
    ) {
        LocalDensity.current.run {
            itemState.intrinsicSize = Size(
                imageVector.defaultWidth.toPx(),
                imageVector.defaultHeight.toPx(),
            )
        }
        Image(
            modifier = Modifier.fillMaxSize(),
            imageVector = imageVector,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}

@Deprecated(
    message = commonDeprecatedText,
)
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    key: Any,
    itemState: TransformItemState = rememberTransformItemState(),
    previewerState: ImagePreviewerState,
    content: @Composable (Any) -> Unit,
) = TransformImageView(modifier, key, itemState, previewerState.transformState, content)

@Deprecated(
    message = commonDeprecatedText,
)
@Composable
fun TransformImageView(
    modifier: Modifier = Modifier,
    key: Any,
    itemState: TransformItemState = rememberTransformItemState(),
    contentState: TransformContentState? = rememberTransformContentState(),
    content: @Composable (Any) -> Unit,
) {
    TransformItemView(
        modifier = modifier,
        key = key,
        itemState = itemState,
        contentState = contentState,
    ) {
        content(key)
    }
}

@Deprecated(
    message = commonDeprecatedText,
)
@Composable
fun TransformItemView(
    modifier: Modifier = Modifier,
    key: Any,
    itemState: TransformItemState = rememberTransformItemState(),
    contentState: TransformContentState?,
    content: @Composable (Any) -> Unit,
) {
    TransformItemView(
        modifier = modifier,
        key = key,
        itemState = itemState,
        itemVisible = contentState?.itemState != itemState || !contentState.onAction,
        content = content,
    )
}

@Deprecated(
    message = commonDeprecatedText,
)
@Composable
fun TransformContentView(
    transformContentState: TransformContentState = rememberTransformContentState(),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                transformContentState.containerSize = it.size
                transformContentState.containerOffset = it.positionInRoot()
            },
    ) {
        if (
            transformContentState.srcCompose != null
            && transformContentState.onAction
        ) {
            Box(
                modifier = Modifier
                    .offset(
                        x = LocalDensity.current.run { (transformContentState.offsetX.value).toDp() },
                        y = LocalDensity.current.run { (transformContentState.offsetY.value).toDp() },
                    )
                    .size(
                        width = LocalDensity.current.run { transformContentState.displayWidth.value.toDp() },
                        height = LocalDensity.current.run { transformContentState.displayHeight.value.toDp() },
                    )
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0F, 0F)
                        scaleX = transformContentState.graphicScaleX.value
                        scaleY = transformContentState.graphicScaleY.value
                    },
            ) {
                transformContentState.srcCompose!!(transformContentState.itemState?.key ?: Unit)
            }
        }
    }
}

@Deprecated(
    message = commonDeprecatedText,
)
class TransformContentState(
    // 协程作用域
    var scope: CoroutineScope = MainScope(),
    // 默认动画窗格
    var defaultAnimationSpec: AnimationSpec<Float> = DEFAULT_SOFT_ANIMATION_SPEC,
    // 用于获取transformItemState
    var itemStateMap: ItemStateMap,
) {

    var itemState: TransformItemState? by mutableStateOf(null)

    val intrinsicSize: Size
        get() = itemState?.intrinsicSize ?: Size.Zero

    val intrinsicRatio: Float
        get() {
            if (intrinsicSize.height == 0F) return 1F
            return intrinsicSize.width.div(intrinsicSize.height)
        }

    val srcPosition: Offset
        get() {
            val offset = itemState?.blockPosition ?: Offset.Zero
            return offset.copy(x = offset.x - containerOffset.x, y = offset.y - containerOffset.y)
        }

    val srcSize: IntSize
        get() = itemState?.blockSize ?: IntSize.Zero

    val srcCompose: (@Composable (Any) -> Unit)?
        get() = itemState?.blockCompose

    var onAction by mutableStateOf(false)

    var onActionTarget by mutableStateOf<Boolean?>(null)

    var displayWidth = Animatable(0F)

    var displayHeight = Animatable(0F)

    var graphicScaleX = Animatable(1F)

    var graphicScaleY = Animatable(1F)

    var offsetX = Animatable(0F)

    var offsetY = Animatable(0F)

    var containerOffset by mutableStateOf(Offset.Zero)

    private var containerSizeState = mutableStateOf(IntSize.Zero)

    var containerSize: IntSize
        get() = containerSizeState.value
        set(value) {
            containerSizeState.value = value
            if (value.width != 0 && value.height != 0) {
                scope.launch {
                    specifierSizeFlow.emit(true)
                }
            }
        }

    var specifierSizeFlow = MutableStateFlow(false)

    val containerRatio: Float
        get() {
            if (containerSize.height == 0) return 1F
            return containerSize.width.toFloat().div(containerSize.height)
        }

    val widthFixed: Boolean
        get() = intrinsicRatio > containerRatio

    val fitSize: Size
        get() {
            return if (intrinsicRatio > containerRatio) {
                // 宽度一致
                val uW = containerSize.width
                val uH = uW / intrinsicRatio
                Size(uW.toFloat(), uH)
            } else {
                // 高度一致
                val uH = containerSize.height
                val uW = uH * intrinsicRatio
                Size(uW, uH.toFloat())
            }
        }

    val fitOffsetX: Float
        get() {
            return (containerSize.width - fitSize.width).div(2)
        }

    val fitOffsetY: Float
        get() {
            return (containerSize.height - fitSize.height).div(2)
        }

    val fitScale: Float
        get() {
            return fitSize.width.div(displayRatioSize.width.zeroOne()).ensureScale()
        }

    val displayRatioSize: Size
        get() {
            return Size(width = srcSize.width.toFloat(), height = srcSize.width.div(intrinsicRatio.zeroOne()))
        }

    val realSize: Size
        get() {
            return Size(
                width = displayWidth.value * graphicScaleX.value,
                height = displayHeight.value * graphicScaleY.value,
            )
        }

    suspend fun awaitContainerSizeSpecifier() {
        specifierSizeFlow.takeWhile { !it }.collect {}
    }

    fun findTransformItem(key: Any) = itemStateMap[key]

    fun clearTransformItems() = itemStateMap.clear()

    fun setEnterState() {
        onAction = true
        onActionTarget = null
    }

    fun setExitState() {
        onAction = false
        onActionTarget = null
    }

    suspend fun notifyEnterChanged() {
        scope.launch {
            listOf(
                scope.async {
                    displayWidth.snapTo(displayRatioSize.width)
                },
                scope.async {
                    displayHeight.snapTo(displayRatioSize.height)
                },
                scope.async {
                    graphicScaleX.snapTo(fitScale)
                },
                scope.async {
                    graphicScaleY.snapTo(fitScale)
                },
                scope.async {
                    offsetX.snapTo(fitOffsetX)
                },
                scope.async {
                    offsetY.snapTo(fitOffsetY)
                },
            ).awaitAll()
        }
    }

    suspend fun exitTransform(
        animationSpec: AnimationSpec<Float>? = null
    ) = suspendCoroutine<Unit> { c ->
        val currentAnimateSpec = animationSpec ?: defaultAnimationSpec
        scope.launch {
            listOf(
                scope.async {
                    displayWidth.animateTo(srcSize.width.toFloat(), currentAnimateSpec)
                },
                scope.async {
                    displayHeight.animateTo(srcSize.height.toFloat(), currentAnimateSpec)
                },
                scope.async {
                    graphicScaleX.animateTo(1F, currentAnimateSpec)
                },
                scope.async {
                    graphicScaleY.animateTo(1F, currentAnimateSpec)
                },
                scope.async {
                    offsetX.animateTo(srcPosition.x, currentAnimateSpec)
                },
                scope.async {
                    offsetY.animateTo(srcPosition.y, currentAnimateSpec)
                },
            ).awaitAll()
            onAction = false
            onActionTarget = null
            c.resume(Unit)
        }
    }

    suspend fun enterTransform(
        itemState: TransformItemState,
        animationSpec: AnimationSpec<Float>? = null
    ) = suspendCoroutine<Unit> { c ->
        val currentAnimationSpec = animationSpec ?: defaultAnimationSpec
        this.itemState = itemState

        displayWidth = Animatable(srcSize.width.toFloat())
        displayHeight = Animatable(srcSize.height.toFloat())
        graphicScaleX = Animatable(1F)
        graphicScaleY = Animatable(1F)

        offsetX = Animatable(srcPosition.x)
        offsetY = Animatable(srcPosition.y)

        onActionTarget = true
        onAction = true

        scope.launch {
            reset(currentAnimationSpec)
            c.resume(Unit)
            onActionTarget = null
        }
    }

    suspend fun reset(animationSpec: AnimationSpec<Float>? = null) {
        val currentAnimationSpec = animationSpec ?: defaultAnimationSpec
        listOf(
            scope.async {
                displayWidth.animateTo(displayRatioSize.width, currentAnimationSpec)
            },
            scope.async {
                displayHeight.animateTo(displayRatioSize.height, currentAnimationSpec)
            },
            scope.async {
                graphicScaleX.animateTo(fitScale, currentAnimationSpec)
            },
            scope.async {
                graphicScaleY.animateTo(fitScale, currentAnimationSpec)
            },
            scope.async {
                offsetX.animateTo(fitOffsetX, currentAnimationSpec)
            },
            scope.async {
                offsetY.animateTo(fitOffsetY, currentAnimationSpec)
            },
        ).awaitAll()
    }

    companion object {
        val Saver: Saver<TransformContentState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.onAction,
                )
            },
            restore = {
                val transformContentState =
                    TransformContentState(itemStateMap = mutableMapOf())
                transformContentState.onAction = it[0] as Boolean
                transformContentState
            }
        )
    }

}

@Deprecated(
    message = commonDeprecatedText,
)
@Composable
fun rememberTransformContentState(
    scope: CoroutineScope = rememberCoroutineScope(),
    animationSpec: AnimationSpec<Float> = DEFAULT_SOFT_ANIMATION_SPEC
): TransformContentState {
    val transformItemState = LocalTransformItemStateMap.current
    val transformContentState = rememberSaveable(saver = TransformContentState.Saver) {
        TransformContentState(itemStateMap = transformItemState)
    }
    transformContentState.scope = scope
    transformContentState.defaultAnimationSpec = animationSpec
    return transformContentState
}