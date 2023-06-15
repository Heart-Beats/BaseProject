# Navigation

Android  Jetpack Navigation 改造优化库，只要改造如下：

* 使用自定义的 `MyNavHostFragment` ， 导航时默认调用 `MyFragmentNavigator` 里的 navigate 方法， 该方法使用 `add()`、`hide()` 代替原始的 `replace()` 方法
* `MyNavHostFragment` 里的 `setCommonNavAnimations` 支持给导航设置全局默认动画，并且也支持 xml 中对导航单独设置过渡动画
