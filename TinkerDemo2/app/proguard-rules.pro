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
-keep public class * implements com.tencent.tinker.loader.app.ApplicationLifeCycle {
    <init>(...);
    void onBaseContextAttached(android.content.Context);
}
-keep public class * extends com.tencent.tinker.loader.TinkerLoader {
    <init>(...);
}
-keep public class * extends android.app.Application {
     <init>();
     void attachBaseContext(android.content.Context);
}
-keep class com.tencent.tinker.loader.TinkerTestAndroidNClassLoader {
    <init>(...);
}
-keep class com.iflytek.elpmobile.smartlearning.ThisApplication {
    <init>(...);
}
-keep class com.tencent.tinker.loader.** {
         <init>(...);
}