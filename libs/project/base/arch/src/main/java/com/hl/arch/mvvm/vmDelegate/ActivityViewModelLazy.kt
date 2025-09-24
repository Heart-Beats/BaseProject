package androidx.activity

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass

/**
 * @author  张磊  on  2025/09/24 at 15:00
 * Email: 913305160@qq.com
 */

/**
 * 为 ComponentActivity 提供 ViewModel 委托, 重写覆盖 Android 官方自带实现，不可修改包名
 *
 * Returns a [Lazy] delegate to access the ComponentActivity's ViewModel, if [factoryProducer]
 * is specified then [ViewModelProvider.Factory] returned by it will be used
 * to create [ViewModel] first time.
 *
 * ```
 * class MyComponentActivity : ComponentActivity() {
 *     val viewmodel: MyViewModel by viewModels()
 * }
 * ```
 *
 * This property can be accessed only after the Activity is attached to the Application,
 * and access prior to that will result in IllegalArgumentException.
 */
@MainThread
public inline fun <reified VM : ViewModel> ComponentActivity.viewModels(
	noinline extrasProducer: (() -> CreationExtras)? = null,
	noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> = createViewModelLazy(
	VM::class,
	{ viewModelStore },
	{ extrasProducer?.invoke() ?: this.defaultViewModelCreationExtras },
	factoryProducer
)

@MainThread
public fun <VM : ViewModel> ComponentActivity.createViewModelLazy(
	viewModelClass: KClass<VM>,
	storeProducer: () -> ViewModelStore,
	extrasProducer: () -> CreationExtras = { defaultViewModelCreationExtras },
	factoryProducer: (() -> Factory)? = null

): Lazy<VM> {
	val factoryPromise = factoryProducer ?: {
		defaultViewModelProviderFactory
	}
	return ViewModelLazy(this,viewModelClass, storeProducer, factoryPromise, extrasProducer)
}