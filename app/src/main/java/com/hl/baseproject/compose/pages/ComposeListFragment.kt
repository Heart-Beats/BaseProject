package com.hl.baseproject.compose.pages

import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import com.hl.arch.base.ComposeBaseFragment
import com.hl.baseproject.compose.AppComposeTheme
import com.hl.baseproject.compose.utils.swipeToDismiss
import com.hl.baseproject.compose.widgets.SmartImage
import com.hl.baseproject.viewmodels.DataViewModel
import com.hl.utils.getRandomString
import com.hl.utils.showImage
import kotlinx.coroutines.launch

/**
 * @author  张磊  on  2023/04/18 at 11:55
 * Email: 913305160@qq.com
 */
class ComposeListFragment : ComposeBaseFragment() {

	/**
	 * 一：LazyList 相关学习总结：
	 *     1. 使用 Arrangement 来指定排列方式时，有几种默认方式，可见：https://developer.android.com/codelabs/jetpack-compose-layouts/img/c1e6c40e30136af2.gif
	 *           若想自定义设置距离，可使用： Arrangement.spacedBy()
	 *
	 *     2. 当有不同布局的子项时，最好给其指定 contentType， compose 会在相同类型的项之前重复使用组合，因为它们具有相似结构，所以渲染效率会更高
	 *
	 *     2. 在延迟布局中，可通过 item 方法添加 Header 与 Footer,  避免在一个 item 中放入多个元素，除了分隔符，
	 *          分隔符需要添加在组合项 Item 中，这样才不会更改 Item 对应的索引
	 *
	 *     3. 可通过 rememberLazy[List|Grid]State() 设置给相应延迟布局的 state  属性，获取相关的状态，包括不限于以下：
	 *          state.firstVisibleItemIndex、 state.layoutInfo.visibleItemsInfo（可见项信息）、state.layoutInfo.totalItemsCount（列表总数）
	 *
	 *     4. 使用 state 相关属性时，需要搭配 derivedStateOf 进行使用，确保只有当计算中使用的状态属性发生变化时才会进行重组
	 *
	 *     5. LazyGrid 相关延迟网格布局，若不确定总共有多少行或列，可通过 GridCells.Adaptive() 指定行或列大小， 进行自适应分配；
	 *            还可通过实现 GridCells 接口来动态计算每行的高度或每列的宽度
	 *
	 *     6. 滚动列表嵌套时，嵌套的滚动列表必须指定宽度或者高度
	 *
	 *     7. （自测实现无效果，不知原因）可通过 modifier.animateItemPlacement() 来为延迟布局设置子项的动画，使用时也必须为每个子项设置 key，key 要为 bundle 可传递的类型
	 *
	 *     8.  debug 包下，延迟布局运行较慢性能比较差，只有在 Release 版本下并且开启 R8 优化（混淆）， 才能真正衡量其性能
	 *
	 *
	 * 二：动画相关学习总结：
	 *      1. animate*AsState()：                相关 API 可为简单的值变化添加动画效果
	 *      2. AnimatedVisibility()：             默认情况下，以淡入和展开的方式显示元素，以淡出和缩小的方式隐藏元素，并支持通过 enter 和 exit 参数自定义显示和隐藏动画效果
	 *      3. AnimationSpec 类型 ：              动画的定制类型，可通过其定制动画的时长以及插值器等
	 *      4. Modifier.animateContentSize():    添加此修饰符后，当可组合项的内容发生变化导致大小更改时，会自动添加实现动画效果
	 *      5. updateTransition():               该方法可设置一个 Transition，它可以给多个不同类型的值设置动画效果，而且还能够在不同状态之间转换时定义不同的 transitionSpec
	 *      6. rememberInfiniteTransition():     该方法可以重复无限期的添加动画效果，无法停止，停止必须移除可组合项
	 *      7. Animatable():                     该方法可以创建 Animatable 实例，它可以获取动画运行状态，添加边界值、直接设置动画目标值以及停止动画运行等方法
	 *
	 *
	 */


	private val dataViewModel by activityViewModels<DataViewModel>()

	@Composable
	override fun Content(savedInstanceState: Bundle?) {
		val images by dataViewModel.imagesLiveData.observeAsState(listOf())
		val messageList = MutableList(50) {
			if (it == 0) {
				Message(avatar = images[0], author = "安卓", msgBody = "Jetpack Compose")
			} else {
				Message(
					avatar = images.random(),
					author = "安卓$it",
					msgBody = "Jetpack Compose ${getRandomString(it * 5)}"
				)
			}
		}

		InitPage(messageList)
	}

	@Composable
	private fun InitPage(messageList: MutableList<Message>) {
		AppComposeTheme(systemBarInset = false) {
			Surface(modifier = Modifier.fillMaxSize()) {
				MessageList(messageList)
			}
		}
	}


	@Preview(
		showBackground = true,
		name = "Dark Mode",
		uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
	)
	@Preview(showBackground = true)
	@Composable
	fun PreviewInitPage() {
		val messageList = MutableList(50) {
			if (it == 0) {
				Message(
					avatar = "https://i.pinimg.com/originals/08/16/e7/0816e71b97808b85d18e8ef7b77ac1a5.jpg",
					author = "安卓",
					msgBody = "Jetpack Compose"
				)
			} else {
				Message(
					avatar = "https://i.pinimg.com/originals/08/16/e7/0816e71b97808b85d18e8ef7b77ac1a5.jpg",
					author = "安卓$it",
					msgBody = "Jetpack Compose ${getRandomString(it * 5)}"
				)
			}
		}

		InitPage(messageList)
	}

	private data class Message(
		val avatar: String,
		val author: String,
		val msgBody: String
	)

	@OptIn(ExperimentalFoundationApi::class)
	@Composable
	private fun MessageList(messageList: MutableList<Message>) {
		val messageList by remember { derivedStateOf { messageList } }

		Box {

			val state = rememberLazyListState()

			//注意： 延迟布局与可滚动的布局嵌套时，必须指定其大小
			LazyColumn(contentPadding = PaddingValues(bottom = 8.dp), state = state) {
				this.stickyHeader(contentType = "text") {
					Text("我是列表吸顶头部", textAlign = TextAlign.Center, modifier = Modifier
						.fillMaxWidth()
						.clickable {
							messageList.clear()
							messageList.addAll(messageList.shuffled())

						})
				}

				this.item(contentType = "text") {
					Text("我是列表头部", modifier = Modifier.padding(horizontal = 8.dp))
				}

				this.items(messageList, contentType = { "MessageCard" }, key = {
					messageList.indexOf(it)
				}) { message ->
					MessageCard(
						message,
						modifier = Modifier
							.animateItemPlacement(tween(250))
							.swipeToDismiss {
								messageList.remove(message)
							},
					)
				}

				this.item(contentType = "text") {
					Text("我是列表尾部", modifier = Modifier.padding(horizontal = 8.dp))
				}
			}


			val showScrollToUp by remember {
				// 确保只有当计算中使用的状态属性发生变化时才会进行重组
				derivedStateOf {
					// state.layoutInfo.visibleItemsInfo 和 state.layoutInfo.totalItemsCount 可用来获取当前列表可见项信息以及列表总数
					state.firstVisibleItemIndex > 5
				}
			}

			if (showScrollToUp) {
				val coroutineScope = rememberCoroutineScope()

				FloatingActionButton(
					{
						coroutineScope.launch {
							state.animateScrollToItem(index = 0)
						}
					}, modifier = Modifier
						.align(Alignment.BottomEnd)
						.offset(x = (-50).dp, y = (-50).dp)
				) {
					Row(
						modifier = Modifier.padding(horizontal = 10.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						var isAddText by remember { mutableStateOf(false) }

						IconButton(onClick = { isAddText = !isAddText }) {
							Icon(
								imageVector = Icons.Default.ArrowUpward,
								contentDescription = null
							)
						}

						AnimatedVisibility(isAddText) {
							Text("点击向上滚动")
						}
					}
				}
			}
		}
	}


	@Composable
	private fun MessageCard(message: Message, modifier: Modifier = Modifier) {
		val context = LocalContext.current

		Card(
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
			elevation = CardDefaults.cardElevation(10.dp),
			modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
		) {
			Row(modifier = Modifier.padding(10.dp)) {
				SmartImage(
					imageModel = message.avatar,
					imageDescription = "测试头像",
					modifier = Modifier
						.size(50.dp)
						.clip(CircleShape)
						.border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
						.clickable {
							context.showImage(null, message.avatar)
						}
				)

				Spacer(modifier = Modifier.width(8.dp))

				var isExpanded by rememberSaveable { mutableStateOf(false) }

				Row(modifier = Modifier.fillMaxWidth()) {

					Column(modifier = Modifier.weight(1F)) {

						Text(
							text = message.author,
							style = MaterialTheme.typography.titleMedium.copy(
								color = Color(0xFF00A381)
							),
						)

						Spacer(modifier = Modifier.height(4.dp))

						val surfaceColor by animateColorAsState(
							if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
						)

						Surface(
							shape = MaterialTheme.shapes.medium,
							color = surfaceColor,
							shadowElevation = 1.dp,
							modifier = Modifier
								.animateContentSize(
									animationSpec = spring(
										dampingRatio = Spring.DampingRatioMediumBouncy,
										stiffness = Spring.StiffnessLow
									)
								)
								.padding(1.dp)
						) {
							Text(
								text = message.msgBody,
								style = MaterialTheme.typography.bodyMedium,
								maxLines = if (isExpanded) Int.MAX_VALUE else 1,
								modifier = Modifier.padding(4.dp)
							)
						}

					}


					IconButton(onClick = {
						isExpanded = !isExpanded
					}) {
						Icon(
							imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
							contentDescription = if (isExpanded) "展开" else "折叠"
						)
					}

				}
			}
		}
	}
}