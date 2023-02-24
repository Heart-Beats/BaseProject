package com.hl.baseproject.repository.network.bean

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


/**
 * @author  张磊  on  2023/02/23 at 17:44
 * Email: 913305160@qq.com
 */
@Keep
data class HomeArticleList(

	/**
	 * 当前页码
	 */
	var curPage: Int? = null,
	var datas: List<Article>? = null,
	var offset: Int? = null,
	var over: Boolean? = null,

	/**
	 * 当前页总数
	 */
	var pageCount: Int? = null,
	var size: Int? = null,

	/**
	 * 数据总数
	 */
	var total: Int? = null
)

@Keep
data class Article(
	@SerializedName("adminAdd")
	var admin: Boolean? = null,
	var apkLink: String? = null,
	var audit: Int? = null,
	var author: String? = null,
	var canEdit: Boolean? = null,
	var chapterId: Int? = null,

	/**
	 * 文章名称
	 */
	var chapterName: String? = null,
	var collect: Boolean? = null,
	var courseId: Int? = null,
	var desc: String? = null,
	var descMd: String? = null,
	var envelopePic: String? = null,
	var fresh: Boolean? = null,
	var host: String? = null,
	var id: Int? = null,
	var isAdminAdd: Boolean? = null,

	/**
	 * 跳转链接
	 */
	var link: String? = null,
	var niceDate: String? = null,
	var niceShareDate: String? = null,
	var origin: String? = null,
	var prefix: String? = null,
	var projectLink: String? = null,

	/**
	 * 文章发布时间
	 */
	var publishTime: Long? = null,
	var realSuperChapterId: Int? = null,
	var route: Boolean? = null,
	var selfVisible: Int? = null,
	var shareDate: Long? = null,

	/**
	 * 分享者
	 */
	var shareUser: String? = null,
	var superChapterId: Int? = null,
	var superChapterName: String? = null,
	var tags: List<Tag?>? = null,

	/**
	 * 文章标题
	 */
	var title: String? = null,
	var type: Int? = null,
	var userId: Int? = null,
	var visible: Int? = null,
	var zan: Int? = null
)

@Keep
data class Tag(
	var name: String? = null,
	var url: String? = null
)