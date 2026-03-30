package com.app.dialer.core.di

import com.app.dialer.data.repository.CallLogRepositoryImpl
import com.app.dialer.data.repository.ContactRepositoryImpl
import com.app.dialer.domain.repository.CallLogRepository
import com.app.dialer.domain.repository.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds repository interfaces to their concrete implementations.
 *
 * DAO `@Provides` bindings have been moved to [DatabaseModule] which owns the
 * [com.app.dialer.data.local.AppDatabase] singleton and all DAO derivations.
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
}
