package com.hl.utils.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * 中国农历工具类-
 */
object LunarUtil {
	private val chineseNumber = arrayOf("正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊")

	private var chineseDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy年MM月dd日")

	private val lunarInfo = longArrayOf(
		0x4bd8, 0x4ae0, 0xa570, 0x54d5, 0xd260, 0xd950, 0x5554, 0x56af, 0x9ad0, 0x55d2,
		0x4ae0, 0xa5b6, 0xa4d0, 0xd250, 0xd255, 0xb54f, 0xd6a0, 0xada2, 0x95b0, 0x4977,
		0x497f, 0xa4b0, 0xb4b5, 0x6a50, 0x6d40, 0xab54, 0x2b6f, 0x9570, 0x52f2, 0x4970,
		0x6566, 0xd4a0, 0xea50, 0x6a95, 0x5adf, 0x2b60, 0x86e3, 0x92ef, 0xc8d7, 0xc95f,
		0xd4a0, 0xd8a6, 0xb55f, 0x56a0, 0xa5b4, 0x25df, 0x92d0, 0xd2b2, 0xa950, 0xb557,
		0x6ca0, 0xb550, 0x5355, 0x4daf, 0xa5b0, 0x4573, 0x52bf, 0xa9a8, 0xe950, 0x6aa0,
		0xaea6, 0xab50, 0x4b60, 0xaae4, 0xa570, 0x5260, 0xf263, 0xd950, 0x5b57, 0x56a0,
		0x96d0, 0x4dd5, 0x4ad0, 0xa4d0, 0xd4d4, 0xd250, 0xd558, 0xb540, 0xb6a0, 0x95a6,
		0x95bf, 0x49b0, 0xa974, 0xa4b0, 0xb27a, 0x6a50, 0x6d40, 0xaf46, 0xab60, 0x9570,
		0x4af5, 0x4970, 0x64b0, 0x74a3, 0xea50, 0x6b58, 0x5ac0, 0xab60, 0x96d5, 0x92e0,
		0xc960, 0xd954, 0xd4a0, 0xda50, 0x7552, 0x56a0, 0xabb7, 0x25d0, 0x92d0, 0xcab5,
		0xa950, 0xb4a0, 0xbaa4, 0xad50, 0x55d9, 0x4ba0, 0xa5b0, 0x5176, 0x52bf, 0xa930,
		0x7954, 0x6aa0, 0xad50, 0x5b52, 0x4b60, 0xa6e6, 0xa4e0, 0xd260, 0xea65, 0xd530,
		0x5aa0, 0x76a3, 0x96d0, 0x4afb, 0x4ad0, 0xa4d0, 0xd0b6, 0xd25f, 0xd520, 0xdd45,
		0xb5a0, 0x56d0, 0x55b2, 0x49b0, 0xa577, 0xa4b0, 0xaa50, 0xb255, 0x6d2f, 0xada0,
		0x4b63, 0x937f, 0x49f8, 0x4970, 0x64b0, 0x68a6, 0xea5f, 0x6b20, 0xa6c4, 0xaaef,
		0x92e0, 0xd2e3, 0xc960, 0xd557, 0xd4a0, 0xda50, 0x5d55, 0x56a0, 0xa6d0, 0x55d4,
		0x52d0, 0xa9b8, 0xa950, 0xb4a0, 0xb6a6, 0xad50, 0x55a0, 0xaba4, 0xa5b0, 0x52b0,
		0xb273, 0x6930, 0x7337, 0x6aa0, 0xad50, 0x4b55, 0x4b6f, 0xa570, 0x54e4, 0xd260,
		0xe968, 0xd520, 0xdaa0, 0x6aa6, 0x56df, 0x4ae0, 0xa9d4, 0xa4d0, 0xd150, 0xf252,
		0xd520
	)

	// 农历部分假日
	private val lunarHoliday = arrayOf(
		"0101 春节",
		"0115 元宵节",
		"0505 端午节",
		"0707 七夕情人节",
		"0715 中元节 孟兰节",
		"0730 地藏节",
		"0802 灶君诞",
		"0815 中秋节",
		"0827 先师诞",
		"0909 重阳节",
		"1208 腊八节  释迦如来成道日",
		"1223 小年",
		"0100 除夕"
	)

	// 公历部分节假日
	private val solarHoliday = arrayOf(
		"0101 元旦",
		"0110 中国110宣传日",
		"0214 情人",
		"0221 国际母语日",
		"0303 国际爱耳日",
		"0308 妇女节",
		"0312 植树节",
		"0315 消费者权益日",
		"0322 世界水日",
		"0323 世界气象日",
		"0401 愚人节",
		"0407 世界卫生日",
		"0501 劳动节",
		"0504 青年节",
		"0512 护士节",
		"0519 全国助残日",
		"0531 世界无烟日",
		"0601 儿童节",
		"0626 国际禁毒日",
		"0701 建党节",  //1921
		"0801 建军节",  //1933
		//"0808 父亲节",
		"0909 毛泽东逝世纪念",  //1976
		"0910 教师节",
		"0917 国际和平日",
		"0927 世界旅游日",
		"0928 孔子诞辰",
		"1001 国庆节",
		"1006 老人节",
		"1007 国际住房日",
		"1014 世界标准日",
		"1024 联合国日",
		"1112 孙中山诞辰纪念",
		"1210 世界人权日",
		"1220 澳门回归纪念",
		"1224 平安夜",
		"1225 圣诞节",
		"1226 毛泽东诞辰纪念"
	)

	//24节气
	private val sTermInfo = arrayOf( // 时节     气候
		"小寒", "大寒",
		"立春", "雨水",
		"惊蛰", "春分",
		"清明", "谷雨",
		"立夏", "小满",
		"芒种", "夏至",
		"小暑", "大暑",
		"立秋", "处暑",
		"白露", "秋分",
		"寒露", "霜降",
		"立冬", "小雪",
		"大雪", "冬至"
	)

	private val constellations = arrayOf(
		"摩蝎座:12.22—01.19", "水瓶座:01.20—02.18", "双鱼座:02.19—03.20", "白羊座:03.21—04.19",
		"金牛座:04.20—05.20", "双子座:05.21—06.20", "巨蟹座:06.21—07.21", "狮子座:07.22—08.22",
		"处女座:08.23—09.22", "天秤座:09.23—10.22", "天蝎座:10.23—11.21", "射手座:11.22—12.21"
	)

	private val yiString = arrayOf(
		"出行.上任.会友.上书.见工", "除服.疗病.出行.拆卸.入宅",
		"祈福.祭祀.结亲.开市.交易", "祭祀.修填.涂泥.余事勿取",
		"交易.立券.会友.签约.纳畜", "祈福.祭祀.求子.结婚.立约",
		"求医.赴考.祭祀.余事勿取", "经营.交易.求官.纳畜.动土",
		"祈福.入学.开市.求医.成服", "祭祀.求财.签约.嫁娶.订盟",
		"疗病.结婚.交易.入仓.求职", "祭祀.交易.收财.安葬"
	)

	private val jiString = arrayOf(
		"动土.开仓.嫁娶.纳采", "求官.上任.开张.搬家.探病",
		"服药.求医.栽种.动土.迁移", "移徙.入宅.嫁娶.开市.安葬",
		"种植.置业.卖田.掘井.造船", "开市.交易.搬家.远行",
		"动土.出行.移徙.开市.修造", "登高.行船.安床.入宅.博彩",
		"词讼.安门.移徙", "开市.安床.安葬.入宅.破土",
		"安葬.动土.针灸", "宴会.安床.出行.嫁娶.移徙"
	)

	private val jcName by lazy {
		arrayOf(
			arrayOf("建", "除", "满", "平", "定", "执", "破", "危", "成", "收", "开", "闭"),
			arrayOf("闭", "建", "除", "满", "平", "定", "执", "破", "危", "成", "收", "开"),
			arrayOf("开", "闭", "建", "除", "满", "平", "定", "执", "破", "危", "成", "收"),
			arrayOf("收", "开", "闭", "建", "除", "满", "平", "定", "执", "破", "危", "成"),
			arrayOf("成", "收", "开", "闭", "建", "除", "满", "平", "定", "执", "破", "危"),
			arrayOf("危", "成", "收", "开", "闭", "建", "除", "满", "平", "定", "执", "破"),
			arrayOf("破", "危", "成", "收", "开", "闭", "建", "除", "满", "平", "定", "执"),
			arrayOf("执", "破", "危", "成", "收", "开", "闭", "建", "除", "满", "平", "定"),
			arrayOf("定", "执", "破", "危", "成", "收", "开", "闭", "建", "除", "满", "平"),
			arrayOf("平", "定", "执", "破", "危", "成", "收", "开", "闭", "建", "除", "满"),
			arrayOf("满", "平", "定", "执", "破", "危", "成", "收", "开", "闭", "建", "除"),
			arrayOf("除", "满", "平", "定", "执", "破", "危", "成", "收", "开", "闭", "建")
		)
	}

	private val Gan = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")

	private val Zhi = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")


	/**
	 * 得到当前年对应的农历年份
	 *
	 * @return
	 */
	private var year = 0

	private var month = 0

	private var day = 0

	/**
	 * 得到当前日期对应的阴历月份
	 *
	 * @return
	 */
	private var lunarMonth: String? = null

	private var leap = false

	/*
	* 闰的是哪个月
	*/
	private var leapMonth = 0

	/**
	 * 传入月日的 offset 传回干支, 0=甲子
	 */
	private fun cyclicalm(num: Int): String {
		return Gan[num % 10] + Zhi[num % 12]
	}


	/**
	 * 将农历 day 日格式化成农历表示的字符串
	 */
	private fun getChinaDayString(day: Int): String {
		val chineseTen = arrayOf("初", "十", "廿", "卅")
		val chineseDay = arrayOf(
			"一", "二", "三", "四", "五", "六", "七",
			"八", "九", "十"
		)
		val chinaDayString: String = if (day != 20 && day != 30) {
			chineseTen[((day - 1) / 10)] + chineseDay[((day - 1) % 10)]
		} else if (day != 20) {
			chineseTen[(day / 10)] + "十"
		} else {
			"二十"
		}
		return chinaDayString
	}

	/**
	 * 传回农历 y 年的总天数 1900--2100
	 */
	fun yearDays(y: Int): Int {
		var i: Int
		var sum = 348
		i = 0x8000
		while (i > 0x8) {
			if (lunarInfo[y - 1900] and i.toLong() != 0L) sum += 1
			i = i shr 1
		}
		return sum + leapDays(y)
	}

	/**
	 * 传回农历 y 年闰月的天数
	 */
	fun leapDays(y: Int): Int {
		return if (leapMonth(y) != 0) {
			if (lunarInfo[y - 1899] and 0xf != 0L) 30 else 29
		} else 0
	}

	/**
	 * 传回农历 y 年闰 哪个月 1-12 , 没闰传回 0
	 */
	fun leapMonth(y: Int): Int {
		val `var` = lunarInfo[y - 1900] and 0xf
		return (if (`var` == 0xfL) 0 else `var`).toInt()
	}

	/**
	 * 传回农历 y年m月的总天数
	 */
	fun monthDays(y: Int, m: Int): Int {
		return if (lunarInfo[y - 1900] and (0x10000 shr m).toLong() == 0L) 29 else 30
	}

	/**
	 * 传入 offset 传回干支, 0=甲子
	 */
	private fun cyclical(year: Int, month: Int, day: Int): String {
		var num = year - 1900 + 36
		//立春日期
		val term2 = sTerm(year, 2)
		num = if (month > 2 || month == 2 && day >= term2) {
			num + 0
		} else {
			num - 1
		}
		return cyclicalm(num)
	}

	/**
	 * 计算公历nY年nM月nD日和bY年bM月bD日渐相差多少天
	 */
	private fun getDaysOfTwoDate(bY: Int, bM: Int, bD: Int, nY: Int, nM: Int, nD: Int): Int {
		var baseDate: Date? = null
		var nowaday: Date? = null
		try {
			baseDate = chineseDateFormat.parse(bY.toString() + "年" + bM + "月" + bD + "日")
			nowaday = chineseDateFormat.parse(nY.toString() + "年" + nM + "月" + nD + "日")
		} catch (e: ParseException) {
			e.printStackTrace()
		}
		// 求出相差的天数
		return ((nowaday!!.time - baseDate!!.time) / 86400000L).toInt()
	}


	/**
	 * 将公历 year 年 month 月 day 日转换成农历
	 * 返回格式为20140506（int）
	 * */
	private fun getLunarDateINT(year: Int, month: Int, day: Int): Int {
		val lMonth: Int
		val lDay: Int
		var daysOfYear = 0
		// 求出和1900年1月31日相差的天数
		//year =1908;
		//month = 3;
		//day =3;
		var offset = getDaysOfTwoDate(1900, 1, 31, year, month, day)
		//Log.i("--ss--","公历:"+year+"-"+month+"-"+day+":"+offset);
		// 用offset减去每农历年的天数
		// 计算当天是农历第几天
		// i最终结果是农历的年份
		// offset是当年的第几天
		var iYear = 1900
		while (iYear < 2100 && offset > 0) {
			daysOfYear = yearDays(iYear)
			offset -= daysOfYear
			iYear++
		}
		if (offset < 0) {
			offset += daysOfYear
			iYear--
			//Log.i("--ss--","农历:"+iYear+":"+daysOfYear+"/"+offset);
		}
		// 农历年份
		val lYear = iYear
		leapMonth = leapMonth(iYear) // 闰哪个月,1-12
		leap = false

		// 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
		var daysOfMonth = 0
		var iMonth = 1
		while (iMonth < 13 && offset > 0) {

			// 闰月
			if (leapMonth > 0 && iMonth == leapMonth + 1 && !leap) {
				--iMonth
				leap = true
				daysOfMonth = leapDays(iYear)
			} else {
				daysOfMonth = monthDays(iYear, iMonth)
			}
			// 解除闰月
			if (leap && iMonth == leapMonth + 1) leap = false
			offset -= daysOfMonth
			iMonth++
		}
		// offset为0时，并且刚才计算的月份是闰月，要校正
		if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
			if (leap) {
				leap = false
			} else {
				leap = true
				--iMonth
			}
		}
		// offset小于0时，也要校正
		if (offset < 0) {
			offset += daysOfMonth
			--iMonth
			//Log.i("--ss--","农历:"+iYear+"-"+iMonth+":"+daysOfMonth+"/"+offset);
		}
		lMonth = iMonth
		lDay = offset + 1
		//Log.i("--ss--","农历:"+LYear+"-"+LMonth+"-"+LDay);
		return lYear * 10000 + lMonth * 100 + lDay
	}

	/**
	 * 传出 y 年 m 月 d 日对应的农历. yearCyl3:农历年与1864的相差数 ? monCyl4:从1900年1月31日以来,闰月数
	 * dayCyl5:与1900年1月31日相差的天数,再加40 ?
	 *
	 *
	 *
	 * @param isDay: 这个参数为false---日期为节假日时，阴历日期就返回节假日 ，true---不管日期是否为节假日依然返回这天对应的阴历日期
	 * @return
	 */
	fun getLunarDate(calendar: Calendar = Calendar.getInstance(), isDay: Boolean): String {
		val yearLog = calendar[Calendar.YEAR]
		val monthLog = calendar[Calendar.MONTH] + 1
		val dayLog = calendar[Calendar.DATE]

		val lunarDateINT = getLunarDateINT(yearLog, monthLog, dayLog)
		// 设置公历对应的农历年份
		year = lunarDateINT / 10000

		//农历月份
		month = (lunarDateINT % 10000) / 100
		// 设置对应的阴历月份
		lunarMonth = chineseNumber[(month - 1) % 12] + "月"
		day = lunarDateINT - year * 10000 - month * 100
		if (!isDay) {
			val festival = getFestival(calendar)
			if (festival.length > 1) {
				return festival
			}
		}
		return if (day == 1) chineseNumber[(month - 1) % 12] + "月" else getChinaDayString(day)
	}

	override fun toString(): String {
		return if (chineseNumber[(month - 1) % 12] === "正" && getChinaDayString(day) === "初一") "农历" + year + "年" else if (getChinaDayString(
				day
			) === "初一"
		) chineseNumber[(month - 1) % 12] + "月" else getChinaDayString(day)
		// return year + "年" + (leap ? "闰" : "") + chineseNumber[month - 1] +
		// "月" + getChinaDayString(day);
	}

	private fun sTerm(y: Int, n: Int): Int {
		val sTermInfo = intArrayOf(
			0, 21208, 42467, 63836, 85337, 107014,
			128867, 150921, 173149, 195551, 218072, 240693,
			263343, 285989, 308563, 331033, 353350, 375494,
			397447, 419210, 440795, 462224, 483532, 504758
		)
		val cal = Calendar.getInstance()
		cal[1900, 0, 6, 2, 5] = 0
		val temp = cal.time.time
		cal.time = Date((31556925974.7 * (y - 1900) + sTermInfo[n] * 60000L + temp).toLong())
		return cal[Calendar.DAY_OF_MONTH]
	}

	private fun getDateOfSolarTerms(year: Int, month: Int): Int {
		val a = sTerm(year, (month - 1) * 2)
		val b = sTerm(year, (month - 1) * 2 + 1)
		return a * 100 + b
		//return 0;
	}

	private fun jcrt(d: String): String {
		var jcrjxt = ""
		val yj0 = "宜:\t"
		val yj1 = "忌:\t"
		val br = "-"
		//String yj0 = "",yj1 = "",br = "-";
		if (d === "建") jcrjxt = yj0 + yiString[0] + br + yj1 + jiString[0]
		if (d === "除") jcrjxt = yj0 + yiString[1] + br + yj1 + jiString[1]
		if (d === "满") jcrjxt = yj0 + yiString[2] + br + yj1 + jiString[2]
		if (d === "平") jcrjxt = yj0 + yiString[3] + br + yj1 + jiString[3]
		if (d === "定") jcrjxt = yj0 + yiString[4] + br + yj1 + jiString[4]
		if (d === "执") jcrjxt = yj0 + yiString[5] + br + yj1 + jiString[5]
		if (d === "破") jcrjxt = yj0 + yiString[6] + br + yj1 + jiString[6]
		if (d === "危") jcrjxt = yj0 + yiString[7] + br + yj1 + jiString[7]
		if (d === "成") jcrjxt = yj0 + yiString[8] + br + yj1 + jiString[8]
		if (d === "收") jcrjxt = yj0 + yiString[9] + br + yj1 + jiString[9]
		if (d === "开") jcrjxt = yj0 + yiString[10] + br + yj1 + jiString[10]
		if (d === "闭") jcrjxt = yj0 + yiString[11] + br + yj1 + jiString[11]
		return jcrjxt
	}

	/**
	 * num_y%12, num_m%12, num_d%12, num_y%10, num_d%10
	 * m:   农历月份 1---12
	 * dt：农历日
	 */
	private fun calConv2(yy: Int, mm: Int, dd: Int, y: Int, d: Int, m: Int, dt: Int): String? {
		val dy = d.toString() + "" + dd
		return if (yy == 0 && dd == 6 || yy == 6 && dd == 0 || yy == 1 && dd == 7 ||
			yy == 7 && dd == 1 || yy == 2 && dd == 8 || yy == 8 && dd == 2 ||
			yy == 3 && dd == 9 || yy == 9 && dd == 3 || yy == 4 && dd == 10 ||
			yy == 10 && dd == 4 || yy == 5 && dd == 11 || yy == 11 && dd == 5
		) {
			/*
                  * 地支共有六对相冲，俗称“六冲”，即：子午相冲、丑未相冲、寅申相冲、卯酉相冲、辰戌相冲、巳亥相冲。
                  * 如当年是甲子年，子为太岁，与子相冲者是午，假如当日是午日，则为岁破，其余的以此类推，即丑年的岁破为未日，
                  * 寅年的岁破为申日，卯年的岁破为酉日，辰年的岁破为戌日，巳年的岁破为亥日，午年的岁破为子日，未年的岁破为丑日，
                  * 申年的岁破为寅日，酉年的岁破为卯日，戌年的岁破为辰日，亥年的岁破为巳日。
                  * */
			"日值岁破 大事不宜"
		} else if (mm == 0 && dd == 6 || mm == 6 && dd == 0 || mm == 1 && dd == 7 || mm == 7 && dd == 1 ||
			mm == 2 && dd == 8 || mm == 8 && dd == 2 || mm == 3 && dd == 9 || mm == 9 && dd == 3 ||
			mm == 4 && dd == 10 || mm == 10 && dd == 4 || mm == 5 && dd == 11 || mm == 11 && dd == 5
		) {
			"日值月破 大事不宜"
		} else if (y == 0 && dy === "911" || y == 1 && dy === "55" || y == 2 && dy === "111" ||
			y == 3 && dy === "75" || y == 4 && dy === "311" || y == 5 && dy === "95" ||
			y == 6 && dy === "511" || y == 7 && dy === "15" || y == 8 && dy === "711" || y == 9 && dy === "35"
		) {
			"日值上朔 大事不宜"
		} else if (m == 1 && dt == 13 || m == 2 && dt == 11 || m == 3 && dt == 9 || m == 4 && dt == 7 ||
			m == 5 && dt == 5 || m == 6 && dt == 3 || m == 7 && dt == 1 || m == 7 && dt == 29 ||
			m == 8 && dt == 27 || m == 9 && dt == 25 || m == 10 && dt == 23 || m == 11 && dt == 21 ||
			m == 12 && dt == 19
		) {
			/*
            * 杨公十三忌   以农历正月十三日始，以后每月提前两天为百事禁忌日
            * */
			"日值杨公十三忌 大事不宜"
			//}else if(var == getSiLi(m,dt)){
			//return "日值四离  大事勿用";
		} else {
			null
		}
	}


	/**
	 * 返回公历 year 年 month 月的当月总天数
	 */
	private fun getDaysOfMonth(year: Int, month: Int): Int {
		if (month == 2) {
			return if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
		} else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			return 31
		}
		return 30
	}


	/**
	 * year 公历年份，大于 1900
	 * month 公历月份  1--12
	 * 输出 ArrayList<String> year年month月全月宜忌
	 */
	fun getYiJi(year: Int, month: Int): ArrayList<String?> {
		var numY = -1
		var numM = (year - 1900) * 12 + month - 1 + 12
		var numD: Int
		var mLMonth = 1
		var mLDay = 1
		var mlunX = 0
		val daysOfMonth = getDaysOfMonth(year, month)
		val yiji = ArrayList<String?>()

		//年柱 1900年立春后为庚子年(60进制36)
		//cyclical(year,month);
		//立春日期
		val term2 = sTerm(year, 2)

		//月柱  1900年1月小寒以前为 丙子月(60进制12)
		val firstNode = sTerm(year, (month - 1) * 2) //当月的24节气中的节开始日
		//cyclicalm(num_m);
		//Calendar cal = Calendar.getInstance();
		//cal.set(year, month, 1, 0, 0, 0);
		//1900/1/1与 1970/1/1 相差25567日, 1900/1/1 日柱为甲戌日(60进制10)
		for (i in 0 until daysOfMonth) {
			if (mLDay > mlunX) {
				//Log.i("","mLDay > mLun_x "+mLDay+":"+mLun_x);
				val `var` = getLunarDateINT(year, month, i + 1)
				val mLYear = `var` / 10000
				mLMonth = (`var` % 10000) / 100
				mLDay = (`var` - mLYear * 10000 - mLMonth * 100)
				mlunX = if (leapMonth(mLYear) != 0) leapDays(mLYear) else monthDays(mLYear, mLMonth)
				//Log.i("","mLDay > mLun_x ?"+mLDay+":"+mLun_x);
			}
			//依节气调整二月分的年柱, 以立春为界
			if (month == 2 && i + 1 == term2) {
				//cY = cyclicalm(year-1900 + 36);
				numY = year - 1900 + 36
			}
			//依节气 调整月柱, 以「节」为界
			if (i + 1 == firstNode) {
				numM = (year - 1900) * 12 + month + 12
				//cM = cyclicalm(num_m);
			}
			//日柱
			//cD = cyclicalm(dayCyclical + i);
			numD = (getDaysOfTwoDate(1900, 1, 1, year, month, 1) + 10) + i
			mLDay++
			//Log.i("","---num_y:"+num_y+","+num_m+","+num_d+",n_m:"+mLMonth+",n_d:"+mLDay+",mLun_x:"+mLun_x);
			var str = calConv2(numY % 12, numM % 12, numD % 12, numY % 10, numD % 10, mLMonth, mLDay - 1)
			if (str == null) {
				val `var` = jcName[numM % 12][numD % 12]
				str = jcrt(`var`)
				//Log.i("","---"+month+"-"+(i+1)+","+var+":"+str);
			}
			//Log.i("","---"+year+"-"+month+"-"+(i+1)+","+str);
			yiji.add(str)
		}
		return yiji
	}

	/**
	 * 公历某日宜忌
	 */
	fun getYiJi(year: Int, month: Int, day: Int): String {
		var numDay = day
		var numY = -1
		var numM = (year - 1900) * 12 + month - 1 + 12
		val mLMonth: Int
		val mLDay: Int
		val daysOfMonth = getDaysOfMonth(year, month)
		if (numDay > daysOfMonth) numDay = daysOfMonth

		//年柱 1900年立春后为庚子年(60进制36)
		//cyclical(year,month);
		//立春日期
		val term2 = sTerm(year, 2)
		val firstNode = sTerm(year, (month - 1) * 2) //当月的24节气中的节开始日
		if (month == 2 && numDay == term2) {
			//cY = cyclicalm(year-1900 + 36);//依节气调整二月分的年柱, 以立春为界
			numY = year - 1900 + 36
		}
		if (numDay == firstNode) {
			numM = (year - 1900) * 12 + month + 12 //依节气 调整月柱, 以「节」为界
			//cM = cyclicalm(num_m);
		}
		val numD = (getDaysOfTwoDate(1900, 1, 1, year, month, 1) + 10) + numDay - 1
		val `var` = getLunarDateINT(year, month, numDay)
		val mLYear = `var` / 10000
		mLMonth = (`var` % 10000) / 100
		mLDay = (`var` - mLYear * 10000 - mLMonth * 100)

		//Log.i("","---num_y:"+num_y+","+num_m+","+num_d+",n_m:"+mLMonth+",n_d:"+mLDay+",mLun_x:"+mLun_x);
		var str = calConv2(numY % 12, numM % 12, numD % 12, numY % 10, numD % 10, mLMonth, mLDay)
		if (str == null) {
			str = jcrt(jcName[numM % 12][numD % 12])
			//Log.i("","---"+month+"-"+(i+1)+","+var+":"+str);
		}
		//Log.i("","---"+year+"-"+month+"-"+(i+1)+","+str);
		return str
	}


	/**
	 * 从农历日期转换成公历日期
	 *
	 * 农历 lunYear 年 lunMonth 月 lunDay 日
	 * isLeap 当前年月是否是闰月
	 *
	 */
	fun getSunDate(lunYear: Int, lunMonth: Int, lunDay: Int, isLeap: Boolean): Calendar {
		//公历1900年1月31日为1900年正月初一
		val years = lunYear - 1900
		var days = 0
		for (i in 0 until years) {
			days += yearDays(1900 + i) //农历某年总天数
		}
		for (i in 1 until lunMonth) {
			days += monthDays(lunYear, i)
		}
		if (leapMonth(lunYear) != 0 && lunMonth > leapMonth(lunYear)) {
			days += leapDays(lunYear) //lunYear年闰月天数
		}
		if (isLeap) {
			days += monthDays(lunYear, lunMonth) //lunYear年lunMonth月 闰月
		}
		days += lunDay
		days -= 1
		val cal = Calendar.getInstance()
		cal[Calendar.YEAR] = 1900
		cal[Calendar.MONTH] = 0
		cal[Calendar.DAY_OF_MONTH] = 31
		cal.add(Calendar.DATE, days)
		/*
	    Date date=cal.getTime();
	    int year_c = cal.get(Calendar.YEAR);
	    int month_c = cal.get(Calendar.MONTH)+1;
	    int day_c = cal.get(Calendar.DAY_OF_MONTH);
	    */
		return cal
	}

	/**
	 * 计算星座
	 */
	fun getConstellation(month: Int, day: Int): String {
		val constellation: String = when (month * 100 + day) {
			in 120..218 -> {
				constellations[1]
			}
			in 219..320 -> {
				constellations[2]
			}
			in 321..419 -> {
				constellations[3]
			}
			in 420..520 -> {
				constellations[4]
			}
			in 521..620 -> {
				constellations[5]
			}
			in 621..721 -> {
				constellations[6]
			}
			in 722..822 -> {
				constellations[7]
			}
			in 823..922 -> {
				constellations[8]
			}
			in 923..1022 -> {
				constellations[9]
			}
			in 1023..1121 -> {
				constellations[10]
			}
			in 1122..1221 -> {
				constellations[11]
			}
			else -> {
				constellations[0]
			}
		}
		return constellation
	}

	/**
	 * 获取公历对应的节假日或二十四节气
	 */
	fun getFestival(calendar: Calendar = Calendar.getInstance()): String {
		val year = calendar[Calendar.YEAR]
		val month = calendar[Calendar.MONTH] + 1
		val day = calendar[Calendar.DATE]

		//农历节假日
		val lunarDateINT = getLunarDateINT(year, month, day)
		val lunYear = lunarDateINT / 10000
		val lunMonth = (lunarDateINT % 10000) / 100
		val lunDay = (lunarDateINT - lunYear * 10000 - lunMonth * 100)
		for (i in lunarHoliday.indices) {
			// 返回农历节假日名称
			val ld = lunarHoliday[i].split(" ").toTypedArray()[0] // 节假日的日期
			val ldv = lunarHoliday[i].split(" ").toTypedArray()[1] // 节假日的名称
			var lmonthV = lunMonth.toString() + ""
			var ldayV = lunDay.toString() + ""
			if (month < 10) {
				lmonthV = "0$lunMonth"
			}
			if (day < 10) {
				ldayV = "0$lunDay"
			}
			val lmd = lmonthV + ldayV
			if (ld.trim { it <= ' ' } == lmd.trim { it <= ' ' }) {
				return ldv
			}
		}

		//公历节假日
		for (i in solarHoliday.indices) {
			if (i == solarHoliday.size && year < 1893 //1893年前没有此节日
				|| i + 3 == solarHoliday.size && year < 1999 || i + 6 == solarHoliday.size && year < 1942 || i + 10 == solarHoliday.size && year < 1949 || i == 19 && year < 1921 || i == 20 && year < 1933 || i == 22 && year < 1976
			) {
				break
			}
			// 返回公历节假日名称
			val sd = solarHoliday[i].split(" ").toTypedArray()[0] // 节假日的日期
			val sdv = solarHoliday[i].split(" ").toTypedArray()[1] // 节假日的名称
			var smonthV = month.toString() + ""
			var sdayV = day.toString() + ""
			if (month < 10) {
				smonthV = "0$month"
			}
			if (day < 10) {
				sdayV = "0$day"
			}
			val smd = smonthV + sdayV
			if (sd.trim { it <= ' ' } == smd.trim { it <= ' ' }) {
				return sdv
			}
		}
		val b = getDateOfSolarTerms(year, month)
		if (day == b / 100) {
			return sTermInfo[(month - 1) * 2]
		} else if (day == b % 100) {
			return sTermInfo[(month - 1) * 2 + 1]
		}
		return ""
	}

	/**
	 * 获取农历日期
	 *
	 *  @param isNeedYear 是否需要农历年份
	 */
	fun getLunarString(calendar: Calendar = Calendar.getInstance(), isNeedYear: Boolean = false): String {
		val year = calendar[Calendar.YEAR]
		val month = calendar[Calendar.MONTH] + 1
		val day = calendar[Calendar.DATE]

		val lunarDateINT = getLunarDateINT(year, month, day)
		val lYear = lunarDateINT / 10000
		val lMonth = (lunarDateINT % 10000) / 100
		val lDay = lunarDateINT - lYear * 10000 - lMonth * 100
		val lunY = cyclical(year, month, day) + "年"
		var lunM = ""
		val testMonth = getSunDate(lYear, lMonth, lDay, false)[Calendar.MONTH] + 1
		if (testMonth != month) {
			lunM = "闰"
		}
		lunM += chineseNumber[(lMonth - 1) % 12] + "月"
		//int leap = leapMonth(lYear);
		val lunD = getChinaDayString(lDay)
		val term2 = sTerm(year, 2) //立春
		val animalsYear = if (month > 2 || month == 2 && day >= term2) {
			year
		} else {
			year - 1
		}
		val festival = getFestival(calendar)
		// return lunY + "-" + lunM + "-" + lunD + "-" + animalsYear(animalsYear)
		// return "农历 $lunM$lunD $festival"

		return if (isNeedYear) "$lunY$lunM$lunD" else "$lunM$lunD"
	}

	/**
	 * 返回农历 y年时的生肖
	 */
	fun animalsYear(year: Int): String {
		val animals = arrayOf(
			"鼠", "牛", "虎", "兔", "龙", "蛇",
			"马", "羊", "猴", "鸡", "狗", "猪"
		)
		return animals[(year - 4) % 12] + "年"
	}

}
