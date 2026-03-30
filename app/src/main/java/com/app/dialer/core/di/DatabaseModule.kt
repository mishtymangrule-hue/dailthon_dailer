package com.app.dialer.core.di

import android.content.Context
import androidx.room.Room
import com.app.dialer.core.utils.Constants
import com.app.dialer.data.local.DialerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides application-scoped infrastructure dependencies:
 * - Room database instance
 * - DAO instances derived from the database
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDialerDatabase(
        @ApplicationContext context: Context
    ): DialerDatabase {
        return Room.databaseBuilder(
            context,
            DialerDatabase::class.java,
            Constants.DATABASE_NAME
        )
            // dropAllTables = false: only drops tables belonging to tracked entities,
            // preserving any app-managed tables (e.g. FTS) on destructive migration.
            // Room 2.6.0+ requires an explicit boolean; the no-arg overload is deprecated.
            .fallbackToDestructiveMigration(dropAllTables = false)
            .build()
    }
}
