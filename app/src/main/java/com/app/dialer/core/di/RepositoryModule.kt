package com.app.dialer.core.di

import com.app.dialer.data.local.CallLogDao
import com.app.dialer.data.local.ContactDao
import com.app.dialer.data.local.DialerDatabase
import com.app.dialer.data.repository.CallLogRepositoryImpl
import com.app.dialer.data.repository.ContactRepositoryImpl
import com.app.dialer.domain.repository.CallLogRepository
import com.app.dialer.domain.repository.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds repository interfaces to their concrete implementations
 * and provides DAO instances from the Room database.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCallLogRepository(
        impl: CallLogRepositoryImpl
    ): CallLogRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        impl: ContactRepositoryImpl
    ): ContactRepository

    companion object {

        @Provides
        @Singleton
        fun provideCallLogDao(database: DialerDatabase): CallLogDao =
            database.callLogDao()

        @Provides
        @Singleton
        fun provideContactDao(database: DialerDatabase): ContactDao =
            database.contactDao()
    }
}
