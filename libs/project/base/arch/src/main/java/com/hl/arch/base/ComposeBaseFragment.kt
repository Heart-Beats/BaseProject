package com.hl.arch.base

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.hl.arch.databinding.FragmentComposeBaseBinding

/**
 * @author  张磊  on  2023/04/28 at 17:24
 * Email: 913305160@qq.com
 */
abstract class ComposeBaseFragment : ViewBindingBaseFragment<FragmentComposeBaseBinding>() {

	override fun FragmentComposeBaseBinding.onViewCreated(savedInstanceState: Bundle?) {
		this.composeView.setContent {
			Content(savedInstanceState)
		}
	}

	@Composable
	abstract fun Content(savedInstanceState: Bundle?)
}