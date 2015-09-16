# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Studio_SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes Exceptions,InnerClasses,Signature


-keep class javax.mail.**{*;}

-dontwarn javax.activation.**
-keep class javax.activation.**{*;}

-keep class javax.activation.** implements myjava.awt.datatransfer.Transferable {*;}

-dontwarn org.apache.**
-keep class org.apache.**{*;}

-dontwarn java.awt.**

-dontwarn javax.security.**
-keep class javax.security.**{*;}

-keep public class com.sun.mail.smtp.**{public *;}

-keep class com.phoenix.devicemonitor.service.MailSender{*;}