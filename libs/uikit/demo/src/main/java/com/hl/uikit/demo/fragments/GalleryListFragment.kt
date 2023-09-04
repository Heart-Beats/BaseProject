package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.gallery.GalleryImageInfo
import com.hl.uikit.demo.gallery.toGalleryImage
import com.hl.uikit.demo.util.dpInt
import com.hl.ui.utils.onClick

class GalleryListFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.fragment_gallery_list
    private val mImages = listOf(
        "https://pics0.baidu.com/feed/ac345982b2b7d0a2fae722573f74050e4a369a75.jpeg?token=826a2eb5b5abb8f67a37a38393363d43",
        "https://pics5.baidu.com/feed/83025aafa40f4bfb1a77204506adf9f7f6361861.jpeg?token=4f3d3870d4671a82d5a9cbdf56532a26",
        "https://pics0.baidu.com/feed/b3119313b07eca80f4e9ada753cce4daa34483fd.jpeg?token=5e1abc06817e937c70b63952eaba7806",
        "https://pics4.baidu.com/feed/f636afc379310a55e901c8cebefe37ae82261020.jpeg?token=6a444227898218208709ee1f2cc4c93",
        "https://pics4.baidu.com/feed/f636afc379310a55e901c8cebefe37ae82261020.jpeg?token=6a444227898218208709ee1f2cc4c93",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1603098160850&di=f7cebe7b86c4a4952b770cc3ce954368&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201405%2F31%2F124814kg76m5xeenbax6qn.jpg"
    )
    private lateinit var mLayoutManager: GridLayoutManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLayoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = mLayoutManager
        val galleryList = mutableListOf<GalleryImageInfo>()
        for (imageUrl in mImages) {
            galleryList.add(imageUrl.toGalleryImage())
        }
        val adapter = GalleryListAdapter(mImages).apply {
            onImageItemClickListener = { position, itemView ->
                computeBoundsBackward(galleryList, mLayoutManager.findFirstVisibleItemPosition())
                // GPreviewBuilder.from(this@GalleryListFragment)
                //     .setData(galleryList)
                //     .setCurrentIndex(position)
                //     .setSingleFling(true)
                //     .setDrag(false)
                //     .setType(GPreviewBuilder.IndicatorType.Number)
                //     .start()
            }
        }
        recyclerView.adapter = adapter
    }

    private fun computeBoundsBackward(
        galleryList: List<GalleryImageInfo>,
        firstCompletelyVisiblePos: Int
    ) {
        for (index in firstCompletelyVisiblePos until mImages.size) {
            val itemView = mLayoutManager.findViewByPosition(index)
            val gallery = galleryList[index]
            if (itemView is ImageView) {
                // itemView.getGlobalVisibleRect(gallery.bounds)
            }
        }
    }

    private class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private class GalleryListAdapter(val imageList: List<String>) :
        RecyclerView.Adapter<GalleryViewHolder>() {
        var onImageItemClickListener: (position: Int, itemView: View) -> Unit = { _, _ -> }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
            val context = parent.context
            val imageView = ImageView(context)
            imageView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                70.dpInt
            )
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            return GalleryViewHolder(imageView)
        }

        override fun getItemCount(): Int {
            return imageList.size
        }

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
            val imageUrl = imageList[position]
            val context = holder.itemView.context
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_hld_gallery_image)
                .error(R.drawable.placeholder_hld_gallery_image)
                .into(holder.itemView as ImageView)
            holder.itemView.updateLayoutParams<RecyclerView.LayoutParams> {
                marginStart = when {
                    position % 3 == 1 || position % 3 == 2 -> 10.dpInt
                    else -> 0.dpInt
                }
                topMargin = if (position <= 2) {
                    0.dpInt
                } else {
                    10.dpInt
                }
            }

            holder.itemView.onClick {
                onImageItemClickListener(holder.adapterPosition, it)
            }
        }
    }
}