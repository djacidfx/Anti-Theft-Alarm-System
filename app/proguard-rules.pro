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


-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}

-keep class com.example.app.json.** { *; }




-keep class com.google.gson.examples.android.model.** { <fields>; }


-keep class com.battery.TheftAlerm.chargedetect.*
-keep class com.battery.TheftAlerm.FullBatteryHealth.*
-keep class com.battery.TheftAlerm.HeadphoneAlerm.*
-keep class com.battery.TheftAlerm.intruder.*
-keep class com.battery.TheftAlerm.intruder.androidhiddencamera*
-keep class com.battery.TheftAlerm.intruder.androidhiddencamera.config*
-keep class com.battery.TheftAlerm.MotionSensor.*
-keep class com.battery.TheftAlerm.Preferences*
-keep class com.battery.TheftAlerm.utils*
-keep class com.battery.TheftAlerm.wifi.*
-keep class com.battery.TheftAlerm.Manifest.*
-keep class com.battery.TheftAlerm.MyApplication.*
-keep class com.battery.TheftAlerm.PinActivity.*
-keep class com.battery.TheftAlerm.Settings.*
-keep class com.battery.TheftAlerm.Splash.*

-keep class com.battery.TheftAlerm.R$raw { *; }


