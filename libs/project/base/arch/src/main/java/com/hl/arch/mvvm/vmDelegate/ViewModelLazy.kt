package androidx.lifecycle

import androidx.lifecycle.viewmodel.CreationExtras
import com.hl.arch.mvvm.vm.DispatcherVM
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * @author  张磊  on  2025/09/24 at 15:51
 * Email: 913305160@qq.com
 */
public class ViewModelLazy<VM : ViewModel> @JvmOverloads constructor(
	private val lifecycleOwner: LifecycleOwner,
	private val viewModelClass: KClass<VM>,
	private val storeProducer: () -> ViewModelStore,
	private val factoryProducer: () -> ViewModelProvider.Factory,
	private val extrasProducer: () -> CreationExtras = { CreationExtras.Empty }
) : Lazy<VM> {
	private var cached: VM? = null

	override val value: VM
		get() {
			val viewModel = cached
			return if (viewModel == null) {
				val factory = factoryProducer()
				val store = storeProducer()
				ViewModelProvider(
					store,
					factory,
					extrasProducer()
				)[viewModelClass.java].also {
					cached = it

					lifecycleOwner.lifecycleScope.launch {
						// ViewModel 已被添加到 viewModelStore 后发送通知关联的 LifecycleOwner
						DispatcherVM.viewModelOnCreateSharedFlow.emit(Pair(lifecycleOwner, it))
					}
				}
			} else {
				viewModel
			}
		}

	override fun isInitialized(): Boolean = cached != null
}