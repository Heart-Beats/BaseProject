package com.buildsrc

internal object Versions {
    const val JUNIT = "4.+"
    const val JUNIT_EXT = "1.1.3"
    const val ESPRESSO_CORE = "3.4.0"
    const val KOTLIN = "1.6.10"
    const val KOTLIN_COROUTINES = "1.4.2"
    const val CORE_KTX = "1.6.0"

    const val APPCOMPAT = "1.4.0"
    const val CONSTRAINT_LAYOUT = "1.1.3"
    const val ACTIVITY = "1.4.0"
    const val FRAGMENT = "1.4.0"
    const val SECURITY_CRYPTO = "1.0.0"
    const val RECYCLERVIEW = "1.2.1"
    const val DATA_BINDING = "7.0.4"
    const val CAMERA_X = "1.1.0-beta01"

    const val LIFECYCLE = "2.4.0"
    const val NAV_VERSION = "2.4.0-rc01"

    const val MATERIAL = "1.4.0"

    const val XLOG = "1.10.1"

    const val NERTC = "4.5.2"
    const val NIM = "8.5.1"
    const val RETROFIT = "2.9.0"
    const val OKHTTP = "4.2.2"
    const val EASY_HTTP = "11.0"
    const val IMMERSION_BAR = "3.2.2"
    const val EVENT_BUS = "3.3.1"
    const val RX_JAVA_3 = "3.1.3"
    const val RX_ANDROID = "3.0.0"
    const val FANCY_TOAST = "2.0.1"
    const val GLIDE = "4.12.0"
    const val PERMISSION_X = "1.6.2"
    const val GSON = "2.8.9"
    const val FAST_JSON = "1.1.72.android"
    const val BUTTER_KNIFE = "10.2.1"
    const val BRVAH = "3.0.7"
    const val BASE_POPUP = "3.1.8"
    const val X_POPUP = "2.7.5"
    const val ZXING = "2.2.8"
    const val ZXING_LITE = "2.1.1"
    const val UTIL_CODE_X = "1.31.0"
    const val SHORTCUT_BADGER = "1.1.22"
    const val PHOTO_VIEW = "latest.release"
    const val BANNER = "2.2.2"
    const val JS_BRIDGE = "1.0.4"
    const val SMART_REFRESH_LAYOUT = "2.0.5"
    const val PICTURE_SELECTOR = "v3.0.6"
    const val ANDROID_FILE_PICKER = "0.75"
    const val CALENDAR_VIEW = "3.7.1"
    const val FLOW_LAYOUT = "1.1.2"
    const val CAMERA = "1.1.9"
    const val VERIFICATION_CODE_INPUT_VIEW = "1.0.2"
    const val GSY_VIDEO_PLAYER = "v8.2.0-release-jitpack"
    const val WEI_XIN_OPEN_SDK = "+"
    const val TBS_SDK = "44181"
    const val UPDATE_APP_UTILSX = "2.3.0"
    const val LOTTIE = "5.0.3"
    const val WAVE_SIDE_BAR = "1.3"
    const val SIDE_BAR = "1.0.0"
    const val ANDROID_AUTO_SIZE = "v1.2.1"
}

class Deps {

    /**
     * ?????????????????????
     */
    object Test {
        const val junit = "junit:junit:${Versions.JUNIT}"
        const val junit_ext = "androidx.test.ext:junit:${Versions.JUNIT_EXT}"
        const val espresso_core = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
    }

    object AndroidX {
        const val core_ktx = "androidx.core:core-ktx:${Versions.CORE_KTX}"
        const val appcompat = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
        const val constraint_layout = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
        const val activity = "androidx.activity:activity:${Versions.ACTIVITY}"
        const val fragment = "androidx.fragment:fragment:${Versions.FRAGMENT}"
        const val activity_ktx = "androidx.activity:activity-ktx:${Versions.ACTIVITY}"
        const val fragment_ktx = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT}"
        const val security_crypto = "androidx.security:security-crypto:${Versions.SECURITY_CRYPTO}"
        const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.RECYCLERVIEW}"
        const val view_binding = "androidx.databinding:viewbinding:${Versions.DATA_BINDING}"
        const val databinding_runtime = "androidx.databinding:databinding-runtime:${Versions.DATA_BINDING}"

        /**
         *  camera_core ????????? camera-camera2??????????????????
         */
        const val camera_core = "androidx.camera:camera-core:${Versions.CAMERA_X}"
        const val camera_camera2 = "androidx.camera:camera-camera2:${Versions.CAMERA_X}"
        const val camera_lifecycle = "androidx.camera:camera-lifecycle:${Versions.CAMERA_X}"
        const val camera_view = "androidx.camera:camera-view:${Versions.CAMERA_X}"
        const val camera_extensions = "androidx.camera:camera-extensions:${Versions.CAMERA_X}"
    }

    object Lifecycle {
        const val lifecycle_extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
        const val lifecycle_livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}"
        const val lifecycle_viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE}"
        const val lifecycle_runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
    }

    object Material {
        const val material = "com.google.android.material:material:${Versions.MATERIAL}"
    }

    object Kotlin {
        const val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
        const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"

        /**
         * ???????????????
         */
        const val kotlinx_coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KOTLIN_COROUTINES}"

        /**
         * ?????? Android ?????????
         */
        const val kotlinx_coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.KOTLIN_COROUTINES}"
    }

    object JetpackNavigation {
        // Java language implementation
        const val navigation_fragment = "androidx.navigation:navigation-fragment::${Versions.NAV_VERSION}"
        const val navigation_ui = "androidx.navigation:navigation-ui:${Versions.NAV_VERSION}"

        // Kotlin
        const val navigation_fragment_ktx = "androidx.navigation:navigation-fragment-ktx:${Versions.NAV_VERSION}"
        const val navigation_ui_ktx = "androidx.navigation:navigation-ui-ktx:${Versions.NAV_VERSION}"

        // Feature module Support
        const val navigation_dynamic_features_fragment =
            "androidx.navigation:navigation-dynamic-features-fragment:${Versions.NAV_VERSION}"

        // Jetpack Compose Integration
        const val navigation_compose = "androidx.navigation:navigation-compose:${Versions.NAV_VERSION}"
    }

    object XLog {
        const val xlog = "com.elvishew:xlog:${Versions.XLOG}"
    }

    object Netease {
        /**
         * ???????????????
         */
        const val nertc_sdk = "com.netease.yunxin:nertc:${Versions.NERTC}"

        /**
         * ???????????? (??????)
         */
        const val base_sdk = "com.netease.nimlib:basesdk:${Versions.NIM}"

        const val av_sign_alling_sdk = "com.netease.nimlib:avsignalling:${Versions.NIM}"

        /**
         * ????????????????????????????????????????????????
         */
        const val im_push_sdk = "com.netease.nimlib:push:${Versions.NIM}"

        /**
         * ??????????????????
         */
        const val lucene_sdk = "com.netease.nimlib:lucene:${Versions.NIM}"

        /**
         *  ???????????????
         */
        const val chatroom_sdk = "com.netease.nimlib:chatroom:${Versions.NIM}"
        const val rts_sdk = "com.netease.nimlib:rts:${Versions.NIM}"

        /**
         * ???????????????
         */
        const val super_team_Sdk = "com.netease.nimlib:superteam:${Versions.NIM}"
        const val ysf_sdk = "com.netease.nimlib:ysf:${Versions.NIM}"
        const val ysf_kit_sdk = "com.netease.nimlib:ysfkit:${Versions.NIM}"
    }

    object Retrofit {
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
        const val converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.RETROFIT}"
        const val adapter_rxjava3 = "com.squareup.retrofit2:adapter-rxjava3:${Versions.RETROFIT}"
    }

    object Okhttp {
        const val logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.OKHTTP}"
        const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}"

        /**
         * ?????? Okhttp ???????????????????????????https://github.com/getActivity/EasyHttp
         *
         * ????????????????????????????????????????????????????????????????????????????????????
         */
        const val easy_http = "com.github.getActivity:EasyHttp:${Versions.EASY_HTTP}"
    }

    object Status_Bar {

        /**
         *  ?????????????????????????????????
         */
        const val immersionbar = "com.geyifeng.immersionbar:immersionbar:${Versions.IMMERSION_BAR}"


        /**
         * kotlin??????????????????
         */
        const val immersionbar_ktx = "com.geyifeng.immersionbar:immersionbar-ktx:${Versions.IMMERSION_BAR}"
    }

    object EventBus {
        const val event_bus = "org.greenrobot:eventbus:${Versions.EVENT_BUS}"
    }

    object Rxjava3 {
        const val rxjava = "io.reactivex.rxjava3:rxjava:${Versions.RX_JAVA_3}"
        const val rxandroid = "io.reactivex.rxjava3:rxandroid:${Versions.RX_ANDROID}"
    }

    object FancyToast {
        const val fancy_toast = "io.github.shashank02051997:FancyToast:${Versions.FANCY_TOAST}"
    }

    object Glide {
        const val glide = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
        const val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.GLIDE}"
    }

    /**
     * ????????????
     */
    object PermissionX {
        const val permission_x = "com.guolindev.permissionx:permissionx:${Versions.PERMISSION_X}"
    }


    object Json {
        const val gson = "com.google.code.gson:gson:${Versions.GSON}"

        const val fast_json = "com.alibaba:fastjson:${Versions.FAST_JSON}"
    }

    object ButterKnife {
        const val butterknife_compiler_gradle_plugin =
            "com.jakewharton:butterknife-gradle-plugin:${Versions.BUTTER_KNIFE}"
        const val butterknife = "com.jakewharton:butterknife:${Versions.BUTTER_KNIFE}"
        const val butterknife_compiler = "com.jakewharton:butterknife-compiler:${Versions.BUTTER_KNIFE}"
    }

    object BRVAH {
        const val base_recycler_view_adapter_helper =
            "com.github.CymChad:BaseRecyclerViewAdapterHelper:${Versions.BRVAH}"
    }

    object Popup {
        const val base_popup = "io.github.razerdp:BasePopup:${Versions.BASE_POPUP}"
        const val x_popup = "com.github.li-xiaojun:XPopup:${Versions.X_POPUP}"
    }

    object ZXing {
        /**
         * https://github.com/yuzhiqiang1993/zxing
         *
         * 2.2.9???????????????Zxing3.4.1????????????Android7.0?????????API level 24 ???????????????
         */
        const val zxing_yuzhiqiang = "com.github.yuzhiqiang1993:zxing:${Versions.ZXING}"

        /**
         * ??????????????????????????????????????????
         *
         * https://github.com/jenly1314/ZXingLite
         */
        const val zxing_lite = "com.github.jenly1314:zxing-lite:${Versions.ZXING_LITE}"
    }

    object UtilCodeX {
        const val utilcodex = "com.blankj:utilcodex:${Versions.UTIL_CODE_X}"
    }

    /**
     * app ???????????????
     */
    object ShortcutBadger {
        const val shortcut_badger = "me.leolin:ShortcutBadger:${Versions.SHORTCUT_BADGER}"
    }

    object PhotoView {
        const val photo_view = "com.github.chrisbanes:PhotoView:${Versions.PHOTO_VIEW}"
    }


    object Umeng {
        // ?????????????????? ??????
        const val common = "com.umeng.umsdk:common:9.4.4"
        const val asms = "com.umeng.umsdk:asms:1.5.0"

        // ??????????????????????????????????????????SDK??????crash??????????????????????????????
        const val apm = "com.umeng.umsdk:apm:1.5.2"

        //??????Push??????
        const val push = "com.umeng.umsdk:push:6.4.8"

        ////??????U-App???ABTest???????????????
        const val abtest = "com.umeng.umsdk:abtest:1.0.0"

        /*************************************** ????????????   ***************************************************/
        //??????U-Link??????????????????????????????????????????????????????????????????????????????
        const val link = "com.umeng.umsdk:link:1.2.0"

        const val share_core = "com.umeng.umsdk:share-core:7.1.7" //????????????????????????
        const val share_board = "com.umeng.umsdk:share-board:7.1.7" //???????????????????????????

        const val share_wx = "com.umeng.umsdk:share-wx:7.1.7" //???????????????
        const val wechat_sdk_android_without_mta =
            "com.tencent.mm.opensdk:wechat-sdk-android-without-mta:${Versions.WEI_XIN_OPEN_SDK}" //??????????????????????????????

        const val share_qq = "com.umeng.umsdk:share-qq:7.1.7" //QQ?????????

        const val okhttp = "com.squareup.okhttp3:okhttp:3.14.9" //QQ??????sdk 3.53???????????????????????????okhttp3.x?????????

        const val share_sina = "com.umeng.umsdk:share-sina:7.1.7" //?????????????????????
        const val sinaweibosdk = "io.github.sinaweibosdk:core:11.11.1@aar" //??????????????????SDK??????????????????

        const val share_dingding = "com.umeng.umsdk:share-dingding:7.1.7" //???????????????
        const val dd_share_sdk = "com.alibaba.android:ddsharesdk:1.2.0@jar" //??????????????????????????????

        const val share_alipay = "com.umeng.umsdk:share-alipay:7.1.7" //??????????????????

    }

    object Banner {
        const val banner = "io.github.youth5201314:banner:${Versions.BANNER}"
    }

    object JsBridge {
        const val jsbridge = "com.github.lzyzsd:jsbridge:${Versions.JS_BRIDGE}"
    }


    object SmartRefreshLayout {
        //??????????????????
        const val kernel = "io.github.scwang90:refresh-layout-kernel:${Versions.SMART_REFRESH_LAYOUT}"

        const val header_classics =
            "io.github.scwang90:refresh-header-classics:${Versions.SMART_REFRESH_LAYOUT}"    //???????????????
        const val header_radar =
            "io.github.scwang90:refresh-header-radar:${Versions.SMART_REFRESH_LAYOUT}"       //???????????????
        const val header_falsify =
            "io.github.scwang90:refresh-header-falsify:${Versions.SMART_REFRESH_LAYOUT}"     //???????????????
        const val header_material =
            "io.github.scwang90:refresh-header-material:${Versions.SMART_REFRESH_LAYOUT}"    //???????????????
        const val header_two_level =
            "io.github.scwang90:refresh-header-two-level:${Versions.SMART_REFRESH_LAYOUT}"   //???????????????
        const val footer_ball =
            "io.github.scwang90:refresh-footer-ball:${Versions.SMART_REFRESH_LAYOUT}"        //???????????????
        const val footer_classics =
            "io.github.scwang90:refresh-footer-classics:${Versions.SMART_REFRESH_LAYOUT}"    //????????????
    }

    object PickSelector {
        /**
         * ???????????????????????????
         */
        const val picture_selector = "io.github.lucksiege:pictureselector:${Versions.PICTURE_SELECTOR}"

        /**
         * ???????????????????????????, ?????? Luban???
         */
        const val picture_selector_compress = "io.github.lucksiege:compress:${Versions.PICTURE_SELECTOR}"

        /**
         *  ???????????????????????????????????? uCrop???
         */
        const val picture_selector_ucrop = "io.github.lucksiege:ucrop:${Versions.PICTURE_SELECTOR}"

        /**
         *  ????????? camerax ??????????????????
         */
        const val picture_selector_camerax = "io.github.lucksiege:camerax:${Versions.PICTURE_SELECTOR}"

        /**
         * ???????????????
         */
        const val file_picker = "me.rosuh:AndroidFilePicker:${Versions.ANDROID_FILE_PICKER}"
    }

    object CalendarView {
        // ???????????????????????????????????? github ?????????https://github.com/huanghaibin-dev/CalendarView
        const val calendar_view = "com.haibin:calendarview:${Versions.CALENDAR_VIEW}"
    }

    object FlowLayout {
        const val flow_layout = "com.hyman:flowlayout-lib:${Versions.FLOW_LAYOUT}"
    }

    object Camera {
        //?????????????????????Android????????????
        const val camera = "cjt.library.wheel:camera:${Versions.CAMERA}"
    }

    object VerificationCodeInputView {
        //?????????????????????????????????
        const val verification_code_input_view =
            "com.github.Wynsbin:VerificationCodeInputView:${Versions.VERIFICATION_CODE_INPUT_VIEW}"
    }

    object GSYVideoPlayer {
        //?????????????????????????????????
        const val gsy_video_player = "com.github.CarGuo.GSYVideoPlayer:GSYVideoPlayer:${Versions.GSY_VIDEO_PLAYER}"
    }

    object Tencent {
        //?????????????????????
        const val wechat_sdk_android_without_mta =
            "com.tencent.mm.opensdk:wechat-sdk-android-without-mta:${Versions.WEI_XIN_OPEN_SDK}"

        const val tbs_sdk = "com.tencent.tbs:tbssdk:${Versions.TBS_SDK}"
    }

    object Update {
        //?????????????????????
        const val update_app_utilsx = "com.teprinciple:updateapputilsx:${Versions.UPDATE_APP_UTILSX}"
    }

    object Lottie {
        const val lottie = "'com.airbnb.android:lottie':${Versions.LOTTIE}"
    }

    object LetterSlideBar {

        /**
         * ??????????????????????????????
         *
         * ????????? https://github.com/gjiazhe/WaveSideBar
         */
        const val wave_side_bar = "com.gjiazhe:wavesidebar:${Versions.WAVE_SIDE_BAR}"

        /**
         * kotlin ??????????????????????????? ??????????????????????????????
         *
         * ????????? https://github.com/Leo199206/SideBar
         */
        const val side_bar = "com.github.Leo199206:SideBar:${Versions.SIDE_BAR}"
    }

    /**
     * ?????????????????????????????????
     *
     * ????????? https://github.com/JessYanCoding/AndroidAutoSize
     */
    object AndroidAutoSize {
        const val android_auto_size = "com.github.JessYanCoding:AndroidAutoSize:${Versions.ANDROID_AUTO_SIZE}"
    }
}