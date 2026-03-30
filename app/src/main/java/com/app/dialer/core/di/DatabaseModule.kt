package com.app.dialer.core.di

import android.content.Context
import com.app.dialer.data.local.AppDatabase
import com.app.dialer.data.local.CallLogDao
import com.app.dialer.data.local.ContactDao
import com.app.dialer.data.local.RecentCallDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides application-scoped Room database dependencies.
 *
 * [AppDatabase] supersedes the previous [com.app.dialer.data.local.DialerDatabase]
 * introduced in Prompt 1. It consolidates all entities (RecentCallEntity,
 * CallLogEntity, ContactEntity) and includes the [com.app.dialer.data.local.CallTypeConverter]
 * for [com.app.dialer.domain.model.CallType] ↔ String persistence.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = AppDatabase.create(context)

    @Provides
    @Singleton
    fun provideRecentCallDao(db: AppDatabase): RecentCallDao = db.recentCallDao()

    @Provides
    @Singleton
    fun provideCallLogDao(db: AppDatabase): CallLogDao = db.callLogDao()

    @Provides
    @Singleton
    fun provideContactDao(db: AppDatabase): ContactDao = db.contactDao()
}
