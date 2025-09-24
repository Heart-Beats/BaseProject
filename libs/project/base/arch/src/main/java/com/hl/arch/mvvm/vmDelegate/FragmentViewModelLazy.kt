package androidx.fragment.app

import androidx.annotation.MainThread
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass

/**
 * @author  张磊  on  2025/09/24 at 14:47
 * Email: 913305160@qq.com
 */

/**
 * 为 Fragment 提供 ViewModel 委托, 重写覆盖 Android 官方自带实现，不可修改包名
 *
 * Returns a property delegate to access [ViewModel] by **default** scoped to this [Fragment]:
 * ```
 * class MyFragment : Fragment() {
 *     val viewmodel: MyViewModel by viewmodels()
 * }
 * ```
 *
 * Custom [ViewModelProvider.Factory] can be defined via [factoryProducer] parameter,
 * factory returned by it will be used to create [ViewModel]:
 * ```
 * class MyFragment : Fragment() {
 *     val viewmodel: MyViewModel by viewmodels { myFactory }
 * }
 * ```
 *
 * Default scope may be overridden with parameter [ownerProducer]:
 * ```
 * class MyFragment : Fragment() {
 *     val viewmodel: MyViewModel by viewmodels ({requireParentFragment()})
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach()], and access prior to that will result in IllegalArgumentException.
 */
@MainThread
public inline fun <reified VM : ViewModel> Fragment.viewModels(
	noinline ownerProducer: () -> ViewModelStoreOwner = { this },
	noinline extrasProducer: (() -> CreationExtras)? = null,
	noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> {
	val owner by lazy(LazyThreadSafetyMode.NONE) { ownerProducer() }
	return createViewModelLazy(
		VM::class,
		{ owner.viewModelStore },
		{
			extrasProducer?.invoke()
				?: (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelCreationExtras
				?: CreationExtras.Empty
		},
		factoryProducer ?: {
			(owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelProviderFactory
				?: defaultViewModelProviderFactory
		})
}


/**
 *
 * 为 Fragment 提供 ViewModel 委托, 重写覆盖 Android 官方自带实现，不可修改包名
 *
 * Returns a property delegate to access parent activity's [ViewModel],
 * if [factoryProducer] is specified then [ViewModelProvider.Factory]
 * returned by it will be used to create [ViewModel] first time. Otherwise, the activity's
 * [androidx.activity.ComponentActivity.getDefaultViewModelProviderFactory](default factory)
 * will be used.
 *
 * ```
 * class MyFragment : Fragment() {
 *     val viewmodel: MyViewModel by activityViewModels()
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach()], and access prior to that will result in IllegalArgumentException.
 */
@MainThread
public inline fun <reified VM : ViewModel> Fragment.activityViewModels(
	noinline extrasProducer: (() -> CreationExtras)? = null,
	noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> = createViewModelLazy(
	VM::class, { requireActivity().viewModelStore },
	{ extrasProducer?.invoke() ?: requireActivity().defaultViewModelCreationExtras },
	factoryProducer ?: { requireActivity().defaultViewModelProviderFactory }

)


/**
 * 为 Fragment 提供 ViewModel 委托, 重写覆盖 Android 官方自带实现，不可修改包名
 *
 * Helper method for creation of [ViewModelLazy], that resolves `null` passed as [factoryProducer]
 * to default factory.
 *
 * This method also takes an [CreationExtras] produces that provides default extras to the created
 * view model.
 */
@MainThread
public fun <VM : ViewModel> Fragment.createViewModelLazy(
	viewModelClass: KClass<VM>,
	storeProducer: () -> ViewModelStore,
	extrasProducer: () -> CreationExtras = { defaultViewModelCreationExtras },
	factoryProducer: (() -> Factory)? = null

): Lazy<VM> {
	val factoryPromise = factoryProducer ?: {
		defaultViewModelProviderFactory
	}
	return ViewModelLazy(this, viewModelClass, storeProducer, factoryPromise, extrasProducer)
}