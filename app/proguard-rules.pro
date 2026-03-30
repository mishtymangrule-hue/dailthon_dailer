# Hilt rules
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ActivityComponentManager { *; }
-dontwarn dagger.hilt.**
-keepclasseswithmembernames class * {
    @javax.inject.Inject <fields>;
}
-keepclasseswithmembernames class * {
    @javax.inject.Inject <init>(...);
}

# Room rules
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.**

# Navigation component
-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.navigation.NavArgs { *; }
-keepnames @androidx.navigation.NavDestination class *

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.app.dialer.**$$serializer { *; }
-keepclassmembers class com.app.dialer.** {
    *** Companion;
}
-keepclasseswithmembers class com.app.dialer.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# Coil
-dontwarn okhttp3.**
-dontwarn okio.**

# General Android
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.telecom.InCallService
-keep public class * extends android.telecom.CallScreeningService
