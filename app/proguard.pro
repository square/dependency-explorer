-dontobfuscate
-verbose

-keep class com.setup.ide.MainKt { *; }

### These packages references other classes which may or may not be on the runtime classpath. It's fine,
### ignore the warnings.

# Suppress "Maybe this is..." logspam
-dontnote **

# Resolves "can't find referenced class org.jetbrains.annotations.ApiStatus..."
-dontwarn org.jetbrains.annotations.**

# Resolve "can't find referenced method 'double toDouble-impl(long,java.util.concurrent.TimeUnit)'..."
# Not sure *why* it can't, though.
-dontwarn kotlin.time.Duration

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep annotations for serialization
-keepattributes *Annotation*, Signature, Exception

## Keep class members of enums -- they confuse proguard.
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontwarn android.annotation.SuppressLint

# Keep the commandline
-keep class picocli.CommandLine { *; }
-keep class picocli.CommandLine$* { *; }

# Keep all commands -- pico uses reflection to load them.
-keepclasseswithmembers class * {
 @picocli.CommandLine$Command *;
}
-keep @picocli.CommandLine$Command class *