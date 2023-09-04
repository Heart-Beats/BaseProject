package com.hl.uikit.image

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hl.uikit.R
import com.hl.uikit.actionsheet.ArrayListSheetDialogFragment
import com.hl.uikit.databinding.UikitUploadPicImageGridLayoutBinding
import com.hl.uikit.image.pictureselector.PickImageUtil
import com.hl.uikit.recyclerview.decoration.GridSpacingItemDecoration
import com.hl.uikit.reqPermissions
import com.hl.uikit.toast
import com.hl.uikit.utils.dpInt
import com.hl.uikit.utils.onClick
import com.hl.viewbinding.bindingMerge
import java.io.File


/**
 * @author 张磊  on  2022/02/12 at 15:10
 * Email: 913305160@qq.com
 */

class UIKitUploadPicImageGridLayout : FrameLayout {

    private lateinit var viewBinding: UikitUploadPicImageGridLayoutBinding

    /**
     * 最大列数
     */
    private var maxColumn = 3

    /**
     * 最大可选择图片数
     */
    private var maxSelectPictureCount = 9

    /**
     * item 之间的间隔，单位像素
     */
    private var itemSpace = 30

    /**
     * 添加 item 显示的背景图片
     */
    private var addItemRes = R.drawable.uikit_icon_pick_image

    /**
     * 是否可删除选择的图片
     */
    private var isCanDelete = true

    /**
     * 是否可添加选择的图片
     */
    private var isCanAdd = true

    /**
     * 图片的圆角大小, 单位像素
     */
    private var imageRoundRadius = 8.dpInt

    private lateinit var picImageAdapter: PicImageAdapter

    /**
     * 重新上传的监听
     */
    var onRetryUploadListener: (viewHolder: PicImageAdapter.ViewHolder, uploadPicture: UploadPicture) -> Unit =
        { _, _ -> }

    var onReadyUploadListener: (viewHolder: PicImageAdapter.ViewHolder, uploadPicture: UploadPicture) -> Unit =
        { _, _ -> }

    /**
     * 设置依附的 Activity ， 打开选择对话框时需要
     */
    var attachActivity: FragmentActivity? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_uploadPicImageGridLayout)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.obtainStyledAttributes(
            attrs, R.styleable.UIKitUploadPicImageGridLayout, defStyle, 0
        )

        ta.also {
            maxColumn = it.getInt(R.styleable.UIKitUploadPicImageGridLayout_uikit_maxColumn, maxColumn)
            maxSelectPictureCount = it.getInt(
                R.styleable.UIKitUploadPicImageGridLayout_uikit_maxSelectPictureCount, maxSelectPictureCount
            )
            itemSpace = it.getDimensionPixelSize(
                R.styleable.UIKitUploadPicImageGridLayout_uikit_itemSpace, itemSpace
            )
            addItemRes = it.getResourceId(
                R.styleable.UIKitUploadPicImageGridLayout_uikit_addItemRes, addItemRes
            )
            isCanDelete = it.getBoolean(R.styleable.UIKitUploadPicImageGridLayout_uikit_isCanDelete, isCanDelete)
            isCanAdd = it.getBoolean(R.styleable.UIKitUploadPicImageGridLayout_uikit_isCanDelete, isCanAdd)
            imageRoundRadius =
                it.getDimensionPixelSize(
                    R.styleable.UIKitUploadPicImageGridLayout_uikit_imageRoundRadius,
                    imageRoundRadius
                )
        }.recycle()

        viewBinding = bindingMerge()

        initView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (isInEditMode) {
            return
        }

        // 如果无任何图片重新设置无高度
        if (picImageAdapter.data.size == 0) {
            val height = resolveSize(0, heightMeasureSpec)
            setMeasuredDimension(width, height)
        }
    }

    private fun initView() {
        val pickImageRecyclerView = viewBinding.pickImageRecyclerView
        pickImageRecyclerView.layoutManager = GridLayoutManager(context, maxColumn)

        val data = mutableListOf<Picture>().apply {
            if (isCanAdd) add(PickPicture)
        }

        picImageAdapter = PicImageAdapter(data)
        pickImageRecyclerView.adapter = picImageAdapter
        pickImageRecyclerView.addItemDecoration(GridSpacingItemDecoration(itemSpace, false))
    }


    private fun showPickImageDialog() {
        if (attachActivity == null) {
            context.toast("未设置关联的 Activity！")
            return
        }

        val uploadPictures = picImageAdapter.data.filterIsInstance<UploadPicture>()
        if (uploadPictures.size >= maxSelectPictureCount) {
            context.toast("最多只可上传${maxSelectPictureCount}张图片")
            return
        }

        attachActivity?.run {
            ArrayListSheetDialogFragment<String>().apply {
                this.data = listOf("拍照", "相册")
                this.itemClickListener = { dialog, position ->
                    dialog.dismiss()

                    when (position) {
                        0 -> reqPermissions(Manifest.permission.CAMERA, allGrantedAction = {
                            startTakePhoto {
                                it.forEach { imagePath ->
                                    val uploadPicture =
                                        UploadPicture(path = imagePath, isTakePhoto = false)
                                    picImageAdapter.addPicture(uploadPicture)
                                }
                            }
                        }, deniedAction = {
                            this@UIKitUploadPicImageGridLayout.context.toast("未授予相机权限")
                        })

                        1 -> reqPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, allGrantedAction = {
                            startPictureSelect(maxSelectPictureCount - uploadPictures.size) {
                                it.forEach { imagePath ->
                                    val uploadPicture =
                                        UploadPicture(path = imagePath, isTakePhoto = false)
                                    picImageAdapter.addPicture(uploadPicture)
                                }
                            }
                        }, deniedAction = {
                            this@UIKitUploadPicImageGridLayout.context.toast("未授予存储权限")
                        })
                    }
                }
            }.show(attachActivity?.supportFragmentManager ?: return, "")
        }
    }

    private fun startTakePhoto(nextAction: (List<String>) -> Unit) {
        PickImageUtil.startTakePhoto(context) {
            nextAction(it)
        }
    }

    private fun startPictureSelect(maxCount: Int, nextAction: (List<String>) -> Unit) {
        PickImageUtil.startPictureSelect(context, option = {
            this.setMaxSelectNum(maxCount)
        }) {
            nextAction(it)
        }
    }

    fun setData(data: MutableList<Picture>) {
        if (isCanAdd) data.add(PickPicture)
        picImageAdapter.updateData(data)
    }

    /**
     * 当前是否正在上传图片中
     */
    fun isUploading(): Boolean {
        return getUploadPictureData().any {
            it.isUploading()
        }
    }

    /**
     * 获取上传成功的图片集合地址
     */
    fun getUploadSuccessUrls(): List<String> {
        return getUploadPictureData().filter {
            it.isUploadSuccess()
        }.map {
            it.url ?: ""
        }
    }

    private fun getUploadPictureData(): List<UploadPicture> {
        return picImageAdapter.data.filterIsInstance<UploadPicture>()
    }

    inner class PicImageAdapter(val data: MutableList<Picture>) :
        RecyclerView.Adapter<PicImageAdapter.ViewHolder>() {


        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val uploadImage: UIKitProgressImageView = itemView.findViewById(R.id.item_upload_image)
            val deleteImage: ImageView = itemView.findViewById(R.id.item_delete_image)
            val uploadFailTips: TextView = itemView.findViewById(R.id.item_upload_fail_tips)

            fun updateUploadProgress(progress: Int) {
                val picture = data[layoutPosition]
                uploadImage.progress = progress

                if (picture is UploadPicture) {
                    picture.uploadProgress = progress
                }
            }

            fun updateUploadState(uploadState: UploadState) {
                val picture = data[layoutPosition]
                if (picture is UploadPicture) {
                    when (uploadState) {
                        UploadState.UPLOAD_SUCCESS -> {
                            // 拍照的照片上传完成删除
                            if (picture.isTakePhoto) {
                                val file = File(picture.path ?: "")
                                if (file.exists()) {
                                    file.delete()
                                }
                            }

                            updateUploadProgress(100)
                        }
                        UploadState.UPLOAD_FAILED -> {
                            updateUploadProgress(0)
                        }
                        else -> {}
                    }

                    // 非上传中时更新 item
                    if (uploadState != UploadState.UPLOADING) {
                        picture.uploadState = uploadState
                        picImageAdapter.notifyItemChanged(layoutPosition)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.uikit_item_pick_image_upload, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            setItemClickListener(holder, position)

            when (val picture = data[position]) {
                is UploadPicture -> {
                    onBindNormalUploadPicture(holder, picture)
                }
                is PickPicture -> {
                    // 选择项默认不需上传进度
                    holder.uploadImage.progress = 100
                    holder.deleteImage.visibility = View.GONE
                    holder.uploadFailTips.visibility = View.GONE

                    val addItemBitmap = BitmapFactory.decodeResource(resources, addItemRes)
                    val addItemRoundBitmap = RoundedBitmapDrawableFactory.create(resources, addItemBitmap).apply {
                        this.cornerRadius = imageRoundRadius.toFloat()
                    }
                    holder.uploadImage.background = addItemRoundBitmap

                    // 下面方法设置全圆角仅左上角生效
                    // holder.uploadImage.apply {
                    //     shapeAppearanceModel = ShapeAppearanceModel.builder()
                    //         .setAllCorners(CornerFamily.ROUNDED, imageRoundRadius.toFloat())
                    //         .build()
                    //     setBackgroundResource(addItemRes)
                    // }
                }
            }
        }

        private fun setItemClickListener(holder: ViewHolder, position: Int) {
            val picture = data[position]
            holder.uploadImage.onClick {
                if (picture is PickPicture) {
                    showPickImageDialog()
                }
            }

            holder.deleteImage.onClick {
                data.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, data.size - 1)
            }

            holder.uploadFailTips.onClick {
                if (picture is UploadPicture && picture.isUploadFailed()) {
                    // 上传失败时可重新上传
                    onRetryUploadListener(holder, picture)
                }
            }
        }

        private fun onBindNormalUploadPicture(holder: ViewHolder, picture: UploadPicture) {
            Glide.with(context)
                .load(picture.path ?: picture.url)
                .transform(RoundedCorners(imageRoundRadius))
                .into(holder.uploadImage)

            holder.uploadImage.progress = picture.uploadProgress

            val deleteRes = if (picture.isUploadSuccess()) R.drawable.uikit_icon_img_delete else 0
            holder.deleteImage.setBackgroundResource(deleteRes)


            when {
                picture.isUploadReady() -> {
                    holder.deleteImage.visibility = View.GONE
                    holder.uploadFailTips.visibility = View.GONE

                    onReadyUploadListener(holder, picture)
                }
                picture.isUploading() ->{
                    holder.deleteImage.visibility = View.GONE
                    holder.uploadFailTips.visibility = View.GONE
                }
                picture.isUploadSuccess() -> {
                    val deleteVisibility = if (isCanDelete) View.VISIBLE else View.GONE
                    holder.deleteImage.visibility = deleteVisibility

                    holder.uploadFailTips.visibility = View.GONE
                }
                picture.isUploadFailed() -> {
                    val deleteVisibility = if (isCanDelete) View.VISIBLE else View.GONE
                    holder.deleteImage.visibility = deleteVisibility

                    holder.uploadFailTips.visibility = View.VISIBLE
                }
            }
        }

        override fun getItemCount(): Int = data.size

        fun addPicture(picture: UploadPicture) {
            val pickPictureIndex = data.indexOfFirst { it == PickPicture }
            if (pickPictureIndex == -1) {
                data.add(picture)
                notifyItemChanged(data.size - 1)
            } else {
                data.add(pickPictureIndex, picture)
                notifyItemInserted(pickPictureIndex)
                notifyItemRangeChanged(pickPictureIndex, data.size - 1)
            }
        }

        fun updateData(data: MutableList<Picture>) {
            this.data.run {
                clear()
                addAll(data)
            }
            // notifyItemRangeChanged(0, this.data.size - 1)
            notifyDataSetChanged()
        }
    }
}