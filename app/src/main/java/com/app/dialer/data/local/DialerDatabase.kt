package com.app.dialer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.dialer.data.model.CallLogEntity
import com.app.dialer.data.model.ContactEntity

/**
 * Room database for the Dialer app.
 *
 * Entities and DAOs expand across prompts as modules are added.
 * Version starts at 1; migrations will be added as the schema evolves.
 *
 * Schema export is disabled ([exportSchema] = false) during development.
 * Enable and configure a schema export directory in build.gradle.kts via:
 * ```
 * ksp { arg("room.schemaLocation", "$projectDir/schemas") }
 * ```
 * before enabling exportSchema = true for production builds.
 */
@Database(
    entities = [
        CallLogEntity::class,
        ContactEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DialerDatabase : RoomDatabase() {

    abstract fun callLogDao(): CallLogDao

    abstract fun contactDao(): ContactDao
}
