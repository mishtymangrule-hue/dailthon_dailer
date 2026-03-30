package com.app.dialer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.app.dialer.data.model.CallLogEntity
import com.app.dialer.data.model.ContactEntity
import com.app.dialer.data.model.RecentCallEntity
import com.app.dialer.domain.model.RecentCallType

// ─── Type converter ───────────────────────────────────────────────────────────

/**
 * Room [TypeConverter] for [RecentCallType].
 *
 * Persists enum values as their [name] strings (e.g. "MISSED") so schema
 * values are human-readable and immune to enum ordinal changes.
 */
class CallTypeConverter {

    @TypeConverter
    fun fromRecentCallType(value: RecentCallType): String = value.name

    @TypeConverter
    fun toRecentCallType(value: String): RecentCallType =
        runCatching { RecentCallType.valueOf(value) }.getOrDefault(RecentCallType.INCOMING)
}

// ─── Database ─────────────────────────────────────────────────────────────────

/**
 * The main Room database for the Dialer app.
 *
 * ### Entity inventory
 * | Entity              | Table name    | Module        |
 * |---------------------|--------------|---------------|
 * | [RecentCallEntity]  | recent_calls | Dialer (2A)   |
 * | [CallLogEntity]     | call_logs    | Core (P1)     |
 * | [ContactEntity]     | contacts     | Core (P1)     |
 *
 * ### Schema export
 * [exportSchema] is set to **true** so Room generates a JSON schema file at
 * compile time. Configure the output directory in `app/build.gradle.kts`:
 * ```kotlin
 * ksp {
 *     arg("room.schemaLocation", "$projectDir/schemas")
 * }
 * ```
 * Commit the generated schemas to version control to track migrations over time.
 *
 * ### Migrations
 * `fallbackToDestructiveMigration(dropAllTables = false)` is active during
 * development. Before releasing to production, replace this with explicit
 * [androidx.room.migration.Migration] objects for each schema version change.
 *
 * ### Hilt provisioning
 * This class is provided as a `@Singleton` via `DatabaseModule` in the
 * `core/di/` package (implemented in Prompt 2B). The module binds both this
 * database and its DAOs into the Hilt component graph.
 */
@Database(
    entities = [
        RecentCallEntity::class,
        CallLogEntity::class,
        ContactEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(CallTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recentCallDao(): RecentCallDao

    abstract fun callLogDao(): CallLogDao

    abstract fun contactDao(): ContactDao

    companion object {
        private const val DATABASE_NAME = "dialer_app.db"

        /**
         * Builds and returns the [AppDatabase] singleton.
         *
         * This factory is called from the Hilt `DatabaseModule`; call sites should
         * not invoke it directly.
         *
         * @param context Application context — use [androidx.hilt.android.qualifiers.ApplicationContext].
         */
        // Room 2.6.1: the no-arg fallbackToDestructiveMigration() is deprecated but the
        // dropAllTables parameter overload was added in a later Room version.
        @Suppress("DEPRECATION")
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
