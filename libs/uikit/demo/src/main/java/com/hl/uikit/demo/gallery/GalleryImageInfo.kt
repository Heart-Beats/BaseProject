package com.hl.uikit.demo.gallery

import android.graphics.Rect

class GalleryImageInfo(imageUrl: String, bounds: Rect) /*: SimpleThumbViewInfo() {
    init {
        mUrl = imageUrl
        mBounds = bounds
    }
}*/

fun String.toGalleryImage(bounds: Rect = Rect()): GalleryImageInfo {
    return GalleryImageInfo(this, bounds)
}