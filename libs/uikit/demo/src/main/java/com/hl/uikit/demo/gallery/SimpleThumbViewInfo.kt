package com.hl.uikit.demo.gallery

import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable

// open class SimpleThumbViewInfo : IThumbViewInfo {
//     protected var mUrl: String? = null
//     protected var mVideoUrl: String? = null
//     protected var mBounds: Rect? = null
//
//     constructor() : super() {
//     }
//
//     constructor(parcel: Parcel) : this() {
//         mUrl = parcel.readString()
//         mVideoUrl = parcel.readString()
//         mBounds = parcel.readParcelable(Rect::class.java.classLoader)
//     }
//
//     override fun getUrl(): String? {
//         return mUrl
//     }
//
//     fun setUrl(url: String) {
//         mUrl = url
//     }
//
//     override fun getVideoUrl(): String? {
//         return mVideoUrl
//     }
//
//     fun setVideoUrl(videoUrl: String) {
//         mVideoUrl = videoUrl
//     }
//
//     override fun getBounds(): Rect? {
//         return mBounds
//     }
//
//     fun setBounds(bounds: Rect) {
//         mBounds = bounds
//     }
//
//     override fun writeToParcel(parcel: Parcel, flags: Int) {
//         parcel.writeString(mUrl)
//         parcel.writeString(mVideoUrl)
//         parcel.writeParcelable(mBounds, flags)
//     }
//
//     override fun describeContents(): Int {
//         return 0
//     }
//
//     companion object CREATOR : Parcelable.Creator<SimpleThumbViewInfo> {
//         override fun createFromParcel(parcel: Parcel): SimpleThumbViewInfo {
//             return SimpleThumbViewInfo(parcel)
//         }
//
//         override fun newArray(size: Int): Array<SimpleThumbViewInfo?> {
//             return arrayOfNulls(size)
//         }
//     }
// }