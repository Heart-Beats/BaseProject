# 可序列化相关实体类不进行混淆
-keepclasseswithmembernames class * implements java.io.Serializable{*;}          #保持 Serializable 相关子类不被混淆
-keepclasseswithmembernames class * implements android.os.Parcelable{*;}         #保持 Parcelable 相关子类不被混淆

# BaseViewModelDelegate 中使用了反射获取当前 ViewModelStore 中 已存放的 viewModel
-keepclasseswithmembernames class androidx.lifecycle.ViewModelStore{
	<fields>;
}