#  保证接口返回数据类 PublicResp 不被混淆
-keep class com.hl.api.PublicResp
# 保证继承自 PublicResp 的子类不被混淆
-keep class * extends com.hl.api.PublicResp{
	*;
}