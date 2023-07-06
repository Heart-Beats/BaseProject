package com.hl.baseproject.compose.pages

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hl.ui.base.ComposeBaseFragment
import com.hl.baseproject.compose.AppComposeTheme

/**
 * @author  张磊  on  2023/05/23 at 11:39
 * Email: 913305160@qq.com
 *
 * Compose 是一个声明式界面框架，与命令式不同的主要区别在于： 界面组件在初始化时就已确定，在重组时会根据不同的状态去更新组件，而不是像命令式那样，
 *          达到什么条件，手动去更新组件的属性，这点在添加移除组件时，会有非常明显的区别。
 *
 * compose 中的状态：
 *     · 状态：   可以随时间变化的任何值，可以理解为 UI 元素对应的可变化数据
 *     · 事件：   任何会导致状态修改的操作都称为 "事件"
 *
 * compose 中的重组：
 *     · 组合：   Jetpack Compose 在执行可组合项时构建的界面描述，组合的输出是描述界面的树结构。
 *     · 初始组合：通过首次运行可组合项创建组合
 *     · 重组：    在数据发生变化时重新运行可组合项以更新组合
 *   因此 Compose 需要知道要跟踪的状态，以便在收到更新时安排重组，所以它采用了特殊的状态跟踪系统，仅为跟踪特定状态的任何可重组项安排重组。
 *
 * compose 中的状态声明：
 *     · State 和 MutableState 类型： 它们可以让 Compose 能够跟踪观察到状态，  State 类型仅可读取不可改变， MutableState 既可读取又可改变。
 *                                       MutableState 值改变时可触发重组，但 MutableState 对象若声明在重组项内，会触发对象的重新初始化，因此需要记住改变后重组之前的值， remember() 就是为此设计的。
 *     · remember()：                它会在初始组合期间将由 remember 计算的值存储在组合中，并在重组期间一直保持存储的值，即重组时不会重新初始化。
 *                                      注意： 若重组期间退出组合且 remember() 未得到调用，则它存储的值会被忘记重置，下次执行时仍会重新初始化
 *     · rememberSaveable():         remember() 方法可帮助在重组后保持状态，但不会在配置项更改后保持状态，如：屏幕旋转、更改语言、切换深色模式等， 这时若 Activity
 *                                      没对配置项更改进行自定义声明，默认会导致 Activity 重建，因此相关的状态值会进行重新初始化。若想在这种情况下，让 Compose
 *                                      恢复状态，就需要使用 rememberSaveable() 这个方法，它会自动保存可保存在 Bundle 中的任何值，对于 Bundle
 *                                      不可保存的，可以将其传入自定义 Saver 对象。 所以 rememberSaveable() 更多使用在重新创建 activity 或进程后， 恢复界面状态；
 *                                      并且在 LazyList 的列表项中，它也用来保存对应项的状态，确保列表项离开列表后能恢复相应的状态
 *
 * compose 中的状态提升：  状态提升是一种将状态移至可组合项的调用方以使可组合项无状态的模式
 *      · 有状态可组合项： 使用 remember 存储对象的可组合项包含内部状态，这会使该可组合项有状态，具有内部状态的可组合项往往不易重复使用，也更难测试。
 *      · 无状态可组合项： 不保存任何状态的可组合项称为无状态可组合项。
 *
 *      · 常规状态提升模式：将状态变量替换为两个参数：
 *                          · value: T：要显示的当前值
 *                          · onValueChange: (T) -> Unit：请求更改值的事件，其中 T 是建议的新值
 *
 *                        状态提升的优点：
 *                          1. 单一可信来源：通过移动状态，而不是复制状态，可确保只有一个可信来源，这有助于避免 bug。
 *                          2. 可共享：     可与多个可组合项共享提升的状态
 *                          3. 可拦截：     无状态可组合项的调用方可以在更改状态之前决定忽略或修改事件。
 *                          4. 分离：      无状态可组合项的状态可以存储在任何位置。例如，存储在 ViewModel 中。
 *
 *                        状态提升规则：
 *                          1. 状态应至少提升到使用该状态（读取）的所有可组合项的最低共同父项，即组合项共用同一状态。
 *                          2. 状态应至少提升到它可以发生变化（写入）的最高级别。
 *                          3. 如果两种状态发生变化以响应相同的事件，它们应提升到同一级别。
 *
 * compose 中的状态容器：通过创建负责可组合项的内部状态的状态容器类，可以将所有状态变化处理集中在一个位置。
 *      ·  记住状态容器：对于可以存储在 Bundle 内的对象，直接使用 rememberSaveable 即可实现存储的值在 activity 和进程重新创建后留存。
 *                      但对于状态容器并非如此。需要告知 rememberSaveable 如何使用 Saver 保存和恢复状态容器的实例，这就需要自定义保存器。
 *      ·  自定义保存器： Saver 描述了如何将对象转换为 Saveable（可保存）的内容。Saver 的实现需要重写两个函数：
 *                          · save - 将原始值转换为可保存的值
 *                          · restore - 将恢复的值转换为原始类的实例
 *                      可使用一些现有的 Compose API，如 listSaver 或 mapSaver（用于存储要保存在 List 或 Map 中的值）来实现，以减少编码量。
 *
 *
 * compose 中的可观察可变列表：
 *      ·  可变列表：       MutableList<T>  或  ArrayList<T>， 但是它们不会向 Compose 通知列表中的项已发生更改并安排界面重组
 *      ·  可观察可变列表： SnapshotStateList<T>， 可由扩展函数 Collection<T>.toMutableStateList() 直接创建,  还可以使用顶级函数
 *                            mutableStateListOf(vararg elements: T)  进行动态创建
 *
 * compose 中的状态类型转换：  Compose 支持其他可观察类型转换为 State<T>，以便 Jetpack Compose 可以在状态发生变化后自动重组。
 *      其他可观察类型  ----> State<T>
 *          · Flow:      collectAsStateWithLifecycle(), Android 中以生命周期感知型方式从 Flow 收集值。需添加库：androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version
 *          · Flow:      collectAsState(), 与 collectAsStateWithLifecycle() 类似，但其不会感知生命周期，因此适用与平台通用代码
 *          · LiveData： observeAsState()， 观察此 LiveData，并通过 State 表示其值。需添加库： androidx.compose.runtime:runtime-livedata:1.3.2
 *          · RxJava2:   subscribeAsState(), 可将 RxJava2 的响应式流转换成 Compose State, 需添加库：androidx.compose.runtime:runtime-rxjava2:1.3.2
 *          · RxJava3:   subscribeAsState(), 可将 RxJava3 的响应式流转换成 Compose State, 需添加库：androidx.compose.runtime:runtime-rxjava3:1.3.2
 *
 *      State<T>  -----> Flow
 *          · snapshotFlow()：将状态转换为 Flow，当在 snapshotFlow 内读取的状态发生变化时，Flow 会向收集器发出新值
 *
 *      State<T>  -----> State<T2>
 *          · derivedStateOf()：当某个 State 需要衍生自另一个 State 时，请使用 derivedStateOf。每当内部状态发生变化时，会执行 derivedStateOf  计算块，
 *                              但是只有当计算结果与上一次不同时，可组合函数才会重组，非常适合状态频繁变化时转化为布尔状态。
 */
class ComposeStateFragment : com.hl.ui.base.ComposeBaseFragment() {

	@Composable
	override fun Content(savedInstanceState: Bundle?) {
		AppComposeTheme(systemBarInset = false) {
			Surface {
				WellnessScreen()
			}
		}
	}

	@Preview
	@Composable
	fun PreviewWellnessScreen() {
		AppComposeTheme(systemBarInset = false) {
			Surface {
				WellnessScreen()
			}
		}
	}

	@Composable
	fun WellnessScreen(modifier: Modifier = Modifier, wellnessViewModel: WellnessViewModel = viewModel()) {
		Column(modifier) {
			StatefulWaterCounter(modifier)
			WellnessTasksList(wellnessViewModel.task, onCheckedChangeTask = { task, checked ->
				wellnessViewModel.changeTaskChecked(task, checked)
			}, onCloseTask = {
				wellnessViewModel.removeTask(it)
			}, modifier)
		}
	}

	@Composable
	fun StatefulWaterCounter(modifier: Modifier = Modifier) {
		var count by rememberSaveable { mutableStateOf(0) }
		StatelessWaterCounter(count, onIncrement = { count++ }, onClear = { count = 0 }, modifier)
	}

	@Composable
	fun StatelessWaterCounter(count: Int, onIncrement: () -> Unit, onClear: () -> Unit, modifier: Modifier = Modifier) {
		Column(modifier = modifier.padding(16.dp)) {
			if (count > 0) {
				// 有可能重组时退出组合且 remember() 未得到调用，则其保存的值会被重置
				var showTask by rememberSaveable { mutableStateOf(true) }
				if (showTask) {
					WellnessTaskItem(taskName = "您今天有花费 15 分钟喝水?", isChecked = false, onCheckedChange = {}) {
						showTask = false
					}
				}

				Text(text = "您已经喝了${count}杯水.", modifier = Modifier.padding(16.dp))
			}

			Row(Modifier.padding(top = 8.dp)) {
				Button(onClick = onIncrement) {
					Text(text = "点击添加")
				}

				Button(onClick = onClear, Modifier.padding(start = 8.dp)) {
					Text("清除喝水杯数计数")
				}
			}
		}
	}

	@Composable
	fun WellnessTasksList(
		items: List<WellnessTask>,
		onCheckedChangeTask: (WellnessTask, Boolean) -> Unit,
		onCloseTask: (WellnessTask) -> Unit,
		modifier: Modifier = Modifier
	) {
		LazyColumn {
			this.items(items, key = { it.id }) { task ->
				WellnessTaskItem(
					task.label,
					task.checked,
					onCheckedChange = { checked ->
						onCheckedChangeTask(task, checked)
					},
					onClose = { onCloseTask(task) },
					modifier
				)
			}
		}
	}

	@Composable
	fun WellnessTaskItem(
		taskName: String,
		isChecked: Boolean,
		onCheckedChange: (Boolean) -> Unit,
		onClose: () -> Unit,
		modifier: Modifier = Modifier
	) {
		// rememberSaveable 与 LazyList 配合工作的方式，可让状态在离开组合后也能继续保留
		// var isChecked by rememberSaveable { mutableStateOf(false) }
		WellnessTaskItem(
			taskName,
			isChecked = isChecked,
			onCheckedChange = onCheckedChange,
			modifier,
			onClose
		)
	}

	@Composable
	fun WellnessTaskItem(
		taskName: String,
		isChecked: Boolean,
		onCheckedChange: (Boolean) -> Unit,
		modifier: Modifier = Modifier,
		onClose: () -> Unit
	) {
		Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
			Text(
				modifier = Modifier
					.weight(1f)
					.padding(start = 16.dp),
				text = taskName
			)

			Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
			IconButton(onClick = onClose, modifier = Modifier.padding(horizontal = 5.dp)) {
				Icon(Icons.Filled.Close, contentDescription = "关闭")
			}
		}
	}
}

data class WellnessTask(val id: Int, val label: String, var initialChecked: Boolean = false) {

	/**
	 * 指示任务是否选中， 必须为 State<T> 类型， compose 才会跟踪此值并进行重组
	 */
	var checked by mutableStateOf(initialChecked)
}

class WellnessViewModel : ViewModel() {

	private val _tasks = getWellnessTasks().toMutableStateList() //创建为可观察列表 Compose 才能跟踪元素添加和删除

	val task: List<WellnessTask> = _tasks

	private fun getWellnessTasks() = List(30) { i -> WellnessTask(i, "任务 $i") }

	fun removeTask(task: WellnessTask) {
		_tasks.remove(task)
	}

	fun changeTaskChecked(item: WellnessTask, checked: Boolean) =
		_tasks.find { it.id == item.id }?.let { task ->
			task.checked = checked
		}

}