package com.hl.baseproject

import android.net.Uri
import android.os.Bundle
import androidx.core.view.updateLayoutParams
import com.hl.baseproject.databinding.ActivitySplashBinding
import com.hl.ui.base.ViewBindingBaseActivity
import com.hl.ui.utils.startAct
import com.hl.uikit.utils.getScreenHeight
import com.hl.uikit.utils.getScreenWidth

class SplashActivity : ViewBindingBaseActivity<ActivitySplashBinding>() {

	override fun ActivitySplashBinding.onViewCreated(savedInstanceState: Bundle?) {

		var splashVideoView = this.videoView
		splashVideoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.playground_sea))
		// splashVideoView.setMediaController(MediaController(this@SplashActivity))
		splashVideoView.start()

		splashVideoView.setOnPreparedListener {
			splashVideoView.updateLayoutParams {
				this.width = getScreenWidth()
				this.height = getScreenHeight()
			}
		}

		splashVideoView.setOnCompletionListener {
			startAct<MainActivity>()
		}
	}
}