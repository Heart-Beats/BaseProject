package com.hl.uikit.video

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.hl.uikit.R
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

/**
 * @Author  张磊  on  2020/09/25 at 18:58
 * Email: 913305160@qq.com
 */
class UIKitMyStandardGSYVideoPlayer : StandardGSYVideoPlayer, LifecycleEventObserver {

	var orientationUtils: OrientationUtils? = null
		private set

	private var play: ImageView? = null

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

	override fun getLayoutId(): Int {
		return R.layout.layout_my_video_standard
	}

	//增加自定义的 ui 需要在此方法中进行一些初始设置，否则大小屏切换会有问题，比如：事件丢失
	override fun init(context: Context?) {
		super.init(context)
		play = findViewById(R.id.play)
		play?.setOnClickListener {
			clickStartIcon()
		}
	}

	override fun setStateAndUi(state: Int) {
		super.setStateAndUi(state)
		val playResource = when (state) {
			//播放状态： 正常(准备资源完毕)，暂停，播放错误
			CURRENT_STATE_NORMAL, CURRENT_STATE_PAUSE, CURRENT_STATE_ERROR -> R.drawable.uikit_ic_play_24
			//播放状态：正在播放
			CURRENT_STATE_PLAYING -> R.drawable.uikit_ic_pause_24
			//播放状态: 缓冲准备播放，缓冲中
			CURRENT_STATE_PREPAREING, CURRENT_STATE_PLAYING_BUFFERING_START -> R.drawable.uikit_ic_pause_24
			//自动播放完成
			CURRENT_STATE_AUTO_COMPLETE -> R.drawable.uikit_ic_replay_24
			else -> R.drawable.uikit_ic_play_24
		}
		play?.setImageResource(playResource)
	}

	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
		when (event) {
			Lifecycle.Event.ON_CREATE -> {
				val fragment = source as? Fragment
				if (fragment != null) {
					//外部辅助的旋转，帮助全屏
					orientationUtils = OrientationUtils(fragment.requireActivity(), this)
				} else {
					val activity = source as? FragmentActivity
					if (activity != null) {
						orientationUtils = OrientationUtils(activity, this)
					}
				}
			}

			Lifecycle.Event.ON_RESUME -> {
				this.onVideoResume()
			}
			Lifecycle.Event.ON_PAUSE -> {
				this.onVideoPause()
			}
			Lifecycle.Event.ON_DESTROY -> {
				GSYVideoManager.releaseAllVideos()
				orientationUtils?.releaseListener()
			}
			else -> {
			}
		}
	}
}