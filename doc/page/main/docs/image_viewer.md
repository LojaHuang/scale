# ImageViewer

`ImageViewer`是一个图片放大缩小查看的组件，提供了默认配置，简化组件的使用流程

## 🍭 基本使用
```kotlin
val painter = painterResource(id = R.drawable.light_02)
val state = rememberZoomableState(contentSize = painter.intrinsicSize)
ImageViewer(model = painter, state = state)
```

⚠️ ‼️ 这里需要注意的是，提供图片的固有尺寸是必须的，没有的话`ImageViewer`不会正常显示

## 🍰 结合Coil使用
```kotlin
val painter = rememberAsyncImagePainter(model = R.drawable.light_02)
val state = rememberZoomableState(contentSize = painter.intrinsicSize)
ImageViewer(model = painter, state = state)
```

## 🍨 自定义内容

`ImageViewer`通过传人的`model`类型来自动选择使用何种方式进行图片显示，与`Image`类似，默认支持`Painter`、`ImageBitmap`、`ImageVector`，也支持通过`AnyComposable`传入一个`Composable`

```kotlin
// 设定显示内容的固有大小
val rectSize = 100.dp
val density = LocalDensity.current
val rectSizePx = density.run { rectSize.toPx() }
val size = Size(rectSizePx, rectSizePx)
val state = rememberZoomableState(contentSize = size)
ImageViewer(
    state = state,
    model = AnyComposable {
        // 自定义内容
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Cyan)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.6F)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.BottomEnd)
            )
            Text(
                modifier = Modifier.align(Alignment.Center), 
                text = "Hello Compose"
            )
        }
    }
)
```

但是，事实上这里并不推荐使用`AnyComposable`，`ImageViewer`是对`ZoomableView`进行封装而来，有较高的定制化需求可以考虑直接使用 [`ZoomableView`](zoomable_view.md)

<a id="imageviewermodelprocessor"></a>
## 🧁 类型拓展

`ImageViewer`可以通过`ModelProcessor`增加`model`支持的类型

```kotlin
val stringProcessorPair: ModelProcessorPair = String::class to { model, _ ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center), 
            text = model as String
        )
    }
}
```

如上述代码所示，声明一个`ModelProcessorPair`，类型为`Pair<String,@Composable (Any, ZoomableViewState) -> Unit>`,
即传入`String`类型的`model`时，将按此方式进行显示

```kotlin
val message = "好家伙"
val state = rememberZoomableState(contentSize = Size(100F, 100F))
ImageViewer(
    model = message,
    state = state,
    processor = ModelProcessor(stringProcessorPair)
)
```

`Scale`提供了对大型图片二次采样的支持，详情见文档：[`SamplingDecoder`](sampling_decoder.md)

```kotlin
ImageViewer(
    processor = ModelProcessor(samplingProcessorPair)
)
```

## 🍦 手势与状态

`ImageViewer`手势事件回调为`ZoomableGestureScope`，状态与控制使用`ZoomableViewState`，见文档 [`ZoomableView ZoomableGestureScope ZoomableViewState`](zoomable_view.md#zoomablegesturescope)