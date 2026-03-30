package com.app.dialer.data.local

/**
 * Superseded by [AppDatabase] (Prompt 2A).
 *
 * [AppDatabase] consolidates all entities (RecentCallEntity, CallLogEntity,
 * ContactEntity) into a single Room database and is provided as a Hilt
 * singleton via [com.app.dialer.core.di.DatabaseModule]. This file is kept
 * as a tombstone to preserve history; it must not be referenced in new code.
 */
@Deprecated(
    message = "Use AppDatabase instead. DialerDatabase is superseded as of Prompt 2A.",
    replaceWith = ReplaceWith("AppDatabase", "com.app.dialer.data.local.AppDatabase"),
    level = DeprecationLevel.ERROR
)
object DialerDatabaseTombstone
