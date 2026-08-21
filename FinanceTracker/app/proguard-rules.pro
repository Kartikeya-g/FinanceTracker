# Add project specific ProGuard rules here.
-keep class com.financetracker.app.data.model.** { *; }
-keepattributes *Annotation*

# Room
-keep class * extends androidx.room.RoomDatabase
