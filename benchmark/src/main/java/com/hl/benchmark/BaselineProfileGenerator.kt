package com.hl.benchmark

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author  张磊  on  2023/06/06 at 15:19
 * Email: 913305160@qq.com
 */
@ExperimentalBaselineProfilesApi
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
	@get:Rule
	val baselineProfileRule = BaselineProfileRule()

	@Test
	fun startup() =
		baselineProfileRule.collectBaselineProfile(packageName = "com.hl.baseproject.demo") {
			pressHome()
			// This block defines the app's critical user journey. Here we are interested in
			// optimizing for app startup. But you can also navigate and scroll
			// through your most important UI.
			startActivityAndWait()
		}
}