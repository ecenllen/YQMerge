# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.android.cloud.speedup.**{*;}
#-keep class com.gta.zssx.pub.http.**{*;}
-dontwarn com.android.cloud.speedup.**

-keep class com.baidu.mobads.**{*;}
#-dontwarm com.baidu.mobads.**

-keep class com.qq.** {*;}

#umeng
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

# rx
#-dontwarn rx.**
#-keepclassmembers class rx.** { *; }

# retrolambda
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#sharesdk混淆
#-keep class cn.sharesdk.**{*;}
#-keep class com.sina.**{*;}
#-keep class **.R$* {*;}
#-keep class **.R{*;}
#-dontwarn cn.sharesdk.**
#-dontwarn **.R$*
#-dontwarn com.tencent.**
#-keep class com.tencent.** {*;}

#Universal Image Loader
#-keep class com.nostra13.universalimageloader.** { *; }
#-keepattributes Signature

#butterknife混淆
#-keep class butterknife.** { *; }
#-dontwarn butterknife.internal.**
#-keep class **$$ViewBinder { *; }
#-keepclasseswithmembernames class * {
#   @butterknife.* <fields>;
#}
#-keepclasseswithmembernames class * {
#   @butterknife.* <methods>;
#}

#Gson
# removes such information by default, so configure it to keep all of it.
#-keepattributes Signature
## Gson specific classes
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
## Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { *; }
#-keep class com.google.gson.** { *;}
##这句非常重要，主要是滤掉 com.demo.demo.bean包下的所有.class文件不进行混淆编译,com.demo.demo是你的包名
#-keep class com.demo.demo.bean.** {*;}
#-dontwarn com.google.gson.jpush.internal.**


-keep class **.R$* { *; }  #保持R文件不被混淆，否则，你的反射是获取不到资源id的

-keep public class * extends android.view.View {  #保持自定义控件指定规则的方法不被混淆
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclassmembers enum * {  #保持枚举 enum 不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {  #保持 Parcelable 不被混淆（aidl文件不能去混淆）
    public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable #需要序列化和反序列化的类不能被混淆（注：Java反射用到的类也不能被混淆）

-keepclassmembers class * implements java.io.Serializable { #保护实现接口Serializable的类中，指定规则的类成员不被混淆
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclasseswithmembernames class * {
    native <methods>;  #保持 native 的方法不去混淆
}

#极光推送
#-dontoptimize
#-dontpreverify
#
#-dontwarn cn.jpush.**
#-keep class cn.jpush.** { *; }
#
#-dontwarn cn.jiguang.**
#-keep class cn.jiguang.** { *; }
#-dontwarn com.google.**
#-keep class com.google.gson.** {*;}
#-keep class com.google.protobuf.** {*;}

