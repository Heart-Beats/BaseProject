package com.hl.arch.mvvm.vm

import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import com.hl.arch.mvvm.fragment.MvvmBaseFragment
import kotlin.reflect.KClass

@MainThread
inline fun <reified VM : ViewModel> MvvmBaseFragment.activityViewModels(
	noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = createViewModelLazy(VM::class, { requireActivity().viewModelStore },
	factoryProducer ?: { requireActivity().defaultViewModelProviderFactory })

@MainThread
inline fun <reified VM : ViewModel> MvvmBaseFragment.navGraphViewModels(
	@IdRes navGraphId: Int,
	noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
	val backStackEntry by lazy {
		findNavController().getBackStackEntry(navGraphId)
	}
	val storeProducer: () -> ViewModelStore = {
		backStackEntry.viewModelStore
	}
	return createViewModelLazy(VM::class, storeProducer) {
		factoryProducer?.invoke() ?: backStackEntry.defaultViewModelProviderFactory
	}
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModels(
	noinline ownerProducer: () -> ViewModelStoreOwner = { this },
	noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer)


fun <VM : ViewModel> Fragment.createViewModelLazy(
	viewModelClass: KClass<VM>,
	storeProducer: () -> ViewModelStore,
	factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
	val factoryPromise = factoryProducer ?: {
		defaultViewModelProviderFactory
	}
	return ViewModelLazy(viewModelClass, storeProducer, factoryPromise) { vm ->
		view?.let {
			val fragment = this
			if (fragment is MvvmBaseFragment) {
				fragment.onViewModelCreated(vm, fragment.viewLifecycleOwner)
			}
		}
	}
}

class ViewModelLazy<VM : ViewModel>(
	private val viewModelClass: KClass<VM>,
	private val storeProducer: () -> ViewModelStore,
	private val factoryProducer: () -> ViewModelProvider.Factory,
	private val onGetValue: (vm: VM) -> Unit = {}
) : Lazy<VM> {
	private var cached: VM? = null

	override val value: VM
		get() {
			val viewModel = cached
			return if (viewModel == null) {
				val factory = factoryProducer()
				val store = storeProducer()
				ViewModelProvider(store, factory).get(viewModelClass.java).also {
					cached = it
				}
			} else {
				viewModel
			}.apply(onGetValue)
		}

	override fun isInitialized() = cached != null
}