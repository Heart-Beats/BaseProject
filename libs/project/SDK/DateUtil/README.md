# DateUtil — 日期工具模块

## 模块概述

`date-util` 提供日期格式化、农历转换、时间计算、Calendar 操作等丰富的日期时间处理扩展。

**模块坐标**: `com.hl.dateutil`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 对外接口

```kotlin
// 格式化
fun Date.toFormatString(@DatePattern pattern = YMD_HMS): String
fun String.toDate(@DatePattern pattern): Date

// 农历
fun Calendar.toLunarString(isNeedYear: Boolean = false): String
object LunarUtil {
    fun getLunarString(calendar: Calendar, isNeedYear: Boolean = false): String
    fun getFestival(calendar: Calendar): String          // 节气/节日
    fun getConstellation(month: Int, day: Int): String   // 星座
}

// 时间差
infix fun Date.differHours(other: Date): Float
fun Date.compareValueTo(other: Date, betweenValue: Long, unit: TimeUnit): Boolean

// 时间边界
fun Calendar.toDayFirst(): Calendar    // 当天 00:00:00
fun Calendar.toDayLast(): Calendar     // 当天 23:59:59
fun Calendar.toMonthFirst(): Calendar  // 当月第一天
fun Calendar.toMonthLast(): Calendar   // 当月最后一天

// 快捷属性
val nowDateTimeString: String           // 当前日期时间字符串
fun nowDayOfWeekString(): String        // 当前星期
fun nowLunarDateString(): String        // 当前农历
```

## 构建与测试

```bash
./gradlew :date-util:assemble
```

## 使用示例

```kotlin
val now = Date()
now.toFormatString()                    // "2024-01-15 14:30:00"
now.differHours(targetDate)             // 3.5
Calendar.getInstance().toLunarString()  // "腊月初五"
LunarUtil.getConstellation(10, 24)      // "天蝎座"
```
