package com.app.dialer.core.di

import com.app.dialer.data.repository.CallLogRepositoryImpl
import com.app.dialer.data.repository.ContactRepositoryImpl
import com.app.dialer.data.repository.DialerContactRepositoryImpl
import com.app.dialer.data.repository.DialerRepositoryImpl
import com.app.dialer.domain.repository.CallLogRepository
import com.app.dialer.domain.repository.ContactRepository
import com.app.dialer.domain.repository.DialerContactRepository
import com.app.dialer.domain.repository.DialerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds repository interfaces to their concrete implementations.
 *
 * DAO `@Provides` bindings live in [DatabaseModule], which owns the
 * [com.app.dialer.data.local.AppDatabase] singleton and all DAO derivations.
 *
 * | Interface                  | Implementation                    | Notes          |
 * |----------------------------|-----------------------------------|----------------|
 * | [CallLogRepository]        | [CallLogRepositoryImpl]           | P1             |
 * | [ContactRepository]        | [ContactRepositoryImpl]           | P1             |
 * | [DialerRepository]         | [DialerRepositoryImpl]            | 2B             |
 * | [DialerContactRepository]  | [DialerContactRepositoryImpl]     | 2B stub → 2C   |
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

    @Binds
    @Singleton
    abstract fun bindDialerRepository(
        impl: DialerRepositoryImpl
    ): DialerRepository

    @Binds
    @Singleton
    abstract fun bindDialerContactRepository(
        impl: DialerContactRepositoryImpl
    ): DialerContactRepository
}
