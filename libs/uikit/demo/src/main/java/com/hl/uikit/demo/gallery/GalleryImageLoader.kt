package com.hl.uikit.demo.gallery

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hl.uikit.demo.R

// class GalleryImageLoader : IZoomMediaLoader {
//     override fun displayGifImage(
//         context: Fragment,
//         path: String,
//         imageView: ImageView?,
//         simpleTarget: MySimpleTarget
//     ) {
//         if (imageView == null) return
//         Glide.with(context)
//             .load(path)
//             .placeholder(R.drawable.placeholder_hld_gallery_image)
//             .error(R.drawable.placeholder_hld_gallery_image)
//             .listener(object : RequestListener<Drawable> {
//                 override fun onLoadFailed(
//                     e: GlideException?,
//                     model: Any?,
//                     target: Target<Drawable>?,
//                     isFirstResource: Boolean
//                 ): Boolean {
//                     simpleTarget.onLoadFailed(null)
//                     return false
//                 }
//
//                 override fun onResourceReady(
//                     resource: Drawable?,
//                     model: Any?,
//                     target: Target<Drawable>?,
//                     dataSource: DataSource?,
//                     isFirstResource: Boolean
//                 ): Boolean {
//                     simpleTarget.onResourceReady()
//                     return false
//                 }
//             })
//             .into(imageView)
//     }
//
//     override fun clearMemory(c: Context) {
//         Glide.get(c).clearMemory()
//     }
//
//     override fun displayImage(
//         context: Fragment,
//         path: String,
//         imageView: ImageView?,
//         simpleTarget: MySimpleTarget
//     ) {
//         if (imageView == null) return
//         Glide.with(context)
//             .load(path)
//             .placeholder(R.drawable.placeholder_hld_gallery_image)
//             .error(R.drawable.placeholder_hld_gallery_image)
//             .listener(object : RequestListener<Drawable> {
//                 override fun onLoadFailed(
//                     e: GlideException?,
//                     model: Any?,
//                     target: Target<Drawable>?,
//                     isFirstResource: Boolean
//                 ): Boolean {
//                     simpleTarget.onLoadFailed(null)
//                     return false
//                 }
//
//                 override fun onResourceReady(
//                     resource: Drawable?,
//                     model: Any?,
//                     target: Target<Drawable>?,
//                     dataSource: DataSource?,
//                     isFirstResource: Boolean
//                 ): Boolean {
//                     simpleTarget.onResourceReady()
//                     return false
//                 }
//             })
//             .into(imageView)
//     }
//
//     override fun onStop(context: Fragment) {
//         Glide.with(context).onStop()
//     }
// }